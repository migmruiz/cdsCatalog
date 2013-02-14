package br.study.ebah.miguel.cdsCatalog.repo.impl;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import br.study.ebah.miguel.cdsCatalog.entities.Entity;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.AbstractEntity;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;

/**
 * 
 * @author miguel
 * 
 */
public class InMemoryRepository<T extends Entity> implements Repository<T> {
	private final Map<Long, T> map = new ConcurrentHashMap<>();
	private long elementCount = 0;

	@Override
	public void initialize() {
	}

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
			if (entity.isTransient()) {
				((AbstractEntity) entity).setId(++elementCount);
			}
			map.put(entity.getId(), entity);
			return entity;
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
	 * @see java.lang.Closeable#close()
	 */
	@Override
	public void close() throws IOException {
		map.clear();
	}
}
