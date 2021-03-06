package br.study.ebah.miguel.cdsCatalog.repo.impl.c3p0;

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
import br.study.ebah.miguel.cdsCatalog.sql.ConnectionFactory;
import br.study.ebah.miguel.cdsCatalog.sql.MySQL_C3P0ConnectionFactory;
import br.study.ebah.miguel.cdsCatalog.sql.SQLDBNoDataException;

import com.google.common.base.Preconditions;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public class MySQL_C3P0ComposerRepository implements Repository<Composer> {
	private static final Cache<Long, Composer> cache = CacheBuilder
			.newBuilder().build();
	private static final ConnectionFactory connFact = MySQL_C3P0ConnectionFactory
			.getInstance();

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
					try (final Connection con = connFact.getConnection()) {
						final Composer persistentComposer = pullComposer(id,
								con);
						cache.put(id, persistentComposer);
						return persistentComposer;
					}
				}
			});
		} catch (ExecutionException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public Composer save(@Nonnull final Composer composer)
			throws RepositoryException {
		try (final Connection con = connFact.getConnection()) {
			final Long id;
			try (final PreparedStatement insertComposerStmt = con
					.prepareStatement("INSERT INTO composer id_composer"
							+ " VALUE ?;")) {
				if (composer.isTransient()) {
					id = MySQL_C3P0ArtistRepository.insertArtist(composer, con);
					insertComposerStmt.setLong(1, id);
					insertComposerStmt.executeUpdate();
				} else {
					id = composer.getId();
					MySQL_C3P0ArtistRepository.updateArtist(composer, con);
					cache.invalidate(id);
				}
			}
			return getById(id);
		} catch (SQLException | ExecutionException e) {
			throw new RepositoryException(e);
		}

	}

	@Override
	public void delete(@Nonnull final Composer artist)
			throws RepositoryException {
		try (final Connection con = connFact.getConnection()) {
			MySQL_C3P0ArtistRepository.deleteArtist(artist, con);
			cache.invalidate(artist.getId());
		} catch (SQLException e) {
			throw new RepositoryException(e);
		}
	}

	private final Composer pullComposer(@Nonnull final Long id,
			@Nonnull Connection con) throws SQLException, ExecutionException,
			SQLDBNoDataException {
		try (final PreparedStatement composedSongStmt = con
				.prepareStatement("SELECT * FROM song" + " WHERE id_composer=?")) {
			final Artist tempArtist = MySQL_C3P0ArtistRepository.pullArtist(id,
					con);
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
