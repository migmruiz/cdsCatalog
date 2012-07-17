package br.study.ebah.miguel.cdsCatalog.entities.impl.admin;

import java.util.Date;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;

import com.google.common.base.Optional;


/**
 * 
 * @author bruno
 *
 */
public class TransientArtist implements Artist {

	private String name;
	private Optional<Integer> id = Optional.absent();

	@Override
	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public int getId() {
		return id.get();
	}

	@Override
	public boolean isTransient() {
		return true;
	}

	public void setId(final int id) {
		this.id = Optional.of(id);
	}

	@Override
	public Iterable<Song> getKnownSongs() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Disc> getKnownDiscs() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<Disc> getKnownMainDiscs() throws RepositoryException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date getBirthday() {
		// TODO Auto-generated method stub
		return null;
	}
}
