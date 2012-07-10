/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.inMemory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.study.ebah.miguel.cdsCatalog.elements.Artist;
import br.study.ebah.miguel.cdsCatalog.elements.Composer;
import br.study.ebah.miguel.cdsCatalog.elements.Disc;
import br.study.ebah.miguel.cdsCatalog.elements.Song;
import br.study.ebah.miguel.cdsCatalog.inMemory.specifics.InMemoryComposer;

/**
 * @author miguel
 * 
 */
public class InMemorySong implements Song {
	private final String name;
	final List<Artist> knownArtists;
	final List<Disc> knownDiscs;
	Composer composer;
	private final Date firstReleaseDate;
	private String lyrics;

	/*
	 * 
	 */
	public InMemorySong(String name) {
		this(name, null);
	}

	/*
	 * 
	 */
	public InMemorySong(String name, Date firstReleaseDate) {
		this.name = name;

		this.knownArtists = Collections
				.synchronizedList(new ArrayList<Artist>());
		this.knownDiscs = Collections.synchronizedList(new ArrayList<Disc>());

		this.firstReleaseDate = firstReleaseDate;
	}

	/*
	 * 
	 */
	public InMemorySong(Song other) {
		this.name = other.getName();

		this.knownArtists = Collections
				.synchronizedList(new ArrayList<Artist>());
		Iterable<Artist> otherArtists = other.getKnownArtists();
		for (Artist artist : otherArtists) {
			this.knownArtists.add(artist);
		}

		this.knownDiscs = Collections.synchronizedList(new ArrayList<Disc>());
		Iterable<Disc> otherDiscs = other.getKnownDiscs();
		for (Disc disc : otherDiscs) {
			this.knownDiscs.add(disc);
		}

		this.firstReleaseDate = other.getFirstReleaseDate();
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Song#getName()
	 */
	public String getName() {
		return this.name;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Song#getComposer()
	 */
	public Composer getComposer() {
		if (this.composer == null) {
			return new InMemoryComposer("Unknown Composer");
		} else {
			return new InMemoryComposer(this.composer);
		}
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Song#getLyrics()
	 */
	public String getLyrics() {
		if (this.lyrics == null) {
			return "";
		} else {
			return this.lyrics;
		}
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Song#getFirstReleaseDate()
	 */
	public Date getFirstReleaseDate() {
		if (this.firstReleaseDate == null) {
			System.err.println("Unknown release date.");
			return new Date();
		} else {
			return (Date) this.firstReleaseDate.clone();
		}
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Song#getKnownArtists()
	 */
	public Iterable<Artist> getKnownArtists() {
		return this.knownArtists;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Song#getKnownDiscs()
	 */
	public Iterable<Disc> getKnownDiscs() {
		return this.knownDiscs;
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
