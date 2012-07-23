package br.study.ebah.miguel.cdsCatalog.repo.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.PersistentArtist;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;

/**
 * 
 * @author miguel
 * 
 */
public class InMemoryArtistRepository implements Repository<Artist> {
	private static final Map<Long, Artist> map = new ConcurrentHashMap<>();

	@Override
	public Artist getById(@Nonnull final Long id) throws RepositoryException {
		if (map.containsKey(id)) {
			return map.get(id);
		} else {
			throw new RepositoryException(
					"repository does not contain this artist");
		}
	}

	@Override
	public Artist save(@Nonnull final Artist artist) throws RepositoryException {
		try {
			Artist persistentArtist;
			if (artist.isTransient()) {
				persistentArtist = new PersistentArtist(artist);
			} else {
				persistentArtist = artist;
			}
			map.put(persistentArtist.getId(), artist);
			return persistentArtist;
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public void delete(@Nonnull final Artist artist) throws RepositoryException {
		try {
			map.remove(artist.getId());
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
