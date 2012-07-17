/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.inMemory.elements;

import java.util.Date;

import br.study.ebah.miguel.cdsCatalog.actions.IsWritable;
import br.study.ebah.miguel.cdsCatalog.actions.Writable;
import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;

/**
 * @author miguel
 * 
 */
public class InMemoryDiscRW extends InMemoryDisc implements IsWritable {

	/*
	 * 
	 */
	public InMemoryDiscRW(String name) {
		super(name);
	}

	/*
	 * 
	 */
	public InMemoryDiscRW(String name, Date date) {
		super(name, date);
	}
	
	/*
	 * 
	 */
	public InMemoryDiscRW(Disc other) throws RepositoryException {
		super(other);
	}

	/*
	 * 
	 * @see
	 * br.study.ebah.miguel.cdsCatalog.actions.IsWritable#asAddable(java.lang
	 * .Class)
	 */
	@SuppressWarnings("unchecked")
	public <T> Writable<T> asWritable(Class<T> type)
			throws IllegalArgumentException {
		if (type == Song.class) {
			return (Writable<T>) new Writable<Song>() {

				public void add(Song t) {
					songs.add(t);
				}

			};
		} else if (type == Artist.class) {
			return (Writable<T>) new Writable<Artist>() {

				public void add(Artist t) {
					artists.add(t);
				}

			};
		} else {
			throw new IllegalArgumentException();
		}
	}

	/*
	 * 
	 */
	public void setMain(Artist artist) {
		// TODO uncomment
//		if (this.artists.contains(artist)) {
			this.mainArtist = artist;
//		} else {
//			throw new IllegalArgumentException(
//					"This disc is unknown to this artist.");
//		}

	}

}
