package br.study.ebah.miguel.cdsCatalog.repo.impl;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;

import com.google.common.base.Optional;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;


/**
 * 
 * @author bruno
 *
 */
public class ArtistRepository implements Repository<Artist> {
	private static final Cache<Integer, Artist> cache = CacheBuilder
			.newBuilder().build();

	@Override
	public Artist getById(final int id) throws RepositoryException {
		try {
			return cache.get(id, new Callable<Artist>() {
				@SuppressWarnings("unused")
				@Override
				public Artist call() throws Exception {
					// Connection c = ...
					// Artist a = c.query(....select....);
					// cache.put(id, a);
					// return a;
					if (1 > 0) {
						return null;
					} else {
						throw new UnsupportedOperationException();
					}
				}
			});
		} catch (ExecutionException e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public Artist save(final Artist artist) throws RepositoryException {
		Optional<Integer> id = Optional.absent();
		if (artist.isTransient()) {
			// insert ....
			Optional.of(123); // id gerado pelo banco ....
		} else {
			// update ....
			// cache.invalidate(artist.getId());
		}

		return getById(id.get());
	}

	@Override
	public void delete(final Artist artist) {
		// delete from tabela ....
		cache.invalidate(artist.getId());
	}
}
