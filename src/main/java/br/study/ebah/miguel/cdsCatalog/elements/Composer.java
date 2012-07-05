/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.elements;

/**
 * @author miguel
 *
 */
public interface Composer extends Artist{
	
	public Iterable<Song> getKnownComposedSongs();

}
