/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.entities;

/**
 * @author miguel
 * 
 */
public interface Composer extends Artist {
	/*
	 * 
	 */
	public Iterable<? extends Song> getKnownComposedSongs();

}
