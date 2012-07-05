/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.elements;

import java.util.Date;

/**
 * @author miguel
 * 
 */
public interface Artist {
	public String getName();

	public Iterable<Song> getKnownSongs();

	public Iterable<Disc> getKnownDiscs();

	public Iterable<Disc> getKnownMainDiscs();

	// public void setMain(Disc disc);

	public Date getBirthday();

}
