/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.elements;

import java.util.Date;

/**
 * @author miguel
 * 
 */
public interface Disc {
	public String getName();
	
	public Iterable<Artist> getArtists();

	public Artist getMainArtist();
	
	// public void setMain(Artist artist);

	public Iterable<Song> getSongs();

	public Date getReleaseDate();
}
