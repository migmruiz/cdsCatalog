package br.study.ebah.miguel.cdsCatalog.entities.impl;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryFactory;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryType;

/**
 * 
 * @author bruno
 * 
 */
class ArtistFromRepo implements Artist {
	private final Long id;

	private final Artist artist;

	ArtistFromRepo(final Long id, RepositoryType store)
			throws RepositoryException, ExecutionException {

		this.id = id;
		this.artist = RepositoryFactory.getRepository(Artist.class, store)
				.getById(id);
	}

	@Override
	public String getName() {
		return this.artist.getName();
	}

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public boolean isTransient() {
		return false;
	}

	@Override
	public Iterable<Song> getKnownSongs() throws RepositoryException {
		return this.artist.getKnownSongs();
	}

	@Override
	public Iterable<Disc> getKnownDiscs() throws RepositoryException,
			ExecutionException {
		return this.artist.getKnownDiscs();
	}

	@Override
	public Iterable<Disc> getKnownMainDiscs() throws RepositoryException {
		return this.artist.getKnownMainDiscs();
	}

	@Override
	public Date getBirthday() {
		return this.artist.getBirthday();
	}
}