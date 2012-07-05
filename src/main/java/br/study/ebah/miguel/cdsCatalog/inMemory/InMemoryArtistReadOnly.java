/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.inMemory;

import java.util.Date;

import br.study.ebah.miguel.cdsCatalog.actions.Addable;
import br.study.ebah.miguel.cdsCatalog.elements.Artist;

/**
 * @author miguel
 *
 */
public class InMemoryArtistReadOnly extends InMemoryArtist {

	public InMemoryArtistReadOnly(Artist other) {
		super(other);
	}
	
	public InMemoryArtistReadOnly(String name, Date birthday) {
		super(name, birthday);
	}
	
	@Override
	public <T> Addable<T> asAddable(Class<T> type) throws Exception {
		throw new IllegalAccessError();
	}

}
