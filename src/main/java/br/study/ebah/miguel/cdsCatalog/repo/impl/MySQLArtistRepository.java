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
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.PersistentArtist;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.TransientArtist;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryType;
import br.study.ebah.miguel.cdsCatalog.sql.MySQLConnectionFactory;
import br.study.ebah.miguel.cdsCatalog.sql.SQLDBNoDataException;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class MySQLArtistRepository implements Repository<Artist> {
	private static final Cache<Long, Artist> cache;

	private static final Connection con;
	private static final PreparedStatement idStmt;
	private static final PreparedStatement mainDiscStmt;
	private static final PreparedStatement workingOnDiscsStmt;
	private static final PreparedStatement insertArtistStmt;
	private static final PreparedStatement updateArtistStmt;
	private static final PreparedStatement deleteArtistStmt;
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
						.prepareStatement("SELECT * FROM artist WHERE id_artist=?");
				mainDiscStmt = con.prepareStatement("SELECT * FROM disc"
						+ " WHERE id_mainArtist=?");
				workingOnDiscsStmt = con
						.prepareStatement("SELECT * FROM `disc_artist-workingOn`"
								+ " WHERE id_artist=?");
				insertArtistStmt = con.prepareStatement(
						"INSERT INTO artist (name,birthday) VALUES (?,?);",
						Statement.RETURN_GENERATED_KEYS);
				updateArtistStmt = con
						.prepareStatement("UPDATE artist SET name=?, birthday=?"
								+ " WHERE id_artist=?;");
				deleteArtistStmt = con.prepareStatement("DELETE FROM artist"
						+ " WHERE id_artist=?;");
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	@Override
	public Artist getById(@Nonnull final Long id) throws RepositoryException {
		try {
			return cache.get(id, new Callable<Artist>() {
				@Override
				public Artist call() throws Exception {
					Preconditions.checkNotNull(id, "id cannot be null");
					Preconditions.checkState(!(con.isClosed()),
							"cannot execute query if connection is closed");
					Artist transientArtist = pullArtist(id);
					Artist persistentArtist = new PersistentArtist(
							transientArtist);
					cache.put(id, persistentArtist);
					return persistentArtist;
				}
			});
		} catch (ExecutionException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public Artist save(@Nonnull final Artist artist) throws RepositoryException {

		Optional<Long> id = Optional.absent();
		try {
			Preconditions
					.checkState(!(con.isClosed() || con.isReadOnly()),
							"cannot execute query if connection is closed or read-only");
			if (artist.isTransient()) {
				id = Optional.of(insertArtist(artist));
			} else {
				updateArtist(artist);
				cache.invalidate(artist.getId());
			}
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}

		return getById(id.get());
	}

	@Override
	public void delete(@Nonnull final Artist artist) throws RepositoryException {
		try {
			Preconditions
					.checkState(!(con.isClosed() || con.isReadOnly()),
							"cannot execute query if connection is closed or read-only");

			deleteArtist(artist);
			cache.invalidate(artist.getId());
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
	}

	private final Artist pullArtist(@Nonnull final Long id)
			throws SQLException, ExecutionException {
		Preconditions
				.checkState(
						!(idStmt.isClosed() || mainDiscStmt.isClosed() || workingOnDiscsStmt
								.isClosed()),
						"cannot execute query if statement is closed");

		idStmt.setLong(1, id.longValue());
		TransientArtist artist;
		try (ResultSet rs = idStmt.executeQuery()) {
			if (rs.first()) {
				java.sql.Date birthdaySQL = rs.getDate("birthday");
				if (birthdaySQL == null) {
					artist = new TransientArtist(rs.getString("name"),
							RepositoryType.MySQL);
				} else {
					artist = new TransientArtist(rs.getString("name"),
							new Date(birthdaySQL.getTime()),
							RepositoryType.MySQL);
				}
			} else {
				throw new SQLDBNoDataException("no data on artist table");
			}
		}
		artist.setId(id);

		Writable<Disc> discWritableArtist = artist.asWritable(Disc.class);

		workingOnDiscsStmt.setLong(1, id.longValue());
		try (ResultSet rs = workingOnDiscsStmt.executeQuery()) {
			while (rs.next()) {
				discWritableArtist.add(rs.getLong("id_disc"));
			}
		}

		mainDiscStmt.setLong(1, id.longValue());
		try (ResultSet rs = mainDiscStmt.executeQuery()) {
			while (rs.next()) {
				artist.setMain(rs.getLong("id_disc"));
			}
		}

		artist.setId(id);

		return artist;
	}

	private final long insertArtist(@Nonnull Artist artist) throws SQLException {
		Preconditions.checkState(!(insertArtistStmt.isClosed()),
				"cannot execute query if statement is closed");

		insertArtistStmt.setString(1, artist.getName());
		insertArtistStmt.setDate(2, new java.sql.Date(artist.getBirthday()
				.getTime()));
		int rows = insertArtistStmt.executeUpdate();
		try (ResultSet rs = insertArtistStmt.getGeneratedKeys()) {
			ResultSetMetaData metaData = rs.getMetaData();
			if (rows == 1 && metaData.getColumnCount() == 1) {
				return rs.getLong(metaData.getColumnName(1));
			} else {
				throw new SQLException("no rows affected");
			}
		}

	}

	private final void updateArtist(@Nonnull Artist artist) throws SQLException {
		Preconditions.checkState(!(updateArtistStmt.isClosed()),
				"cannot execute query if statement is closed");

		updateArtistStmt.setString(1, artist.getName());
		updateArtistStmt.setDate(2, new java.sql.Date(artist.getBirthday()
				.getTime()));
		updateArtistStmt.setLong(3, artist.getId());
		int rows = updateArtistStmt.executeUpdate();
		if (rows != 1) {
			throw new SQLException("no rows affected");
		}

	}

	private final void deleteArtist(@Nonnull Artist artist) throws SQLException {
		Preconditions.checkState(!(deleteArtistStmt.isClosed()),
				"cannot execute query if statement is closed");

		deleteArtistStmt.setLong(1, artist.getId());
		int rows = deleteArtistStmt.executeUpdate();
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
		mainDiscStmt.close();
		workingOnDiscsStmt.close();
		idStmt.close();
		con.close();
	}

}
