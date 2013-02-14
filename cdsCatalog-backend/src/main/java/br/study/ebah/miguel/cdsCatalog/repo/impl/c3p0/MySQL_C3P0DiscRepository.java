package br.study.ebah.miguel.cdsCatalog.repo.impl.c3p0;

import java.io.IOException;
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
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.DiscImpl;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryType;
import br.study.ebah.miguel.cdsCatalog.sql.ConnectionFactory;
import br.study.ebah.miguel.cdsCatalog.sql.MySQL_C3P0ConnectionFactory;
import br.study.ebah.miguel.cdsCatalog.sql.SQLDBNoDataException;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class MySQL_C3P0DiscRepository implements Repository<Disc> {
	private static final Cache<Long, Disc> cache = CacheBuilder.newBuilder()
			.build();
	private static final ConnectionFactory connFact = MySQL_C3P0ConnectionFactory
			.getInstance();

	@Override
	public void initialize() {
	}

	@Override
	public Disc getById(@Nonnull final Long id) throws RepositoryException {
		try {
			return cache.get(id, new Callable<Disc>() {
				@Override
				public Disc call() throws Exception {
					Preconditions.checkNotNull(id, "id cannot be null");
					try (final Connection con = connFact.getConnection()) {
						final Disc persistentDisc = pullDisc(id, con);
						cache.put(id, persistentDisc);
						return persistentDisc;
					}
				}
			});
		} catch (ExecutionException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public Disc save(@Nonnull final Disc disc) throws RepositoryException {

		try (final Connection con = connFact.getConnection()) {
			final Long id;
			if (disc.isTransient()) {
				id = insertDisc(disc, con);
			} else {
				id = disc.getId();
				updateDisc(disc, con);
				cache.invalidate(id);
			}
			return getById(id);
		} catch (SQLException | ExecutionException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public void delete(@Nonnull final Disc disc) throws RepositoryException {
		try {
			try (final Connection con = connFact.getConnection()) {
				deleteDisc(disc, con);
				cache.invalidate(disc.getId());
			}
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
	}

	private final static Disc pullDisc(@Nonnull final Long id, Connection con)
			throws SQLException, ExecutionException, SQLDBNoDataException {
		try (final PreparedStatement idStmt = con
				.prepareStatement("SELECT * FROM disc WHERE id_disc=?");
				final PreparedStatement workingOnDiscsStmt = con
						.prepareStatement("SELECT * FROM `disc_artist-workingOn`"
								+ " WHERE id_disc=?");) {
			idStmt.setLong(1, id.longValue());
			final DiscImpl disc;
			try (final ResultSet idRs = idStmt.executeQuery()) {
				if (idRs.first()) {
					final java.sql.Date releaseDateSQL = idRs
							.getDate("releaseDate");
					if (releaseDateSQL == null) {
						disc = new DiscImpl(idRs.getString("name"),
								RepositoryType.MySQL);
					} else {
						disc = new DiscImpl(idRs.getString("name"), new Date(
								releaseDateSQL.getTime()), RepositoryType.MySQL);
					}

					final Writable<Artist> artistWritableDisc = disc
							.asWritable(Artist.class);

					workingOnDiscsStmt.setLong(1, id.longValue());
					try (final ResultSet woRs = workingOnDiscsStmt
							.executeQuery()) {
						while (woRs.next()) {
							artistWritableDisc.add(woRs.getLong("id_artist"));
						}
					}

					disc.setMain(idRs.getLong("id_mainArtist"));

					disc.setId(id);
				} else {
					throw new SQLDBNoDataException("no data on artist table");
				}
			}
			return disc;
		}
	}

	private final static long insertDisc(@Nonnull Disc disc, Connection con)
			throws SQLException, RepositoryException, ExecutionException {
		try (final PreparedStatement insertDiscStmt = con.prepareStatement(
				"INSERT INTO disc (name,releaseDate,id_mainArtist)"
						+ " VALUES (?,?,?);", Statement.RETURN_GENERATED_KEYS)) {

			insertDiscStmt.setString(1, disc.getName());
			insertDiscStmt.setDate(2, new java.sql.Date(disc.getReleaseDate()
					.getTime()));
			try {
				insertDiscStmt.setLong(3, disc.getMainArtist().getId());
			} catch (RepositoryException e) {
				insertDiscStmt.setLong(3, -1L);
			}
			final int rows = insertDiscStmt.executeUpdate();
			try (final ResultSet rs = insertDiscStmt.getGeneratedKeys()) {
				final ResultSetMetaData metaData = rs.getMetaData();
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

	private final static void updateDisc(@Nonnull Disc disc, Connection con)
			throws SQLException, RepositoryException, ExecutionException {
		try (final PreparedStatement updateDiscStmt = con
				.prepareStatement("UPDATE disc SET name=?, releaseDate=?,"
						+ "id_mainArtist=? WHERE id_disc=?;")) {

			updateDiscStmt.setString(1, disc.getName());
			updateDiscStmt.setDate(2, new java.sql.Date(disc.getReleaseDate()
					.getTime()));
			updateDiscStmt.setLong(3, disc.getMainArtist().getId());
			updateDiscStmt.setLong(4, disc.getId());
			final int rows = updateDiscStmt.executeUpdate();
			if (rows != 1) {
				throw new SQLException("no rows affected");
			}
		}
		// TODO update many-to-many tables
	}

	private final static void deleteDisc(@Nonnull Disc disc, Connection con)
			throws SQLException {
		try (final PreparedStatement deleteDiscStmt = con
				.prepareStatement("DELETE FROM disc" + " WHERE id_disc=?;");) {

			deleteDiscStmt.setLong(1, disc.getId());
			final int rows = deleteDiscStmt.executeUpdate();
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
