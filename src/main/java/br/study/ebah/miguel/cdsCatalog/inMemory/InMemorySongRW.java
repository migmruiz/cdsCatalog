/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.inMemory;

import java.util.Date;

import br.study.ebah.miguel.cdsCatalog.actions.IsWritable;
import br.study.ebah.miguel.cdsCatalog.actions.Writable;
import br.study.ebah.miguel.cdsCatalog.elements.Artist;
import br.study.ebah.miguel.cdsCatalog.elements.Composer;
import br.study.ebah.miguel.cdsCatalog.elements.Disc;

/**
 * @author miguel
 *
 */
public class InMemorySongRW extends InMemorySong implements IsWritable {

	public InMemorySongRW(String name) {
		super(name);
	}
	
	InMemorySongRW(String name, Date firstReleaseDate) {
		super(name, firstReleaseDate);
	}
	/* 
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.actions.IsWritable#asAddable(java.lang.Class)
	 */
	@SuppressWarnings("unchecked")
	public <T> Writable<T> asWritable(Class<T> type)
			throws IllegalArgumentException {
		if (type == Disc.class) {
			return (Writable<T>) new Writable<Disc>() {

				public void add(Disc t) {
					knownDiscs.add(t);
				}

			};
		} else if (type == Artist.class) {
			return (Writable<T>) new Writable<Artist>() {

				public void add(Artist t) {
					knownArtists.add(t);
				}

			};
		} else {
			throw new IllegalArgumentException();
		}
	}

	/*
	 * 
	 */
	public void setComposer(Composer composer) {
		if (this.knownArtists.contains(composer)) {
			this.composer = composer;
		} else {
			throw new IllegalArgumentException(
					"This artist is unknown to this song.");
		}

	}

}
