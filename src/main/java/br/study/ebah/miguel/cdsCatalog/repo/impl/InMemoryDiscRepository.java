package br.study.ebah.miguel.cdsCatalog.repo.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.PersistentDisc;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;

/**
 * 
 * @author miguel
 * 
 */
public class InMemoryDiscRepository implements Repository<Disc> {
	private static final Map<Long, Disc> map = new ConcurrentHashMap<>();

	@Override
	public Disc getById(@Nonnull final Long id) throws RepositoryException {
		if (map.containsKey(id)) {
			return map.get(id);
		} else {
			throw new RepositoryException(
					"repository does not contain this disc");
		}
	}

	@Override
	public Disc save(@Nonnull final Disc disc) throws RepositoryException {
		try {
			Disc persistentDisc;
			if (disc.isTransient()) {
				persistentDisc = new PersistentDisc(disc);
			} else {
				persistentDisc = disc;
			}
			map.put(persistentDisc.getId(), disc);
			return persistentDisc;
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public void delete(@Nonnull final Disc disc) throws RepositoryException {
		try {
			map.remove(disc.getId());
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
