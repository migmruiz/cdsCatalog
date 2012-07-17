/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.entities;

import java.util.Date;

import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;

/**
 * @author miguel
 * 
 */
public interface Disc extends Entity {
	/*
	 * 
	 */
	public String getName();

	/*
	 * 
	 */
	public Iterable<Artist> getArtists() throws RepositoryException;

	/*
	 * 
	 */
	public Artist getMainArtist() throws RepositoryException;

	/*
	 * 
	 */
	public Iterable<Song> getSongs() throws RepositoryException;

	/*
	 * 
	 */
	public Date getReleaseDate();
}
