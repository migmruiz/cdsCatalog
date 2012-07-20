/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.entities;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;

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
	public Iterable<Disc> getKnownDiscs() throws RepositoryException, ExecutionException;

	/*
	 * 
	 */
	public Iterable<Disc> getKnownMainDiscs() throws RepositoryException;

	/*
	 * 
	 */
	public Date getBirthday();

}
