/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.inMemory;

import java.util.Date;

import br.study.ebah.miguel.cdsCatalog.actions.Writable;
import br.study.ebah.miguel.cdsCatalog.actions.IsWritable;
import br.study.ebah.miguel.cdsCatalog.elements.Artist;
import br.study.ebah.miguel.cdsCatalog.elements.Disc;
import br.study.ebah.miguel.cdsCatalog.elements.Song;

/**
 * @author miguel
 * 
 */
public class InMemoryArtistRW extends InMemoryArtist implements IsWritable {
	/*
	 * 
	 */
	public InMemoryArtistRW(Artist other) {
		super(other);
	}

	/*
	 * 
	 */
	public InMemoryArtistRW(String name, Date birthday) {
		super(name, birthday);
	}

	/*
	 * 
	 */
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
	 * @see
	 * br.study.ebah.miguel.cdsCatalog.actions.IsWritable#asAddable(java.lang
	 * .Class)
	 */
	@SuppressWarnings(value = "unchecked")
	public <T> Writable<T> asAddable(Class<T> type)
			throws IllegalArgumentException {
		if (type == Song.class) {
			return (Writable<T>) new Writable<Song>() {

				public void add(Song t) {
					knownSongs.add(t);
				}

			};
		} else if (type == Disc.class) {
			return (Writable<T>) new Writable<Disc>() {

				public void add(Disc t) {
					knownDiscs.add(t);
				}

			};
		} else {
			throw new IllegalArgumentException();
		}
	}

}
