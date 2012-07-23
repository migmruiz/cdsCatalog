package br.study.ebah.miguel.cdsCatalog.repo.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.TransientArtist;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryType;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * 
 * @author bruno
 * 
 */
public class InMemoryArtistRepository implements Repository<Artist> {
	private static final Cache<Long, Artist> cache = CacheBuilder.newBuilder()
			.build();

	@Override
	public Artist getById(@Nonnull final Long id) throws RepositoryException {
		try {
			return cache.get(id, new Callable<Artist>() {
				@Override
				public Artist call() throws Exception {
					Artist a = new TransientArtist(null,
							RepositoryType.InMemory);
					cache.put(id, a);
					// return a;
					return null;

				}
			});
		} catch (ExecutionException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public Artist save(final Artist artist) throws RepositoryException {
		Optional<Long> id = Optional.absent();
		id = Optional.of(artist.getId());
		if (!artist.isTransient()) {
			cache.invalidate(id.get());
		}
		return getById(id.get());
	}

	@Override
	public void delete(final Artist artist) {
		// delete from tabela ....
		cache.invalidate(artist.getId());
	}

	/*
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		cache.cleanUp();
	}
}
