package br.study.ebah.miguel.cdsCatalog.repo;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Entity;
import br.study.ebah.miguel.cdsCatalog.repo.impl.InMemoryArtistRepository;
import br.study.ebah.miguel.cdsCatalog.repo.impl.MySQLArtistRepository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * 
 * @author bruno
 * 
 */
public final class RepositoryFactory {
	public static final Cache<Class<?>, Repository<?>> repositoryCache = CacheBuilder
			.newBuilder().build();

	@SuppressWarnings("unchecked")
	public static final <T extends Entity> Repository<T> getRepository(
			final Class<T> t, final RepositoryType store)
			throws ExecutionException {
		return ((Repository<T>) repositoryCache.get(t,
				new Callable<Repository<T>>() {

					@Override
					public Repository<T> call() throws Exception {
						switch (store) {
						case InMemory:
							if (t == Artist.class) {
								return (Repository<T>) new InMemoryArtistRepository();
							} else {
								throw new AssertionError(
										"Repository is not set for this entity"
												+ " of InMemory RepositoryType");
							}

						case MySQL:
							if (t == Artist.class) {
								return (Repository<T>) new MySQLArtistRepository();
							} else {
								throw new AssertionError(
										"Repository is not set for this entity"
												+ " of MySQL RepositoryType");
							}

						default:
							throw new AssertionError(
									"Repository is not set for this"
											+ " repository type");
						}

					}
				}));
	}
}