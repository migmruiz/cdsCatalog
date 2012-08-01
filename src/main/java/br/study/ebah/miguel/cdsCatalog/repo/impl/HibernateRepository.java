package br.study.ebah.miguel.cdsCatalog.repo.impl;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;

import org.hibernate.ObjectNotFoundException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Composer;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.Entity;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.entities.jpa.JPAArtist;
import br.study.ebah.miguel.cdsCatalog.entities.jpa.JPAComposer;
import br.study.ebah.miguel.cdsCatalog.entities.jpa.JPADisc;
import br.study.ebah.miguel.cdsCatalog.entities.jpa.JPASong;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;

/**
 * 
 * @author miguel
 * 
 */
public class HibernateRepository<T extends Entity> implements Repository<T> {
	private static SessionFactory factory;
	private static final AnnotationConfiguration cfg;
	private final Class<? extends Entity> clazz;

	static {
		try {
			cfg = new AnnotationConfiguration();
			cfg.addAnnotatedClass(JPAArtist.class);
			cfg.addAnnotatedClass(JPAComposer.class);
			cfg.addAnnotatedClass(JPADisc.class);
			cfg.addAnnotatedClass(JPASong.class);
			cfg.buildMappings();

			rebuildFactory();
		} catch (Throwable ex) {
			throw new ExceptionInInitializerError(ex);
		}
	}

	public HibernateRepository(Class<? extends Entity> type) {
		this.clazz = toJPA(type);
	}

	private static final void rebuildFactory() {
		if (factory != null && !factory.isClosed())
			factory.close();
		if (cfg.getProperty(Environment.SESSION_FACTORY_NAME) != null) {
			cfg.buildSessionFactory();
		} else {
			factory = cfg.buildSessionFactory();
		}
	}

	@Override
	public void init() {
		rebuildFactory();
		factory.getCurrentSession().beginTransaction();;
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getById(@Nonnull final Long id) throws RepositoryException {
		try {
			return (T) factory.getCurrentSession().load(this.clazz, id);
		} catch (ObjectNotFoundException e) {
			throw new RepositoryException(
					"repository does not contain this entity", e);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public T save(@Nonnull final T entity) throws RepositoryException {
		try {
			T persistentEntity;
			if (entity.isTransient()) {
				if (entity instanceof Artist) {
					if (entity instanceof Composer) {
						JPAComposer composer = new JPAComposer();
						composer.setKnownComposedSongs((Set<? extends Song>) ((Composer) entity)
								.getKnownComposedSongs());
						persistentEntity = (T) composer;
					} else {
						persistentEntity = (T) new JPAArtist();
					}
					persistentEntity = (T) newArtist((Artist) entity,
							persistentEntity);
				} else if (entity instanceof Disc) {
					persistentEntity = (T) newDisc(entity);
				} else if (entity instanceof Song) {
					persistentEntity = (T) newSong(entity);
				} else {
					throw new AssertionError();
				}
			} else {
				persistentEntity = entity;
			}
			factory.getCurrentSession().save(persistentEntity);
			return getById(persistentEntity.getId());
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public void delete(@Nonnull final T entity) throws RepositoryException {
		try {
			factory.getCurrentSession().delete(entity);
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}

	/*
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		if (factory != null && !factory.isClosed()) {
			factory.getCurrentSession().getTransaction().commit();
			factory.close();
		}
	}

	private static Class<? extends Entity> toJPA(Class<? extends Entity> type) {
		Class<? extends Entity> jpaClazz;
		if (type == Artist.class) {
			jpaClazz = JPAArtist.class;
		} else if (type == Composer.class) {
			jpaClazz = JPAComposer.class;
		} else if (type == Disc.class) {
			jpaClazz = JPADisc.class;
		} else if (type == Song.class) {
			jpaClazz = JPASong.class;
		} else {
			jpaClazz = type;
		}
		return jpaClazz;
	}

	private Song newSong(final T entity) throws RepositoryException {
		JPASong nextSong = new JPASong();
		Song prevSong = (Song) entity;
		nextSong.setName(prevSong.getName());
		nextSong.setFirstReleaseDate(prevSong.getFirstReleaseDate());
		nextSong.setKnownDiscs((Set<? extends Disc>) prevSong.getKnownDiscs());
		nextSong.setKnownArtists((Set<? extends Artist>) prevSong
				.getKnownArtists());
		nextSong.setComposer(prevSong.getComposer());
		return nextSong;
	}

	private Disc newDisc(final T entity) throws RepositoryException,
			ExecutionException {
		JPADisc nextDisc = new JPADisc();
		Disc prevDisc = (Disc) entity;
		nextDisc.setName(prevDisc.getName());
		nextDisc.setReleaseDate(prevDisc.getReleaseDate());
		nextDisc.setSongs((List<? extends Song>) prevDisc.getSongs());
		nextDisc.setArtists((Set<? extends Artist>) prevDisc.getArtists());
		nextDisc.setMainArtist(prevDisc.getMainArtist());
		return nextDisc;
	}

	private final Artist newArtist(final Artist prevArtist, T persistentEntity)
			throws RepositoryException, ExecutionException {
		JPAArtist nextArtist = (JPAArtist) persistentEntity;
		nextArtist.setName(prevArtist.getName());
		nextArtist.setBirthday(prevArtist.getBirthday());
		nextArtist.setKnownSongs((Set<? extends Song>) prevArtist
				.getKnownSongs());
		nextArtist.setKnownDiscs((Set<? extends Disc>) prevArtist
				.getKnownDiscs());
		nextArtist.setKnownMainDiscs((Set<? extends Disc>) prevArtist
				.getKnownMainDiscs());
		return nextArtist;
	}

	// @SuppressWarnings("unused")
	private static void createTables() {
		SchemaExport se = new SchemaExport(cfg);
		se.create(true, true);
	}

	public static void main(String[] args) {
		createTables();
	}
}
