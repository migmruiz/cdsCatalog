/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.inMemory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.study.ebah.miguel.cdsCatalog.actions.Addable;
import br.study.ebah.miguel.cdsCatalog.elements.Artist;
import br.study.ebah.miguel.cdsCatalog.elements.Disc;
import br.study.ebah.miguel.cdsCatalog.elements.Song;

/**
 * @author miguel
 * 
 */
public class InMemoryDisc implements Disc {
	private final String name;
	private final List<Song> songs;
	private final List<Artist> artists;
	private Artist mainArtist;
	private final Date releaseDate;

	InMemoryDisc(String name, Date date) {
		this.name = name;

		this.songs = Collections.synchronizedList(new ArrayList<Song>());
		this.artists = Collections.synchronizedList(new ArrayList<Artist>());

		this.releaseDate = date;
	}

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
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Disc#getName()
	 */
	public String getName() {
		return this.name;
	}

	/*
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Disc#getArtists()
	 */
	public Iterable<Artist> getArtists() {
		return this.artists;
	}

	/*
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Disc#getMainArtist()
	 */
	public Artist getMainArtist() {
		return new InMemoryArtist(this.mainArtist);
	}

	/*
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Disc#getSongs()
	 */
	public Iterable<Song> getSongs() {
		return this.songs;
	}

	/*
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Disc#getReleaseDate()
	 */
	public Date getReleaseDate() {
		// return new Date(this.date.getTime());
		return (Date) this.releaseDate.clone();
	}

	@SuppressWarnings("unchecked")
	public <T> Addable<T> asAddable(Class<T> type) throws Throwable {
		if (type == Song.class) {
			return (Addable<T>) new Addable<Song>() {

				public void add(Song t) {
					// InMemoryArtist.this.
					songs.add(t);
				}

			};
		} else if (type == Artist.class) {
			return (Addable<T>) new Addable<Artist>() {

				public void add(Artist t) {
					artists.add(t);
				}

			};
		} else {
			throw new Exception();
		}
	}

	public void setMain(Artist artist) {
		if (this.artists.contains(artist)) {
			this.mainArtist = artist;
		} else {
			throw new IllegalArgumentException(
					"This disc is unknown to this artist.");
		}

	}

}
