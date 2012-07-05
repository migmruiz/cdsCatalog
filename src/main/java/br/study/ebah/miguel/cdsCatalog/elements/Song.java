/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.elements;

import java.util.Date;

/**
 * @author miguel
 * 
 */
public interface Song {
	/*
	 * 
	 */
	public String getName();

	/*
	 * 
	 */
	public Composer getComposer();

	/*
	 * 
	 */
	public String getLyrics();

	/*
	 * 
	 */
	public Date getFirstReleaseDate();

	/*
	 * 
	 */
	public Iterable<Artist> getKnownArtists();

	/*
	 * 
	 */
	public Iterable<Disc> getKnownDiscs();

}
