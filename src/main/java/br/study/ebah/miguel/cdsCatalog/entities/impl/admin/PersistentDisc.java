/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.entities.impl.admin;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;

/**
 * @author miguel
 * 
 */
public class PersistentDisc implements Disc {
	private Disc disc;

	/*
	 * 
	 */
	public PersistentDisc(@Nonnull Disc disc) {
		this.disc = disc;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Entity#getId()
	 */
	public Long getId() {
		return disc.getId();
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Entity#isTransient()
	 */
	public boolean isTransient() {
		return false;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Artist#getName()
	 */
	public String getName() {
		return this.disc.getName();
	}

	@Override
	public Iterable<Artist> getArtists() throws RepositoryException {
		return this.disc.getArtists();
	}

	@Override
	public Artist getMainArtist() throws RepositoryException,
			ExecutionException {
		return this.disc.getMainArtist();
	}

	@Override
	public Iterable<Song> getSongs() throws RepositoryException {
		return this.disc.getSongs();
	}

	@Override
	public Date getReleaseDate() {
		return this.disc.getReleaseDate();
	}



}
