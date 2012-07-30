/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.entities.impl.admin;

import java.util.Date;

import javax.annotation.Nonnull;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Composer;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;

/**
 * @author miguel
 * 
 */
public class PersistentSong implements Song {
	private Song song;

	/*
	 * 
	 */
	public PersistentSong(@Nonnull Song song) {
		this.song = song;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Entity#getId()
	 */
	@Override
	public Long getId() {
		return song.getId();
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Entity#isTransient()
	 */
	@Override
	public boolean isTransient() {
		return false;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Song#getName()
	 */
	@Override
	public String getName() {
		return this.song.getName();
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Song#getComposer()
	 */
	@Override
	public Composer getComposer() throws RepositoryException {
		return this.song.getComposer();
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Song#getLyrics()
	 */
	@Override
	public String getLyrics() {
		return this.song.getLyrics();
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Song#getFirstReleaseDate()
	 */
	@Override
	public Date getFirstReleaseDate() {
		return this.song.getFirstReleaseDate();
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Song#getKnownArtists()
	 */
	@Override
	public Iterable<? extends Artist> getKnownArtists() throws RepositoryException {
		return this.song.getKnownArtists();
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Song#getKnownDiscs()
	 */
	@Override
	public Iterable<? extends Disc> getKnownDiscs() throws RepositoryException {
		return this.song.getKnownDiscs();
	}

}
