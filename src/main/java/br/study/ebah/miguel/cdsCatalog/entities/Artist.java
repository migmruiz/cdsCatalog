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
public interface Artist extends Entity {
	/*
	 * 
	 */
	public String getName();

	/*
	 * 
	 */
	public Iterable<Song> getKnownSongs() throws RepositoryException;

	/*
	 * 
	 */
	public Iterable<Disc> getKnownDiscs() throws RepositoryException;

	/*
	 * 
	 */
	public Iterable<Disc> getKnownMainDiscs() throws RepositoryException;

	/*
	 * 
	 */
	public Date getBirthday();

}
