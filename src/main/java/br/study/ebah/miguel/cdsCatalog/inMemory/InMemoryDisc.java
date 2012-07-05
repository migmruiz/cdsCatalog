/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.inMemory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.study.ebah.miguel.cdsCatalog.elements.Artist;
import br.study.ebah.miguel.cdsCatalog.elements.Disc;
import br.study.ebah.miguel.cdsCatalog.elements.Song;

/**
 * @author miguel
 * 
 */
public class InMemoryDisc implements Disc {
	private final String name;
	final List<Song> songs;
	final List<Artist> artists;
	Artist mainArtist;
	private final Date releaseDate;

	/*
	 * 
	 */
	InMemoryDisc(String name, Date date) {
		this.name = name;

		this.songs = Collections.synchronizedList(new ArrayList<Song>());
		this.artists = Collections.synchronizedList(new ArrayList<Artist>());

		this.releaseDate = date;
	}

	/*
	 * 
	 */
	InMemoryDisc(Disc other) {
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
	public Artist getMainArtist() {
		return new InMemoryArtist(this.mainArtist);
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
		return (Date) this.releaseDate.clone();
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
		return super.toString();
	}

}
