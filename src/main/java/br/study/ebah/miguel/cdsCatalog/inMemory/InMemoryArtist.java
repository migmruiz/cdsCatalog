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
public class InMemoryArtist implements Artist {
	private final String name;
	private final List<Song> knownSongs;
	private final List<Disc> knownDiscs;
	private final List<Disc> knownMainDiscs;
	private final Date birthday;

	public InMemoryArtist(String name, Date birthday) {
		this.name = name;

		this.knownSongs = Collections.synchronizedList(new ArrayList<Song>());
		// Collections.synchronizedList ou Vector<Song>
		// se getKnownSongs retornar List

		this.knownDiscs = Collections.synchronizedList(new ArrayList<Disc>());
		this.knownMainDiscs = Collections
				.synchronizedList(new ArrayList<Disc>());
		this.birthday = birthday;

	}

	public InMemoryArtist(Artist other) {
		this.name = other.getName();

		this.knownSongs = Collections.synchronizedList(new ArrayList<Song>());
		Iterable<Song> otherKnownSongs = other.getKnownSongs();
		for (Song song : otherKnownSongs) {
			this.knownSongs.add(song);
		}

		this.knownDiscs = Collections.synchronizedList(new ArrayList<Disc>());
		Iterable<Disc> otherDiscs = other.getKnownDiscs();
		for (Disc disc : otherDiscs) {
			this.knownDiscs.add(disc);
		}
		this.knownMainDiscs = Collections
				.synchronizedList(new ArrayList<Disc>());
		otherDiscs = other.getKnownMainDiscs();
		for (Disc disc : otherDiscs) {
			this.knownMainDiscs.add(disc);
		}
		
		this.birthday = other.getBirthday();
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Artist#getName()
	 * 
	 * final -> classe filha não pode dar Override
	 */
	public final String getName() {
		return this.name;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Artist#getKnownSongs()
	 */
	public Iterable<Song> getKnownSongs() {
		return this.knownSongs;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Artist#getKnownDiscs()
	 */
	public Iterable<Disc> getKnownDiscs() {
		return this.knownDiscs;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Artist#getKnownMainDiscs()
	 */
	public Iterable<Disc> getKnownMainDiscs() {
		return this.knownMainDiscs;
	}
	
	public void setMain(Disc disc) {
		if (this.knownDiscs.contains(disc)) {
			this.knownMainDiscs.add(disc);
		} else {
			throw new IllegalArgumentException(
					"This disc is unknown to this artist.");
		}

	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Artist#getBirthday()
	 */
	public Date getBirthday() {
		// return new Date(this.date.getTime());
		return (Date) this.birthday.clone();
	}

	@SuppressWarnings(value = "unchecked")
	public <T> Addable<T> asAddable(Class<T> type) throws Exception {
		if (type == Song.class) {
			return (Addable<T>) new Addable<Song>() {

				public void add(Song t) {
					// InMemoryArtist.this.
					knownSongs.add(t);
				}

			};
		} else if (type == Disc.class) {
			return (Addable<T>) new Addable<Disc>() {

				public void add(Disc t) {
					knownDiscs.add(t);
				}

			};
		} else {
			throw new Exception();
		}
	}

	/* usage
	 * { this.asWritable(Song.class).add(null);
	 * this.asWritable(Artist.class).add(null); }
	 */

}
