package br.study.ebah.miguel.cdsCatalog.repo;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Composer;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.Entity;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.PersistentArtist;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.PersistentComposer;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.PersistentDisc;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.PersistentSong;

public class Persistence<T extends Entity> {

	private final T entity;

	public Persistence(final T entity) {
		this.entity = entity;
	}

	@SuppressWarnings("unchecked")
	public T entity() {
		if (entity instanceof Artist) {
			return (T) new PersistentArtist((Artist) entity);
		} else if (entity instanceof Composer) {
			return (T) new PersistentComposer((Composer) entity);
		} else if (entity instanceof Disc) {
			return (T) new PersistentDisc((Disc) entity);
		} else if (entity instanceof Song) {
			return (T) new PersistentSong((Song) entity);
		} else {
			throw new AssertionError("Unknown Entity class for Persistence");
		}
	}

}
