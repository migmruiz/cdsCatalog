package br.study.ebah.miguel.cdsCatalog.repo.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Composer;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.PersistentComposer;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.TransientComposer;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryType;
import br.study.ebah.miguel.cdsCatalog.sql.MySQLConnectionFactory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class MySQLComposerRepository implements Repository<Composer> {
	private static final Cache<Long, Composer> cache;

	private static final Connection con;
	private static final PreparedStatement insertComposerStmt;
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
				insertComposerStmt = con
						.prepareStatement("INSERT INTO composer id_composer"
								+ " VALUE ?;");
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	@Override
	public Composer getById(@Nonnull final Long id) throws RepositoryException {
		try {
			return cache.get(id, new Callable<Composer>() {
				@Override
				public Composer call() throws Exception {
					Preconditions.checkNotNull(id, "id cannot be null");
					Preconditions.checkState(!(con.isClosed()),
							"cannot execute query if connection is closed");
					Composer transientComposer = pullComposer(id);
					Composer persistentComposer = new PersistentComposer(
							transientComposer);
					cache.put(id, persistentComposer);
					return persistentComposer;
				}
			});
		} catch (ExecutionException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public Composer save(@Nonnull final Composer composer)
			throws RepositoryException {
		Optional<Long> id = Optional.absent();
		try {
			Preconditions.checkState(!(con.isClosed() || con.isReadOnly()),
					"cannot execute query if connection is closed or"
							+ " read-only");
			if (composer.isTransient()) {
				id = Optional.of(MySQLArtistRepository.insertArtist(composer));
				insertComposerStmt.setLong(1, id.get());
				insertComposerStmt.executeUpdate();
			} else {
				MySQLArtistRepository.updateArtist(composer);
				cache.invalidate(composer.getId());
			}
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}

		return getById(id.get());
	}

	@Override
	public void delete(@Nonnull final Composer artist)
			throws RepositoryException {
		try {
			Preconditions.checkState(!(con.isClosed() || con.isReadOnly()),
					"cannot execute query if connection is closed or"
							+ " read-only");

			MySQLArtistRepository.deleteArtist(artist);
			cache.invalidate(artist.getId());
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
	}

	private final Composer pullComposer(@Nonnull final Long id)
			throws SQLException, ExecutionException {
		Artist tempArtist = MySQLArtistRepository.pullArtist(id);
		Composer composer = new TransientComposer(tempArtist.getName(),
				tempArtist.getBirthday(), RepositoryType.MySQL);
		// TODO import composed songs and create constructor Composer(Artist
		// tempArtist)
		return composer;
	}

	/*
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		insertComposerStmt.close();
		con.close();
	}

}
