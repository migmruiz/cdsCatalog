package br.study.ebah.miguel.cdsCatalog.repo.impl.jdbc;

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
import br.study.ebah.miguel.cdsCatalog.sql.MySQLConnectionFactory;
import br.study.ebah.miguel.cdsCatalog.sql.SQLDBNoDataException;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class MySQLDiscRepository implements Repository<Disc> {
	private static final Cache<Long, Disc> cache;

	private static final Connection con;
	private static final PreparedStatement idStmt;
	private static final PreparedStatement workingOnDiscsStmt;
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
						.prepareStatement("SELECT * FROM disc WHERE id_disc=?");
				workingOnDiscsStmt = con
						.prepareStatement("SELECT * FROM `disc_artist-workingOn`"
								+ " WHERE id_disc=?");
				insertDiscStmt = con.prepareStatement(
						"INSERT INTO disc (name,releaseDate,id_mainArtist)"
								+ " VALUES (?,?,?);",
						Statement.RETURN_GENERATED_KEYS);
				updateDiscStmt = con
						.prepareStatement("UPDATE disc SET name=?, releaseDate=?,"
								+ "id_mainArtist=? WHERE id_disc=?;");
				deleteDiscStmt = con.prepareStatement("DELETE FROM disc"
						+ " WHERE id_disc=?;");
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	@Override
	public Disc getById(@Nonnull final Long id) throws RepositoryException {
		try {
			return cache.get(id, new Callable<Disc>() {
				@Override
				public Disc call() throws Exception {
					Preconditions.checkNotNull(id, "id cannot be null");
					Preconditions.checkState(!(con.isClosed()),
							"cannot execute query if connection is closed");
					Disc persistentDisc = pullDisc(id);
					cache.put(id, persistentDisc);
					return persistentDisc;
				}
			});
		} catch (ExecutionException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public Disc save(@Nonnull final Disc disc) throws RepositoryException {
		Optional<Long> id = Optional.absent();
		try {
			Preconditions
					.checkState(!(con.isClosed() || con.isReadOnly()),
							"cannot execute query if connection is closed or read-only");
			if (disc.isTransient()) {
				id = Optional.of(insertDisc(disc));
			} else {
				id = Optional.of(disc.getId());
				updateDisc(disc);
				cache.invalidate(id.get());
			}
		} catch (SQLException | ExecutionException e) {
			throw new RepositoryException(e);
		}

		return getById(id.get());
	}

	@Override
	public void delete(@Nonnull final Disc disc) throws RepositoryException {
		try {
			Preconditions
					.checkState(!(con.isClosed() || con.isReadOnly()),
							"cannot execute query if connection is closed or read-only");

			deleteDisc(disc);
			cache.invalidate(disc.getId());
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
	}

	private final static Disc pullDisc(@Nonnull final Long id)
			throws SQLException, ExecutionException, SQLDBNoDataException {
		Preconditions.checkState(
				!(idStmt.isClosed() || workingOnDiscsStmt.isClosed()),
				"cannot execute query if statement is closed");

		idStmt.setLong(1, id.longValue());
		DiscImpl disc;
		try (ResultSet idRs = idStmt.executeQuery()) {
			if (idRs.first()) {
				java.sql.Date releaseDateSQL = idRs.getDate("releaseDate");
				if (releaseDateSQL == null) {
					disc = new DiscImpl(idRs.getString("name"),
							RepositoryType.MySQL);
				} else {
					disc = new DiscImpl(idRs.getString("name"), new Date(
							releaseDateSQL.getTime()), RepositoryType.MySQL);
				}

				Writable<Artist> artistWritableDisc = disc
						.asWritable(Artist.class);

				workingOnDiscsStmt.setLong(1, id.longValue());
				try (ResultSet woRs = workingOnDiscsStmt.executeQuery()) {
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

	private final static long insertDisc(@Nonnull Disc disc)
			throws SQLException, RepositoryException, ExecutionException {
		Preconditions.checkState(!(insertDiscStmt.isClosed()),
				"cannot execute query if statement is closed");

		insertDiscStmt.setString(1, disc.getName());
		insertDiscStmt.setDate(2, new java.sql.Date(disc.getReleaseDate()
				.getTime()));
		try {
			insertDiscStmt.setLong(3, disc.getMainArtist().getId());
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
		// TODO update/insert into many-to-many tables
	}

	private final static void updateDisc(@Nonnull Disc disc)
			throws SQLException, RepositoryException, ExecutionException {
		Preconditions.checkState(!(updateDiscStmt.isClosed()),
				"cannot execute query if statement is closed");

		updateDiscStmt.setString(1, disc.getName());
		updateDiscStmt.setDate(2, new java.sql.Date(disc.getReleaseDate()
				.getTime()));
		updateDiscStmt.setLong(3, disc.getMainArtist().getId());
		updateDiscStmt.setLong(4, disc.getId());
		int rows = updateDiscStmt.executeUpdate();
		if (rows != 1) {
			throw new SQLException("no rows affected");
		}
		// TODO update many-to-many tables
	}

	private final static void deleteDisc(@Nonnull Disc disc)
			throws SQLException {
		Preconditions.checkState(!(deleteDiscStmt.isClosed()),
				"cannot execute query if statement is closed");

		deleteDiscStmt.setLong(1, disc.getId());
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
		workingOnDiscsStmt.close();
		idStmt.close();
		con.close();
	}

	@Override
	public void initialize() {
	}

}
