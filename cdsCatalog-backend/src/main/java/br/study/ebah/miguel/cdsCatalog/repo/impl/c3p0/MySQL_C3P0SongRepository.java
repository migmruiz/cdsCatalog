package br.study.ebah.miguel.cdsCatalog.repo.impl.c3p0;

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
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.SongImpl;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryType;
import br.study.ebah.miguel.cdsCatalog.sql.ConnectionFactory;
import br.study.ebah.miguel.cdsCatalog.sql.MySQL_C3P0ConnectionFactory;
import br.study.ebah.miguel.cdsCatalog.sql.SQLDBNoDataException;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class MySQL_C3P0SongRepository implements Repository<Song> {
	private static final Cache<Long, Song> cache;

	private static final ConnectionFactory connFact;

	static {
		try {
			cache = CacheBuilder.newBuilder().build();
			connFact = MySQL_C3P0ConnectionFactory.getInstance();

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
					try (Connection con = connFact.getConnection()) {
						Song persistentSong = pullSong(id, con);
						cache.put(id, persistentSong);
						return persistentSong;
					}
				}
			});
		} catch (ExecutionException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public Song save(@Nonnull final Song song) throws RepositoryException {

		try (Connection con = connFact.getConnection()) {
			final Long id;
			if (song.isTransient()) {
				id = insertSong(song, con);
			} else {
				id = song.getId();
				updateSong(song, con);
				cache.invalidate(id);
			}
			return getById(id);
		} catch (SQLException | ExecutionException e) {
			throw new RepositoryException(e);
		}

	}

	@Override
	public void delete(@Nonnull final Song song) throws RepositoryException {
		try (Connection con = connFact.getConnection()) {
			deleteSong(song, con);
			cache.invalidate(song.getId());
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
	}

	private final static Song pullSong(@Nonnull final Long id, Connection con)
			throws SQLException, ExecutionException, SQLDBNoDataException {
		try (PreparedStatement idStmt = con
				.prepareStatement("SELECT * FROM song WHERE id_song=?");
				PreparedStatement discContainsStmt = con
						.prepareStatement("SELECT * FROM `disc_song-contains`"
								+ " WHERE id_song=?");
				PreparedStatement fromArtistStmt = con
						.prepareStatement("SELECT * FROM artist_song"
								+ " WHERE id_song=?")) {

			idStmt.setLong(1, id.longValue());
			SongImpl song;
			try (ResultSet rs = idStmt.executeQuery()) {
				if (rs.first()) {
					java.sql.Date releaseDateSQL = rs
							.getDate("firstReleaseDate");
					if (releaseDateSQL == null) {
						song = new SongImpl(rs.getString("name"),
								RepositoryType.MySQL);
					} else {
						song = new SongImpl(rs.getString("name"), new Date(
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
	}

	private final static long insertSong(@Nonnull Song song, Connection con)
			throws SQLException, RepositoryException, ExecutionException {
		try (PreparedStatement insertDiscStmt = con.prepareStatement(
				"INSERT INTO song (name,firstReleaseDate,id_composer)"
						+ " VALUES (?,?,?);", Statement.RETURN_GENERATED_KEYS)) {

			insertDiscStmt.setString(1, song.getName());
			insertDiscStmt.setDate(2, new java.sql.Date(song
					.getFirstReleaseDate().getTime()));
			try {
				insertDiscStmt.setLong(3, song.getComposer().getId());
			} catch (RepositoryException e) {
				insertDiscStmt.setLong(3, -1L);
			}
			int rows = insertDiscStmt.executeUpdate();
			try (ResultSet rs = insertDiscStmt.getGeneratedKeys()) {
				ResultSetMetaData metaData = rs.getMetaData();
				if (rows == 1 && metaData.getColumnCount() == 1) {
					rs.first();
					return rs.getLong(1);
				} else {
					throw new SQLException("no rows affected");
				}
			}
		}
		// TODO update/insert into many-to-many tables
	}

	private final static void updateSong(@Nonnull Song song, Connection con)
			throws SQLException, RepositoryException, ExecutionException {
		try (PreparedStatement updateDiscStmt = con
				.prepareStatement("UPDATE song SET name=?, firstReleaseDate=?,"
						+ "id_composer=? WHERE id_song=?;")) {

			updateDiscStmt.setString(1, song.getName());
			updateDiscStmt.setDate(2, new java.sql.Date(song
					.getFirstReleaseDate().getTime()));
			updateDiscStmt.setLong(3, song.getComposer().getId());
			updateDiscStmt.setLong(4, song.getId());
			int rows = updateDiscStmt.executeUpdate();
			if (rows != 1) {
				throw new SQLException("no rows affected");
			}
		}
		// TODO update many-to-many tables
	}

	private final static void deleteSong(@Nonnull Song song, Connection con)
			throws SQLException {
		try (PreparedStatement deleteDiscStmt = con
				.prepareStatement("DELETE FROM song" + " WHERE id_song=?;")) {

			deleteDiscStmt.setLong(1, song.getId());
			int rows = deleteDiscStmt.executeUpdate();
			if (rows != 1) {
				throw new SQLException("no rows affected");
			}
		}

	}

	/*
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		cache.cleanUp();
	}

	@Override
	public void initialize() {
	}

}
