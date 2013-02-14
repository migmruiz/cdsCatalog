package br.study.ebah.miguel.cdsCatalog.repo.impl.c3p0;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;

import br.study.ebah.miguel.cdsCatalog.actions.Writable;
import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.ArtistImpl;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryType;
import br.study.ebah.miguel.cdsCatalog.sql.ConnectionFactory;
import br.study.ebah.miguel.cdsCatalog.sql.MySQL_C3P0ConnectionFactory;
import br.study.ebah.miguel.cdsCatalog.sql.SQLDBNoDataException;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class MySQL_C3P0ArtistRepository implements Repository<Artist> {
	private static final Cache<Long, Artist> cache = CacheBuilder.newBuilder()
			.build();
	private static final ConnectionFactory connFact = MySQL_C3P0ConnectionFactory
			.getInstance();

	@Override
	public void initialize() {
	}

	@Override
	public Artist getById(@Nonnull final Long id) throws RepositoryException {
		try {
			return cache.get(id, new Callable<Artist>() {
				@Override
				public Artist call() throws Exception {
					Preconditions.checkNotNull(id, "id cannot be null");
					try (final Connection con = connFact.getConnection()) {
						final Artist persistentArtist = pullArtist(id, con);
						cache.put(id, persistentArtist);
						return persistentArtist;
					}
				}
			});
		} catch (ExecutionException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public Artist save(@Nonnull final Artist artist) throws RepositoryException {
		try (final Connection con = connFact.getConnection()) {
			final Long id;
			if (artist.isTransient()) {
				id = insertArtist(artist, con);
			} else {
				id = artist.getId();
				updateArtist(artist, con);
				cache.invalidate(id);
			}
			return getById(id);
		} catch (SQLException | ExecutionException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public void delete(@Nonnull final Artist artist) throws RepositoryException {
		try (final Connection con = connFact.getConnection()) {
			deleteArtist(artist, con);
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
		cache.invalidate(artist.getId());
	}

	final static Artist pullArtist(@Nonnull final Long id, Connection con)
			throws SQLException, ExecutionException, SQLDBNoDataException {
		try (final PreparedStatement idStmt = con
				.prepareStatement("SELECT * FROM artist WHERE id_artist=?");
				final PreparedStatement mainDiscStmt = con
						.prepareStatement("SELECT * FROM disc"
								+ " WHERE id_mainArtist=?");
				final PreparedStatement workingOnDiscsStmt = con
						.prepareStatement("SELECT * FROM `disc_artist-workingOn`"
								+ " WHERE id_artist=?");
				final PreparedStatement artistsSongStmt = con
						.prepareStatement("SELECT * FROM `artist_song`"
								+ " WHERE id_artist=?");) {

			idStmt.setLong(1, id.longValue());
			final ArtistImpl artist;
			try (final ResultSet rs = idStmt.executeQuery()) {
				if (rs.first()) {
					final java.sql.Date birthdaySQL = rs.getDate("birthday");
					if (birthdaySQL == null) {
						artist = new ArtistImpl(rs.getString("name"),
								RepositoryType.MySQL);
					} else {
						artist = new ArtistImpl(rs.getString("name"), new Date(
								birthdaySQL.getTime()), RepositoryType.MySQL);
					}
				} else {
					throw new SQLDBNoDataException("no data on artist table");
				}
			}

			final Writable<Disc> discWritableArtist = artist
					.asWritable(Disc.class);

			workingOnDiscsStmt.setLong(1, id.longValue());
			try (final ResultSet rs = workingOnDiscsStmt.executeQuery()) {
				while (rs.next()) {
					discWritableArtist.add(rs.getLong("id_disc"));
				}
			}

			mainDiscStmt.setLong(1, id.longValue());
			try (final ResultSet rs = mainDiscStmt.executeQuery()) {
				while (rs.next()) {
					artist.setMain(rs.getLong("id_disc"));
				}
			}

			final Writable<Song> songWritableArtist = artist
					.asWritable(Song.class);

			artistsSongStmt.setLong(1, id.longValue());
			try (final ResultSet rs = artistsSongStmt.executeQuery()) {
				while (rs.next()) {
					songWritableArtist.add(rs.getLong("id_song"));
				}
			}

			artist.setId(id);

			return artist;
		}
	}

	final static long insertArtist(@Nonnull Artist artist, Connection con)
			throws SQLException, RepositoryException, ExecutionException {
		try (final PreparedStatement insertArtistStmt = con.prepareStatement(
				"INSERT INTO artist (name,birthday) VALUES (?,?);",
				Statement.RETURN_GENERATED_KEYS);
				final PreparedStatement insertWorkingOnStmt = con
						.prepareStatement("INSERT INTO `disc_artist-workingOn` (id_disc, id_artist)"
								+ " VALUES (?,?);");
				final PreparedStatement insertArtistSongStmt = con
						.prepareStatement("INSERT INTO `artist_song` (id_artist, id_song)"
								+ " VALUES (?,?);")) {

			insertArtistStmt.setString(1, artist.getName());
			insertArtistStmt.setDate(2, new java.sql.Date(artist.getBirthday()
					.getTime()));
			final int rows = insertArtistStmt.executeUpdate();

			for (final Disc disc : artist.getKnownDiscs()) {
				insertWorkingOnStmt.setLong(1, disc.getId());
				insertWorkingOnStmt.setLong(2, artist.getId());
				insertWorkingOnStmt.executeUpdate();
			}
			for (final Song song : artist.getKnownSongs()) {
				insertArtistSongStmt.setLong(1, artist.getId());
				insertArtistSongStmt.setLong(2, song.getId());
				insertArtistSongStmt.executeUpdate();
			}

			try (final ResultSet rs = insertArtistStmt.getGeneratedKeys()) {
				final ResultSetMetaData metaData = rs.getMetaData();
				if (rows == 1 && metaData.getColumnCount() == 1) {
					rs.first();
					return rs.getLong(1);
				} else {
					throw new SQLException("no rows affected");
				}
			}
		}
	}

	final static void updateArtist(@Nonnull Artist artist, Connection con)
			throws SQLException, RepositoryException, ExecutionException {
		try (final PreparedStatement updateArtistStmt = con
				.prepareStatement("UPDATE artist SET name=?, birthday=?"
						+ " WHERE id_artist=?;");
				final PreparedStatement workingOnDiscsStmt = con
						.prepareStatement("SELECT * FROM `disc_artist-workingOn`"
								+ " WHERE id_artist=?");
				final PreparedStatement insertWorkingOnStmt = con
						.prepareStatement("INSERT INTO `disc_artist-workingOn` (id_disc, id_artist)"
								+ " VALUES (?,?);");
				final PreparedStatement artistsSongStmt = con
						.prepareStatement("SELECT * FROM `artist_song`"
								+ " WHERE id_artist=?");
				final PreparedStatement insertArtistSongStmt = con
						.prepareStatement("INSERT INTO `artist_song` (id_artist, id_song)"
								+ " VALUES (?,?);")) {

			updateArtistStmt.setString(1, artist.getName());
			updateArtistStmt.setDate(2, new java.sql.Date(artist.getBirthday()
					.getTime()));
			updateArtistStmt.setLong(3, artist.getId());
			final int rows = updateArtistStmt.executeUpdate();
			if (rows != 1) {
				throw new SQLException("no rows affected");
			}
			// TODO update many-to-many tables

			final Set<Disc> knownDiscs = new HashSet<>();
			for (final Disc disc : artist.getKnownDiscs()) {
				knownDiscs.add(disc);
			}

			workingOnDiscsStmt.setLong(1, artist.getId());
			try (final ResultSet rs = workingOnDiscsStmt.executeQuery()) {
				while (rs.next()) {
					knownDiscs.remove(rs.getLong("id_disc"));
				}
			}

			for (final Disc disc : knownDiscs) {
				insertWorkingOnStmt.setLong(1, disc.getId());
				insertWorkingOnStmt.setLong(2, artist.getId());
				insertWorkingOnStmt.executeUpdate();
			}

			final Set<Song> knownSongs = new HashSet<>();
			for (final Song song : artist.getKnownSongs()) {
				knownSongs.add(song);
			}

			artistsSongStmt.setLong(1, artist.getId());
			try (final ResultSet rs = artistsSongStmt.executeQuery()) {
				while (rs.next()) {
					knownSongs.remove(rs.getLong("id_song"));
				}
			}

			for (final Song song : knownSongs) {
				insertArtistSongStmt.setLong(1, artist.getId());
				insertArtistSongStmt.setLong(2, song.getId());
				insertArtistSongStmt.executeUpdate();
			}
		}
	}

	final static void deleteArtist(@Nonnull Artist artist, Connection con)
			throws SQLException {
		try (final PreparedStatement deleteArtistStmt = con
				.prepareStatement("DELETE FROM artist" + " WHERE id_artist=?;")) {

			deleteArtistStmt.setLong(1, artist.getId());
			final int rows = deleteArtistStmt.executeUpdate();
			if (rows != 1) {
				throw new SQLException("no rows affected");
			}
		}
	}

	/*
	 * 
	 * @see java.lang.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		cache.cleanUp();
	}

}
