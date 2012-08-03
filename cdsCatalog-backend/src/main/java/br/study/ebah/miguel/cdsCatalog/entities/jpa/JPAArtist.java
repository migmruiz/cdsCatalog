/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.entities.jpa;

import java.util.Date;
import java.util.Set;

import javax.persistence.Cacheable;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.Song;

/**
 * @author miguel
 * 
 */
@Entity
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class JPAArtist implements Artist {

	@GeneratedValue
	@Id
	private Long id;

	private String name;
	private Date birthday;

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, targetEntity = JPASong.class, fetch = FetchType.LAZY)
	private Set<? extends Song> knownSongs;
	@ManyToMany(mappedBy = "artists", targetEntity = JPADisc.class, fetch = FetchType.LAZY)
	private Set<? extends Disc> knownDiscs;
	@OneToMany(mappedBy = "mainArtist", targetEntity = JPADisc.class, fetch = FetchType.LAZY)
	private Set<? extends Disc> knownMainDiscs;

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Entity#getId()
	 */
	@Override
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
	@Override
	public boolean isTransient() {
		return false;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Artist#getName()
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
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Artist#getBirthday()
	 */
	@Override
	public Date getBirthday() {
		if (this.birthday == null) {
			System.err.println("Unknown release date.");
			return new Date();
		} else {
			return (Date) this.birthday.clone();
		}
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Artist#getKnownSongs()
	 */
	@Override
	public Iterable<? extends Song> getKnownSongs() {
		return this.knownSongs;
	}

	public void setKnownSongs(Set<? extends Song> knownSongs) {
		this.knownSongs = knownSongs;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Artist#getKnownDiscs()
	 */
	@Override
	public Iterable<? extends Disc> getKnownDiscs() {
		return this.knownDiscs;
	}

	public void setKnownDiscs(Set<? extends Disc> knownDiscs) {
		this.knownDiscs = knownDiscs;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Artist#getKnownMainDiscs()
	 */
	@Override
	public Iterable<? extends Disc> getKnownMainDiscs() {
		return this.knownMainDiscs;
	}

	public void setKnownMainDiscs(Set<? extends Disc> knownMainDiscs) {
		this.knownMainDiscs = knownMainDiscs;
	}
}
