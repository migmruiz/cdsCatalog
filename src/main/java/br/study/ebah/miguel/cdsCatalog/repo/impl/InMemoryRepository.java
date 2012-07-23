package br.study.ebah.miguel.cdsCatalog.repo.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import br.study.ebah.miguel.cdsCatalog.entities.Entity;
import br.study.ebah.miguel.cdsCatalog.repo.Persistence;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;

/**
 * 
 * @author miguel
 * 
 */
public class InMemoryRepository<T extends Entity> implements
		Repository<T> {
	private final Map<Long, T> map = new ConcurrentHashMap<>();

	@Override
	public T getById(@Nonnull final Long id) throws RepositoryException {
		T gotIt = map.get(id);
		if (gotIt != null) {
			return gotIt;
		} else {
			throw new RepositoryException(
					"repository does not contain this entity");
		}
	}

	@Override
	public T save(@Nonnull final T entity) throws RepositoryException {
		try {
			T persistentEntity;
			if (entity.isTransient()) {
				persistentEntity = (new Persistence<T>(entity)).entity();
			} else {
				persistentEntity = entity;
			}
			map.put(persistentEntity.getId(), entity);
			return persistentEntity;
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public void delete(@Nonnull final T entity) throws RepositoryException {
		try {
			map.remove(entity.getId());
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
		map.clear();
	}
}
