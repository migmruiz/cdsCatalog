package br.study.ebah.miguel.cdsCatalog.entities.impl;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;

import br.study.ebah.miguel.cdsCatalog.entities.Composer;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryFactory;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryType;

/**
 * 
 * @author bruno, miguel
 * 
 */
class ComposerFromRepo implements Composer {
	private final Long id;

	private final Composer composer;

	public ComposerFromRepo(@Nonnull final Long id,
			@Nonnull RepositoryType store) throws RepositoryException,
			ExecutionException {

		this.id = id;
		this.composer = RepositoryFactory.getRepository(Composer.class, store)
				.getById(id);
	}

	@Override
	public String getName() {
		return this.composer.getName();
	}

	@Override
	@Nonnull
	public Long getId() {
		return this.id;
	}

	@Override
	public boolean isTransient() {
		return false;
	}

	@Override
	public Iterable<? extends Song> getKnownSongs() throws RepositoryException {
		return this.composer.getKnownSongs();
	}

	@Override
	public Iterable<? extends Disc> getKnownDiscs() throws RepositoryException,
			ExecutionException {
		return this.composer.getKnownDiscs();
	}

	@Override
	public Iterable<? extends Disc> getKnownMainDiscs() throws RepositoryException {
		return this.composer.getKnownMainDiscs();
	}

	@Override
	public Date getBirthday() {
		return this.composer.getBirthday();
	}

	@Override
	public Iterable<? extends Song> getKnownComposedSongs() {
		return this.composer.getKnownComposedSongs();
	}
}