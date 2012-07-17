package br.study.ebah.miguel.cdsCatalog.entities.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;

/**
 * 
 * @author bruno
 *
 */
class ArtistFromRepoImpl implements Artist {
	private final Repository<Song> songRepository;
	private final String name;
	private final int id;
	private final List<Integer> knownSongsIds = new ArrayList<>();

	ArtistFromRepoImpl(final String name, final Integer id,
			final Collection<Integer> knownSongsIds,
			final Repository<Song> songRepository) {
		this.name = name;
		this.id = id;
		this.songRepository = songRepository;
		this.knownSongsIds.addAll(knownSongsIds);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public boolean isTransient() {
		return false;
	}

	@Override
	public Iterable<Song> getKnownSongs() throws RepositoryException {
		List<Song> l = new ArrayList<>();
		// TODO friendIds -> knownSongs
		for (final Integer knownSongId : knownSongsIds) {
			l.add(songRepository.getById(knownSongId));
		}
		return l;
	}

	@Override
	public Iterable<Disc> getKnownDiscs() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Disc> getKnownMainDiscs() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getBirthday() {
		// TODO Auto-generated method stub
		return null;
	}
}