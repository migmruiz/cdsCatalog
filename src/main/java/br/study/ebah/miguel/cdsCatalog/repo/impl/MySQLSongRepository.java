package br.study.ebah.miguel.cdsCatalog.repo.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;

import br.study.ebah.miguel.cdsCatalog.actions.Writable;
import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.PersistentSong;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.TransientSong;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryType;
import br.study.ebah.miguel.cdsCatalog.sql.MySQLConnectionFactory;
import br.study.ebah.miguel.cdsCatalog.sql.SQLDBNoDataException;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class MySQLSongRepository implements Repository<Song> {
	private static final Cache<Long, Song> cache;

	private static final Connection con;
	private static final PreparedStatement idStmt;
	private static final PreparedStatement discContainsStmt;
	private static final PreparedStatement fromArtistStmt;
	private static final PreparedStatement insertDiscStmt;
	private static final PreparedStatement updateDiscStmt;
	private static final PreparedStatement deleteDiscStmt;
	private static final MySQLConnectionFactory connFact;

	static {
		try {
			cache = CacheBuilder.newBuilder().build();
			try {
				try {
					connFact = new MySQLConnectionFactory();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					throw new RuntimeException(e);
				}
				con = connFact.getConnection();
				idStmt = con
						.prepareStatement("SELECT * FROM song WHERE id_song=?");
				discContainsStmt = con
						.prepareStatement("SELECT * FROM `disc_song-contains`"
								+ " WHERE id_song=?");
				fromArtistStmt = con
						.prepareStatement("SELECT * FROM artist_song"
								+ " WHERE id_song=?");
				insertDiscStmt = con.prepareStatement(
						"INSERT INTO song (name,firstReleaseDate,id_composer)"
								+ " VALUES (?,?,?);",
						Statement.RETURN_GENERATED_KEYS);
				updateDiscStmt = con
						.prepareStatement("UPDATE song SET name=?, firstReleaseDate=?,"
								+ "id_composer=? WHERE id_song=?;");
				deleteDiscStmt = con.prepareStatement("DELETE FROM song"
						+ " WHERE id_song=?;");
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	@Override
	public Song getById(@Nonnull final Long id) throws RepositoryException {
		try {
			return cache.get(id, new Callable<Song>() {
				@Override
				public Song call() throws Exception {
					Preconditions.checkNotNull(id, "id cannot be null");
					Preconditions.checkState(!(con.isClosed()),
							"cannot execute query if connection is closed");
					Song transientSong = pullSong(id);
					Song persistentSong = new PersistentSong(transientSong);
					cache.put(id, persistentSong);
					return persistentSong;
				}
			});
		} catch (ExecutionException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public Song save(@Nonnull final Song song) throws RepositoryException {
		Optional<Long> id = Optional.absent();
		try {
			Preconditions
					.checkState(!(con.isClosed() || con.isReadOnly()),
							"cannot execute query if connection is closed or read-only");
			if (song.isTransient()) {
				id = Optional.of(insertSong(song));
			} else {
				updateSong(song);
				cache.invalidate(song.getId());
			}
		} catch (SQLException | ExecutionException e) {
			throw new RepositoryException(e);
		}

		return getById(id.get());
	}

	@Override
	public void delete(@Nonnull final Song song) throws RepositoryException {
		try {
			Preconditions
					.checkState(!(con.isClosed() || con.isReadOnly()),
							"cannot execute query if connection is closed or read-only");

			deleteSong(song);
			cache.invalidate(song.getId());
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
	}

	private final static Song pullSong(@Nonnull final Long id)
			throws SQLException, ExecutionException, SQLDBNoDataException {
		Preconditions
				.checkState(
						!(idStmt.isClosed() || discContainsStmt.isClosed() || fromArtistStmt
								.isClosed()),
						"cannot execute query if statement is closed");

		idStmt.setLong(1, id.longValue());
		TransientSong song;
		try (ResultSet rs = idStmt.executeQuery()) {
			if (rs.first()) {
				java.sql.Date releaseDateSQL = rs.getDate("firstReleaseDate");
				if (releaseDateSQL == null) {
					song = new TransientSong(rs.getString("name"),
							RepositoryType.MySQL);
				} else {
					song = new TransientSong(rs.getString("name"), new Date(
							releaseDateSQL.getTime()), RepositoryType.MySQL);

					song.setComposer(rs.getLong("id_composer"));
				}
			} else {
				throw new SQLDBNoDataException("no data on artist table");
			}
		}
		Writable<Artist> artistWritableDisc = song.asWritable(Artist.class);

		fromArtistStmt.setLong(1, id.longValue());
		try (ResultSet rs = fromArtistStmt.executeQuery()) {
			while (rs.next()) {
				artistWritableDisc.add(rs.getLong("id_artist"));
			}
		}

		Writable<Disc> discWritableDisc = song.asWritable(Disc.class);

		discContainsStmt.setLong(1, id.longValue());
		try (ResultSet rs = discContainsStmt.executeQuery()) {
			while (rs.next()) {
				discWritableDisc.add(rs.getLong("id_disc"));
			}
		}

		song.setId(id);

		return song;
	}

	private final static long insertSong(@Nonnull Song song)
			throws SQLException, RepositoryException, ExecutionException {
		Preconditions.checkState(!(insertDiscStmt.isClosed()),
				"cannot execute query if statement is closed");

		insertDiscStmt.setString(1, song.getName());
		insertDiscStmt.setDate(2, new java.sql.Date(song.getFirstReleaseDate()
				.getTime()));
		insertDiscStmt.setLong(3, song.getComposer().getId());
		int rows = insertDiscStmt.executeUpdate();
		try (ResultSet rs = insertDiscStmt.getGeneratedKeys()) {
			ResultSetMetaData metaData = rs.getMetaData();
			if (rows == 1 && metaData.getColumnCount() == 1) {
				return rs.getLong(metaData.getColumnName(1));
			} else {
				throw new SQLException("no rows affected");
			}
		}
		// TODO update/insert into many-to-many tables
	}

	private final static void updateSong(@Nonnull Song song)
			throws SQLException, RepositoryException, ExecutionException {
		Preconditions.checkState(!(updateDiscStmt.isClosed()),
				"cannot execute query if statement is closed");

		updateDiscStmt.setString(1, song.getName());
		updateDiscStmt.setDate(2, new java.sql.Date(song.getFirstReleaseDate()
				.getTime()));
		updateDiscStmt.setLong(3, song.getComposer().getId());
		updateDiscStmt.setLong(4, song.getId());
		int rows = updateDiscStmt.executeUpdate();
		if (rows != 1) {
			throw new SQLException("no rows affected");
		}
		// TODO update many-to-many tables
	}

	private final static void deleteSong(@Nonnull Song song)
			throws SQLException {
		Preconditions.checkState(!(deleteDiscStmt.isClosed()),
				"cannot execute query if statement is closed");

		deleteDiscStmt.setLong(1, song.getId());
		int rows = deleteDiscStmt.executeUpdate();
		if (rows != 1) {
			throw new SQLException("no rows affected");
		}

	}

	/*
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		insertDiscStmt.close();
		updateDiscStmt.close();
		deleteDiscStmt.close();
		discContainsStmt.close();
		idStmt.close();
		con.close();
	}

}
