/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.entities.impl.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.TransientArtist;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryFactory;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryType;

/**
 * @author miguel
 * 
 */
public class TransientDisc implements Disc {
	private final String name;
	final List<Song> songs;
	final List<Artist> artists;
	Artist mainArtist;
	private final Date releaseDate;

	/*
	 * 
	 */
	public TransientDisc(String name) {
		this(name, null);
	}

	/*
	 * 
	 */
	public TransientDisc(String name, Date releaseDate) {
		this.name = name;

		this.songs = Collections.synchronizedList(new ArrayList<Song>());
		this.artists = Collections.synchronizedList(new ArrayList<Artist>());

		this.releaseDate = releaseDate;
	}

	/*
	 * 
	 */
	public TransientDisc(Disc other) throws RepositoryException {
		this.name = other.getName();

		this.songs = Collections.synchronizedList(new ArrayList<Song>());
		Iterable<Song> otherSongs = other.getSongs();
		for (Song song : otherSongs) {
			this.songs.add(song);
		}

		this.artists = Collections.synchronizedList(new ArrayList<Artist>());
		Iterable<Artist> otherArtists = other.getArtists();
		for (Artist artist : otherArtists) {
			this.artists.add(artist);
		}

		this.releaseDate = other.getReleaseDate();
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Entity#getId()
	 */
	public Long getId() {
		// TODO Auto-generated method stub
		return 0L;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Entity#isTransient()
	 */
	public boolean isTransient() {
		// TODO Auto-generated method stub
		return false;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Disc#getName()
	 */
	public String getName() {
		return this.name;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Disc#getArtists()
	 */
	public Iterable<Artist> getArtists() {
		return this.artists;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Disc#getMainArtist()
	 */
	public Artist getMainArtist() throws RepositoryException,
			ExecutionException {
		if (this.mainArtist == null) {
			return new TransientArtist("Unknown Main Artist",
					RepositoryType.InMemory);
		} else {
			return RepositoryFactory.getRepository(Artist.class,
					RepositoryType.InMemory).getById(this.mainArtist.getId());
		}
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Disc#getSongs()
	 */
	public Iterable<Song> getSongs() {
		return this.songs;
	}

	/*
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Disc#getReleaseDate()
	 */
	public Date getReleaseDate() {
		if (this.releaseDate == null) {
			System.err.println("Unknown release date.");
			return new Date();
		} else {
			return (Date) this.releaseDate.clone();
		}
	}

	/*
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Override Object method
		return super.equals(obj);
	}

	/*
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Override Object method
		return getClass().getName() + "@" + Integer.toHexString(hashCode());
	}

	/*
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public int hashCode() {
		// TODO Override Object method
		return super.hashCode();
	}

}
