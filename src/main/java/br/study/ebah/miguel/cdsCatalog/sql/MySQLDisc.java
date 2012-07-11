/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import br.study.ebah.miguel.cdsCatalog.actions.Writable;
import br.study.ebah.miguel.cdsCatalog.elements.Artist;
import br.study.ebah.miguel.cdsCatalog.elements.Disc;
import br.study.ebah.miguel.cdsCatalog.elements.Song;
import br.study.ebah.miguel.cdsCatalog.inMemory.InMemoryDiscRW;
import br.study.ebah.miguel.cdsCatalog.sql.access.SQLDBNoDataException;
import br.study.ebah.miguel.cdsCatalog.sql.access.MySQLConnectionFactory;

/**
 * @author miguel
 * 
 */
public class MySQLDisc implements Disc, AutoCloseable {
	private final String name;
	private Connection con;
	private PreparedStatement nameStmt;
	private PreparedStatement idStmt;
	private final long id;
	private InMemoryDiscRW disc;
	private final MySQLConnectionFactory connFact = new MySQLConnectionFactory();

	/*
	 * 
	 */
	public MySQLDisc(@Nonnull String name) throws SQLException, Exception {
		setupGlobal();
		Preconditions.checkNotNull(name, "name cannot be null");
		this.name = name;
		Preconditions.checkState(!nameStmt.isClosed(),
				"cannot execute query if" + " statement is closed");
		nameStmt.setString(1, name);
		try (ResultSet rs = nameStmt.executeQuery()) {
			if (rs.first()) {
				this.id = rs.getLong("id_disc");
				setupDisc(rs);
			} else {
				throw new SQLDBNoDataException("no data on disc table");
			}
		}
	}

	/*
	 * 
	 */
	public MySQLDisc(@Nonnull Long id) throws SQLException, Exception {
		setupGlobal();
		Preconditions.checkNotNull(id, "id cannot be null");
		this.id = id.longValue();
		Preconditions.checkState(!nameStmt.isClosed(),
				"cannot execute query if" + " statement is closed");
		idStmt.setLong(1, this.id);
		try (ResultSet rs = idStmt.executeQuery()) {
			if (rs.first()) {
				this.name = rs.getString("name");
				setupDisc(rs);
			} else {
				throw new SQLDBNoDataException("no data on disc table");
			}
		}
	}

	private final void setupGlobal() throws SQLException {
		this.con = connFact.getConnection();
		this.nameStmt = con.prepareStatement("SELECT * FROM disc WHERE name=?");
		this.idStmt = con
				.prepareStatement("SELECT * FROM disc WHERE id_disc=?");

	}

	private void setupDisc(ResultSet rs) throws SQLException {
		java.sql.Date releaseDateSQL = rs.getDate("releaseDate");
		if (releaseDateSQL == null) {
			this.disc = new InMemoryDiscRW(this.name);
		} else {
			this.disc = new InMemoryDiscRW(this.name, new Date(
					releaseDateSQL.getTime()));
		}
		long mainArtist = rs.getLong("id_mainArtist");
		Writable<Artist> artistWritableDisc = this.disc
				.asWritable(Artist.class);
		artistWritableDisc.add(new MySQLArtist(mainArtist));
		this.disc.setMain(new MySQLArtist(mainArtist));

		// TODO write generate artists from artist's row, and songs from song's
		// row
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
