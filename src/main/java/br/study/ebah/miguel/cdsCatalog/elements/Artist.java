/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.elements;

import java.util.Date;

import br.study.ebah.miguel.cdsCatalog.actions.Addable;

/**
 * @author miguel
 * 
 */
public interface Artist {
	public String getName();

	public Iterable<Song> getKnownSongs();

	public Iterable<Disc> getKnownDiscs();

	public Iterable<Disc> getKnownMainDiscs();

	public void setMain(Disc disc);

	public Date getBirthday();

	public <T> Addable<T> asAddable(Class<T> type) throws Throwable;

}
