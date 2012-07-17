/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.entities;

import java.util.Date;

import br.study.ebah.miguel.cdsCatalog.actions.RepositoryException;

/**
 * @author miguel
 * 
 */
public interface Song extends Entity {
	/*
	 * 
	 */
	public String getName();

	/*
	 * 
	 */
	public Composer getComposer() throws RepositoryException;

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
	public Iterable<Artist> getKnownArtists() throws RepositoryException;

	/*
	 * 
	 */
	public Iterable<Disc> getKnownDiscs() throws RepositoryException;

}
