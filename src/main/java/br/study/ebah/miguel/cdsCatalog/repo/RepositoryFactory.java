package br.study.ebah.miguel.cdsCatalog.repo;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Entity;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * 
 * @author bruno
 *
 */
public final class RepositoryFactory {
	Cache<Class<?>, Repository<?>> repositoryCache = CacheBuilder.newBuilder()
			.build();

	@SuppressWarnings("unchecked")
	public <T extends Entity> Repository<T> getRepository(final Class<T> t)
			throws ExecutionException {
		return ((Repository<T>) repositoryCache.get(t,
				new Callable<Repository<T>>() {

					@Override
					public Repository<T> call() throws Exception {
						if (t == Artist.class) {
							return (Repository<T>) new ArtistRepository();
						} else {
							throw new AssertionError(
									"Repository is not set for this entity");
						}
					}
				}));
	}
}