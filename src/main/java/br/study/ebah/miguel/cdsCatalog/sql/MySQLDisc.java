/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.sql;

import java.sql.Connection;
import java.util.Date;

import br.study.ebah.miguel.cdsCatalog.actions.Writable;
import br.study.ebah.miguel.cdsCatalog.elements.Artist;
import br.study.ebah.miguel.cdsCatalog.elements.Disc;
import br.study.ebah.miguel.cdsCatalog.elements.Song;
import br.study.ebah.miguel.cdsCatalog.inMemory.InMemoryDiscRW;
import br.study.ebah.miguel.cdsCatalog.sql.acccess.MySQLConnectionFactory;

/**
 * @author miguel
 * 
 */
public class MySQLDisc implements Disc {
	private final String name;
	private final Connection con;
	private final long id;
	private final InMemoryDiscRW disc;

	/*
	 * 
	 */
	public MySQLDisc(String name, MySQLConnectionFactory con) {
		this.name = name;
		this.con = con.getConnection();
		// TODO connect and retrieve id
		this.id = con.hashCode();
		java.sql.Date ds = null;
		Date d = null;
		this.disc = new InMemoryDiscRW(name, d);
		this.disc.setMain(null);
		Writable<Artist> artistWritableDisc = this.disc.asWritable(Artist.class);
		Writable<Song> songWritableDisc = this.disc.asWritable(Song.class);

	}

	/*
	 * 
	 */
	public MySQLDisc(long id, MySQLConnectionFactory con) {
		this.id = id;
		this.con = con.getConnection();
		// TODO connect and retrieve name and releaseDate
		this.name = con.toString();
		java.sql.Date ds = null;
		Date d = null;
		this.disc = new InMemoryDiscRW(name, d);
		this.disc.setMain(null);
		Writable<Artist> artistWritableDisc = this.disc.asWritable(Artist.class);
		Writable<Song> songWritableDisc = this.disc.asWritable(Song.class);
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Disc#getName()
	 */
	public String getName() {
		return this.name;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Disc#getArtists()
	 */
	public Iterable<Artist> getArtists() {
		return this.disc.getArtists();
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Disc#getMainArtist()
	 */
	public Artist getMainArtist() {
		return this.disc.getMainArtist();
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Disc#getSongs()
	 */
	public Iterable<Song> getSongs() {
		return this.disc.getSongs();
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Disc#getReleaseDate()
	 */
	public Date getReleaseDate() {
		return this.disc.getReleaseDate();
	}

	/*
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Override Object method
		return super.equals(obj);
	}

	/*
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Override Object method
		return getClass().getName() + "@" + Integer.toHexString(hashCode());
	}

	/*
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public int hashCode() {
		// TODO Override Object method
		return super.hashCode();
	}

}
