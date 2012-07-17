/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.sql.elements;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.annotation.Nonnull;

import com.google.common.base.Preconditions;

import br.study.ebah.miguel.cdsCatalog.actions.Writable;
import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.inMemory.elements.InMemoryDiscRW;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;
import br.study.ebah.miguel.cdsCatalog.sql.access.SQLDBNoDataException;
import br.study.ebah.miguel.cdsCatalog.sql.access.MySQLConnectionFactory;

/**
 * @author miguel
 * 
 */
public class MySQLDisc implements Disc, AutoCloseable {

	private final long id;
	private final String name;
	private MySQLArtist mainArtist;

	private InMemoryDiscRW disc;
	private Writable<Artist> artistWritableDisc;

	private Connection con;
	private PreparedStatement nameStmt;
	private PreparedStatement idStmt;
	private PreparedStatement workingOnArtistsStmt;
	private boolean artistsAreSetted;
	private MySQLConnectionFactory connFact;

	/*
	 * 
	 */
	public MySQLDisc(@Nonnull String name) throws SQLException,
			SQLDBNoDataException, ClassNotFoundException {
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
	public MySQLDisc(@Nonnull Long id) throws SQLException,
			SQLDBNoDataException, ClassNotFoundException {
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

	private final void setupGlobal() throws SQLException, ClassNotFoundException {
		this.connFact = new MySQLConnectionFactory();
		this.con = connFact.getConnection();
		this.nameStmt = con
				.prepareStatement("SELECT * FROM disc WHERE name=?");
		this.idStmt = con
				.prepareStatement("SELECT * FROM disc WHERE id_disc=?");
		this.workingOnArtistsStmt = con
				.prepareStatement("SELECT * FROM `disc_artist-workingOn`"
						+ " WHERE id_disc=?");
		this.artistsAreSetted = false;
	}

	private void setupDisc(ResultSet rs) throws SQLException, SQLDBNoDataException, ClassNotFoundException {
		java.sql.Date releaseDateSQL = rs.getDate("releaseDate");
		if (releaseDateSQL == null) {
			this.disc = new InMemoryDiscRW(this.name);
		} else {
			this.disc = new InMemoryDiscRW(this.name, new Date(
					releaseDateSQL.getTime()));
		}
		long mainArtistID = rs.getLong("id_mainArtist");
		this.artistWritableDisc = this.disc.asWritable(Artist.class);
		mainArtist = new MySQLArtist(mainArtistID);
		this.artistWritableDisc.add(mainArtist);
		this.disc.setMain(mainArtist);
	}


	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Entity#getId()
	 */
	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Entity#isTransient()
	 */
	public boolean isTransient() {
		// TODO Auto-generated method stub
		return false;
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
		if (!this.artistsAreSetted)
			try {
				workingOnArtistsStmt.setLong(1, this.id);
				try (ResultSet artists_rs = workingOnArtistsStmt.executeQuery()) {
					while (artists_rs.next()) {
						this.artistWritableDisc.add(new MySQLArtist(artists_rs
								.getLong("id_artist")));
					}
				} catch (SQLDBNoDataException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		artistsAreSetted = true;
		return this.disc.getArtists();

		// TODO write and generate songs from song's row
		// Writable<Song> songWritableDisc = this.disc.asWritable(Song.class);
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Disc#getMainArtist()
	 */
	public Artist getMainArtist() throws RepositoryException {
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
