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
 * @author miguel
 * 
 */
class DiscFromRepo implements Disc {
	private final Long id;

	private final Disc disc;

	DiscFromRepo(final Long id, RepositoryType store)
			throws RepositoryException, ExecutionException {

		this.id = id;
		this.disc = RepositoryFactory.getRepository(Disc.class, store)
				.getById(id);
	}

	@Override
	public String getName() {
		return this.disc.getName();
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
	public Iterable<? extends Artist> getArtists() throws RepositoryException {
		return this.disc.getArtists();
	}

	@Override
	public Artist getMainArtist() throws RepositoryException,
			ExecutionException {
		return this.disc.getMainArtist();
	}

	@Override
	public Iterable<? extends Song> getSongs() throws RepositoryException {
		return this.disc.getSongs();
	}

	@Override
	public Date getReleaseDate() {
		return this.disc.getReleaseDate();
	}

}