/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.entities.jpa;

import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import br.study.ebah.miguel.cdsCatalog.entities.Composer;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;

/**
 * @author miguel
 * 
 */
@Entity
public class JPAComposer extends JPAArtist implements Composer {
	@OneToMany(mappedBy = "composer", targetEntity = JPASong.class)
	private Set<? extends Song> knownComposedSongs;

	public JPAComposer(Composer composer) throws RepositoryException,
			ExecutionException {
		super(composer);
		setKnownComposedSongs((Set<? extends Song>) composer.getKnownComposedSongs());
	}

	/*
	 * 
	 * @see
	 * br.study.ebah.miguel.cdsCatalog.entities.Composer#getKnownComposedSongs()
	 */
	@Override
	public Iterable<? extends Song> getKnownComposedSongs() {
		return this.knownComposedSongs;
	}

	public void setKnownComposedSongs(Set<? extends Song> knownComposedSongs) {
		this.knownComposedSongs = knownComposedSongs;
	}

}
