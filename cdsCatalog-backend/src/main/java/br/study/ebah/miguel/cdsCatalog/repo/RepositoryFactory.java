package br.study.ebah.miguel.cdsCatalog.repo;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Composer;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.Entity;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.repo.impl.HibernateRepository;
import br.study.ebah.miguel.cdsCatalog.repo.impl.InMemoryRepository;
import br.study.ebah.miguel.cdsCatalog.repo.impl.c3p0.MySQL_C3P0ArtistRepository;
import br.study.ebah.miguel.cdsCatalog.repo.impl.c3p0.MySQL_C3P0ComposerRepository;
import br.study.ebah.miguel.cdsCatalog.repo.impl.c3p0.MySQL_C3P0DiscRepository;
import br.study.ebah.miguel.cdsCatalog.repo.impl.c3p0.MySQL_C3P0SongRepository;
import br.study.ebah.miguel.cdsCatalog.repo.impl.jdbc.MySQLArtistRepository;
import br.study.ebah.miguel.cdsCatalog.repo.impl.jdbc.MySQLComposerRepository;
import br.study.ebah.miguel.cdsCatalog.repo.impl.jdbc.MySQLDiscRepository;
import br.study.ebah.miguel.cdsCatalog.repo.impl.jdbc.MySQLSongRepository;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

/**
 * 
 * @author bruno, miguel
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
							return new InMemoryRepository<>();

						case MySQL:
							if (t == Artist.class) {
								return (Repository<T>) new MySQLArtistRepository();
							} else if (t == Composer.class) {
								return (Repository<T>) new MySQLComposerRepository();
							} else if (t == Disc.class) {
								return (Repository<T>) new MySQLDiscRepository();
							} else if (t == Song.class) {
								return (Repository<T>) new MySQLSongRepository();
							} else {
								throw new AssertionError(
										"Repository is not set for this entity"
												+ " of MySQL RepositoryType");
							}
						case Hibernate:
							return new HibernateRepository<>(t);

						case MySQL_C3P0:
							if (t == Artist.class) {
								return (Repository<T>) new MySQL_C3P0ArtistRepository();
							} else if (t == Composer.class) {
								return (Repository<T>) new MySQL_C3P0ComposerRepository();
							} else if (t == Disc.class) {
								return (Repository<T>) new MySQL_C3P0DiscRepository();
							} else if (t == Song.class) {
								return (Repository<T>) new MySQL_C3P0SongRepository();
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

	public static void destroy() {
		repositoryCache.invalidateAll();
	}
}