/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.entities.jpa;

import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.study.ebah.miguel.cdsCatalog.entities.Composer;
import br.study.ebah.miguel.cdsCatalog.entities.Song;

/**
 * @author miguel
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_ONLY)
public class JPAComposer extends JPAArtist implements Composer {
	@OneToMany(mappedBy = "composer", targetEntity = JPASong.class, fetch = FetchType.LAZY)
	private Set<? extends Song> knownComposedSongs;

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
