/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.inMemory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.study.ebah.miguel.cdsCatalog.actions.SongsAreWritable;
import br.study.ebah.miguel.cdsCatalog.actions.Addable;
import br.study.ebah.miguel.cdsCatalog.elements.Artist;
import br.study.ebah.miguel.cdsCatalog.elements.Disc;
import br.study.ebah.miguel.cdsCatalog.elements.Song;

/**
 * @author miguel
 * 
 */
public class InMemoryArtist implements Artist/*, Writable<String, Date>*/ {
	private final String name;
	private final List<Song> knownSongs;
	Date date;
	

	public InMemoryArtist(String name) {
		this.name = name;
		
		this.knownSongs = Collections.synchronizedList(new ArrayList<Song>());
				       // Collections.synchronizedList ou Vector<Song>
			           // se getKnownSongs retornar List
		
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
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Artist#getKnownMainDiscs()
	 */
	public Iterable<Disc> getKnownMainDiscs() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Artist#getBirthday()
	 */
	public Date getBirthday() {
//		return new Date(this.date.getTime());
		return (Date) this.date.clone();
	}

	public void addSong(Song song) {
		// TODO Auto-generated method stub
		
	}

	public void add(String t) {
		// TODO Auto-generated method stub
		
	}

	public void add(Date u) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings(value = "unchecked")
	public <T> Addable<T> asWritable(Class<T> type) throws Exception {
		if (type == Song.class) {
			return (Addable<T>) new Addable<Song>() {

				public void add(Song t) {
					knownSongs.add(t);
					
				}
				
			};
		} else if (type == Artist.class) {
			return (Addable<T>) new Addable<Artist>() {

				public void add(Artist t) {
					//knownArtists.add(t);
					
				}
				
			};
		} else {
			throw new Exception();
		}
	}
	/*
	{
		this.asWritable(Song.class).add(null);
		this.asWritable(Artist.class).add(null);
	}
	*/
}
