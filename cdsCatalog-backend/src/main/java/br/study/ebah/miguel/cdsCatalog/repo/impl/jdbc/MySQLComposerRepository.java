package br.study.ebah.miguel.cdsCatalog.repo.impl.jdbc;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Composer;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.ComposerImpl;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryType;
import br.study.ebah.miguel.cdsCatalog.sql.MySQLConnectionFactory;
import br.study.ebah.miguel.cdsCatalog.sql.SQLDBNoDataException;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class MySQLComposerRepository implements Repository<Composer> {
	private static final Cache<Long, Composer> cache = CacheBuilder
			.newBuilder().build();

	private static final Connection con;
	private static final PreparedStatement insertComposerStmt;
	private static final PreparedStatement composedSongStmt;
	private static final MySQLConnectionFactory connFact;

	static {
		try {
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
				composedSongStmt = con.prepareStatement("SELECT * FROM song"
						+ " WHERE id_composer=?");
			} catch (SQLException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	@Override
	public void initialize() {
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
					final Composer persistentComposer = pullComposer(id);
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
		final Long id;
		try {
			Preconditions.checkState(!(con.isClosed() || con.isReadOnly()),
					"cannot execute query if connection is closed or"
							+ " read-only");
			if (composer.isTransient()) {
				id = MySQLArtistRepository.insertArtist(composer);
				insertComposerStmt.setLong(1, id);
				insertComposerStmt.executeUpdate();
			} else {
				id = composer.getId();
				MySQLArtistRepository.updateArtist(composer);
				cache.invalidate(id);
			}
		} catch (SQLException | ExecutionException e) {
			throw new RepositoryException(e);
		}

		return getById(id);
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
			throws SQLException, ExecutionException, SQLDBNoDataException {
		Preconditions.checkState(!composedSongStmt.isClosed(),
				"cannot execute query if statement is closed");
		final Artist tempArtist = MySQLArtistRepository.pullArtist(id);
		final ComposerImpl composer = new ComposerImpl(tempArtist,
				RepositoryType.MySQL);

		try (final ResultSet rs = composedSongStmt.executeQuery()) {
			if (rs.first()) {
				final long song_id = rs.getLong("id_song");
				if (song_id != 0L) {
					composer.asWritable(Song.class).add(song_id);
					composer.setMain(song_id);
				}
			}
		}

		return composer;
	}

	/*
	 * 
	 * @see java.lang.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		try {
			insertComposerStmt.close();
			con.close();
		} catch (SQLException e) {
			throw new IOException(e);
		}
	}

}
