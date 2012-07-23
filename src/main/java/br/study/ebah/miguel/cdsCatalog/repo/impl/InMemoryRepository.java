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
		if (map.containsKey(id)) {
			return map.get(id);
		} else {
			throw new RepositoryException(
					"repository does not contain this entity");
		}
	}

	@Override
	public T save(@Nonnull final T entity) throws RepositoryException {
		try {
			T persistEntentity;
			if (entity.isTransient()) {
				persistEntentity = (new Persistence<T>(entity)).entity();
			} else {
				persistEntentity = entity;
			}
			map.put(persistEntentity.getId(), entity);
			return persistEntentity;
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
