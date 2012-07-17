/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.inMemory.elements;

import java.util.Date;

import br.study.ebah.miguel.cdsCatalog.actions.IsWritable;
import br.study.ebah.miguel.cdsCatalog.actions.RepositoryException;
import br.study.ebah.miguel.cdsCatalog.actions.Writable;
import br.study.ebah.miguel.cdsCatalog.entities.Composer;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.Song;

/**
 * @author miguel
 * 
 */
public class InMemoryComposerRW extends InMemoryComposer implements IsWritable {

	/*
	 * 
	 */
	public InMemoryComposerRW(Composer other) throws RepositoryException {
		super(other);
	}

	/*
	 * 
	 */
	public InMemoryComposerRW(String name) {
		super(name);
	}

	/*
	 * 
	 */
	public InMemoryComposerRW(String name, Date birthday) {
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
					"This disc is unknown to this composer.");
		}

	}
	
	/*
	 * 
	 */
	public void setMain(Song song) {
		if (this.knownComposedSongs.contains(song)) {
			this.knownMainComposedSongs.add(song);
		} else {
			throw new IllegalArgumentException(
					"This song is unknown to this composer.");
		}

	}

	/*
	 * 
	 * @see
	 * br.study.ebah.miguel.cdsCatalog.actions.IsWritable#asAddable(java.lang
	 * .Class)
	 */
	@SuppressWarnings(value = "unchecked")
	public <T> Writable<T> asWritable(Class<T> type)
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
