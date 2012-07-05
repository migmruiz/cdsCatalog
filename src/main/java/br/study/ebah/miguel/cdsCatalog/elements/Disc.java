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
	public Iterable<Artist> getArtists();

	public Artist getMainArtist();

	public Iterable<Song> getSongs();

	public Date getReleaseDate();
}
