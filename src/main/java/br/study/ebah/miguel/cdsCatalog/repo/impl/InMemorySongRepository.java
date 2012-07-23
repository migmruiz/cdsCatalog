package br.study.ebah.miguel.cdsCatalog.repo.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.PersistentSong;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;

/**
 * 
 * @author miguel
 * 
 */
public class InMemorySongRepository implements Repository<Song> {
	private static final Map<Long, Song> map = new ConcurrentHashMap<>();

	@Override
	public Song getById(@Nonnull final Long id) throws RepositoryException {
		if (map.containsKey(id)) {
			return map.get(id);
		} else {
			throw new RepositoryException(
					"repository does not contain this artist");
		}
	}

	@Override
	public Song save(@Nonnull final Song song) throws RepositoryException {
		try {
			Song persistentSong;
			if (song.isTransient()) {
				persistentSong = new PersistentSong(song);
			} else {
				persistentSong = song;
			}
			map.put(persistentSong.getId(), song);
			return persistentSong;
		} catch (Exception e) {
			throw new RepositoryException(e);
		}
	}

	@Override
	public void delete(@Nonnull final Song song) throws RepositoryException {
		try {
			map.remove(song.getId());
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
