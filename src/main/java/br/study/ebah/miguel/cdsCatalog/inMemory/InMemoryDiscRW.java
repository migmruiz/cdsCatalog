/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.inMemory;

import java.util.Date;

import br.study.ebah.miguel.cdsCatalog.actions.IsWritable;
import br.study.ebah.miguel.cdsCatalog.actions.Writable;
import br.study.ebah.miguel.cdsCatalog.elements.Artist;
import br.study.ebah.miguel.cdsCatalog.elements.Disc;
import br.study.ebah.miguel.cdsCatalog.elements.Song;

/**
 * @author miguel
 *
 */
public class InMemoryDiscRW extends InMemoryDisc implements IsWritable{

	InMemoryDiscRW(Disc other) {
		super(other);
	}
	
	InMemoryDiscRW(String name, Date date) {
		super(name, date);
	}
	
	@SuppressWarnings("unchecked")
	public <T> Writable<T> asAddable(Class<T> type) throws IllegalArgumentException {
		if (type == Song.class) {
			return (Writable<T>) new Writable<Song>() {

				public void add(Song t) {
					// InMemoryArtist.this.
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

	public void setMain(Artist artist) {
		if (this.artists.contains(artist)) {
			this.mainArtist = artist;
		} else {
			throw new IllegalArgumentException(
					"This disc is unknown to this artist.");
		}

	}
	
}
