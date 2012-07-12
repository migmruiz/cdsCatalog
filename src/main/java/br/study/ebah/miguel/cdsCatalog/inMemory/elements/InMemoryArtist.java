/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.inMemory.elements;

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
public class InMemoryArtist implements Artist {
	private final String name;
	protected final List<Song> knownSongs;
	protected final List<Disc> knownDiscs;
	protected final List<Disc> knownMainDiscs;
	private final Date birthday;
	
	/*
	 * 
	 */
	public InMemoryArtist(String name) {
		this(name, null);
	}
	
	/*
	 * 
	 */
	public InMemoryArtist(String name, Date birthday) {
		this.name = name;

		this.knownSongs = Collections.synchronizedList(new ArrayList<Song>());
		this.knownDiscs = Collections.synchronizedList(new ArrayList<Disc>());
		this.knownMainDiscs = Collections
				.synchronizedList(new ArrayList<Disc>());
		
		this.birthday = birthday;

	}
	
	/*
	 * 
	 */
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
	
	
	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Artist#getBirthday()
	 */
	public Date getBirthday() {
		return (Date) this.birthday.clone();
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
