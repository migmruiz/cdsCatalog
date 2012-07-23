package br.study.ebah.miguel.cdsCatalog.entities.impl;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Composer;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryFactory;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryType;

/**
 * 
 * @author miguel
 * 
 */
class SongFromRepo implements Song {
	private final Long id;

	private final Song song;

	SongFromRepo(final Long id, RepositoryType store)
			throws RepositoryException, ExecutionException {

		this.id = id;
		this.song = RepositoryFactory.getRepository(Song.class, store)
				.getById(id);
	}
	

	@Override
	public Long getId() {
		return this.id;
	}

	@Override
	public boolean isTransient() {
		return false;
	}
	
	@Override
	public String getName() {
		return this.song.getName();
	}


	@Override
	public Composer getComposer() throws RepositoryException {
		return this.song.getComposer();
	}

	@Override
	public String getLyrics() {
		return this.song.getLyrics();
	}

	@Override
	public Date getFirstReleaseDate() {
		return this.song.getFirstReleaseDate();
	}

	@Override
	public Iterable<Artist> getKnownArtists() throws RepositoryException {
		return this.song.getKnownArtists();
	}

	@Override
	public Iterable<Disc> getKnownDiscs() throws RepositoryException {
		return this.song.getKnownDiscs();
	}

}