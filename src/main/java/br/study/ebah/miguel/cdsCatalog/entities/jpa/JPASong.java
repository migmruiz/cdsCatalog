/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.entities.jpa;

import java.util.Date;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Target;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Composer;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;

/**
 * @author miguel
 * 
 */
@Entity
public class JPASong implements Song {

	@GeneratedValue
	@Id
	private Long id;

	private String name;
	private Date firstReleaseDate;

	@ManyToMany(mappedBy = "songs", targetEntity = JPADisc.class, fetch = FetchType.LAZY)
	private Set<? extends Disc> knownDiscs;
	@ManyToMany(mappedBy = "knownSongs", targetEntity = JPAArtist.class, fetch = FetchType.LAZY)
	private Set<? extends Artist> knownArtists;
	@ManyToOne(fetch = FetchType.LAZY, optional = true)
	@Target(JPAComposer.class)
	private Composer composer;

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Entity#getId()
	 */
	public Long getId() {
		return this.id;
	}

	@SuppressWarnings("unused")
	private void setId(Long id) {
		this.id = id;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Entity#isTransient()
	 */
	public boolean isTransient() {
		return false;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Song#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Song#getKnownArtists()
	 */
	@Override
	public Iterable<? extends Artist> getKnownArtists() {
		return this.knownArtists;
	}

	public void setKnownArtists(Set<? extends Artist> knownArtists) {
		this.knownArtists = knownArtists;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Song#getComposer()
	 */
	@Override
	public Composer getComposer() throws RepositoryException {
		return this.composer;
	}

	public void setComposer(Composer composer) {
		this.composer = composer;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Song#getLyrics()
	 */
	@Override
	public String getLyrics() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Song#getFirstReleaseDate()
	 */
	@Override
	public Date getFirstReleaseDate() {
		if (this.firstReleaseDate == null) {
			System.err.println("Unknown release date.");
			return new Date();
		} else {
			return (Date) this.firstReleaseDate.clone();
		}
	}

	public void setFirstReleaseDate(Date firstReleaseDate) {
		this.firstReleaseDate = firstReleaseDate;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Song#getKnownDiscs()
	 */
	@Override
	public Iterable<? extends Disc> getKnownDiscs() throws RepositoryException {
		return this.knownDiscs;
	}

	public void setKnownDiscs(Set<? extends Disc> knownDiscs) {
		this.knownDiscs = knownDiscs;
	}

}
