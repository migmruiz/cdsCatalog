/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.inMemory.specifics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.study.ebah.miguel.cdsCatalog.elements.Composer;
import br.study.ebah.miguel.cdsCatalog.elements.Song;
import br.study.ebah.miguel.cdsCatalog.inMemory.InMemoryArtist;

/**
 * @author miguel
 * 
 */
public class InMemoryComposer extends InMemoryArtist implements Composer {
	final List<Song> knownComposedSongs;
	final List<Song> knownMainComposedSongs;

	/*
	 * 
	 */
	public InMemoryComposer(String name) {
		super(name);
		this.knownComposedSongs = Collections
				.synchronizedList(new ArrayList<Song>());
		this.knownMainComposedSongs = Collections
				.synchronizedList(new ArrayList<Song>());

	}

	/*
	 * 
	 */
	public InMemoryComposer(String name, Date birthday) {
		super(name, birthday);
		this.knownComposedSongs = Collections
				.synchronizedList(new ArrayList<Song>());
		this.knownMainComposedSongs = Collections
				.synchronizedList(new ArrayList<Song>());
	}

	public InMemoryComposer(Composer other) {
		super(other);
		this.knownComposedSongs = Collections
				.synchronizedList(new ArrayList<Song>());
		this.knownMainComposedSongs = Collections
				.synchronizedList(new ArrayList<Song>());
	}

	/*
	 * 
	 * @see
	 * br.study.ebah.miguel.cdsCatalog.elements.Composer#getKnownComposedSongs()
	 */
	public Iterable<Song> getKnownComposedSongs() {
		return this.knownComposedSongs;
	}

}
