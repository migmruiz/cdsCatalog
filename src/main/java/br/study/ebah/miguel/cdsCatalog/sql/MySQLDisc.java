/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import br.study.ebah.miguel.cdsCatalog.elements.Artist;
import br.study.ebah.miguel.cdsCatalog.elements.Disc;
import br.study.ebah.miguel.cdsCatalog.elements.Song;
import br.study.ebah.miguel.cdsCatalog.inMemory.InMemoryDiscRW;
import br.study.ebah.miguel.cdsCatalog.sql.access.MySQLConnectionFactory;

/**
 * @author miguel
 * 
 */
public class MySQLDisc implements Disc, AutoCloseable {
	private String name;
	private static final Connection con;
	private static PreparedStatement nameStmt;
	private static PreparedStatement idStmt;
	private long id;
	private InMemoryDiscRW disc;

	static {
		con = new MySQLConnectionFactory().getConnection();
		try {
			nameStmt = con.prepareStatement("SELECT * FROM disc WHERE name=?");
			idStmt = con.prepareStatement("SELECT * FROM disc WHERE id_disc=?");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * 
	 */
	public MySQLDisc(String name) throws SQLException {
		this.name = name;
		nameStmt.setString(1, name);
		try (ResultSet rs = nameStmt.executeQuery()) {
			if (rs.first()) {
				this.id = rs.getLong("id_disc");
				java.sql.Date ds = rs.getDate("releaseDate");
				if (ds == null) {
					this.disc = new InMemoryDiscRW(this.name);
				} else {
					this.disc = new InMemoryDiscRW(this.name, new Date(
							ds.getTime()));
				}
			}
			// TODO generate Artist from artist's row
			// this.disc.setMain(null);
		}
		// TODO write generate Artist from artist's row, and Song from song's
		// row
		// Writable<Artist> artistWritableDisc = this.disc
		// .asWritable(Artist.class);
		// Writable<Song> songWritableDisc = this.disc.asWritable(Song.class);

	}

	/*
	 * 
	 */
	public MySQLDisc(long id) throws SQLException {
		this.id = id;

		idStmt.setLong(1, this.id);
		try (ResultSet rs = idStmt.executeQuery()) {
			if (rs.first()) {
				this.name = rs.getString("name");
				java.sql.Date ds = rs.getDate("releaseDate");
				if (ds == null) {
					this.disc = new InMemoryDiscRW(this.name);
				} else {
					this.disc = new InMemoryDiscRW(this.name, new Date(
							ds.getTime()));
				}
			}
		}
		// TODO write generate Artist from artist's row, and Song from song's
		// row
		// Writable<Artist> artistWritableDisc = this.disc
		// .asWritable(Artist.class);
		// Writable<Song> songWritableDisc = this.disc.asWritable(Song.class);
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

	/*
	 * 
	 * @see java.lang.AutoCloseable#close()
	 */
	@Override
	public void close() throws Exception {
		idStmt.close();
		nameStmt.close();
		con.close();
	}

}
