package br.study.ebah.miguel.cdsCatalog.repo.impl;

import javax.annotation.Nonnull;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
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
	private static final SessionFactory factory;
	private static final AnnotationConfiguration cfg;
	private final Class<? extends Entity> clazz;

	static {
		cfg = new AnnotationConfiguration();
		cfg.addAnnotatedClass(JPAArtist.class);
		cfg.addAnnotatedClass(JPAComposer.class);
		cfg.addAnnotatedClass(JPADisc.class);
		cfg.addAnnotatedClass(JPASong.class);
		cfg.buildMappings();
		factory = cfg.buildSessionFactory();
	}

	public HibernateRepository(Class<? extends Entity> type) {
		this.clazz = type;
	}

	// public static void main(String[] args) {
	// createTables();
	// }

	@SuppressWarnings("unused")
	private static void createTables() {
		SchemaExport se = new SchemaExport(cfg);
		se.create(true, true);
	}

	@SuppressWarnings("unchecked")
	@Override
	public T getById(@Nonnull final Long id) throws RepositoryException {
		Session session = factory.openSession();
		try {
			T gotIt = null;
			gotIt = (T) session.load(this.clazz, id);
			if (gotIt != null) {
				return gotIt;
			} else {
				throw new RepositoryException(
						"repository does not contain this entity");
			}
		} finally {
			session.close();
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public T save(@Nonnull final T entity) throws RepositoryException {
		Session session = null;
		try {
			T persistentEntity;
			if (entity.isTransient()) {
				if (entity instanceof Artist) {
					if (entity instanceof Composer) {
						persistentEntity = (T) new JPAComposer(
								(Composer) entity);
					} else {
						persistentEntity = (T) new JPAArtist((Artist) entity);
					}
				} else if (entity instanceof Disc) {
					persistentEntity = (T) new JPADisc((Disc) entity);
				} else if (entity instanceof Song) {
					persistentEntity = (T) new JPASong((Song) entity);
				} else {
					throw new AssertionError();
				}
			} else {
				persistentEntity = entity;
			}
			session = factory.openSession();
			session.persist(persistentEntity);
			session.close();
			return getById(persistentEntity.getId());
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public void delete(@Nonnull final T entity) throws RepositoryException {
		Session session = factory.openSession();
		try {
			session.delete(entity);
		} catch (Exception e) {
			throw new RepositoryException(e);
		} finally {
			session.close();
		}
	}

	/*
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		factory.close();
	}
}
