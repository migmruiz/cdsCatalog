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
import br.study.ebah.miguel.cdsCatalog.elements.Artist;
import br.study.ebah.miguel.cdsCatalog.elements.Disc;
import br.study.ebah.miguel.cdsCatalog.elements.Song;
import br.study.ebah.miguel.cdsCatalog.inMemory.elements.InMemoryArtistRW;
import br.study.ebah.miguel.cdsCatalog.sql.access.MySQLConnectionFactory;
import br.study.ebah.miguel.cdsCatalog.sql.access.SQLDBNoDataException;

/**
 * @author miguel
 * 
 */
public class MySQLArtist implements Artist, AutoCloseable {
	private final String name;
	private final long id;

	private InMemoryArtistRW artist;
	private Writable<Disc> discWritableArtist;

	private Connection con;
	private PreparedStatement nameStmt;
	private PreparedStatement idStmt;
	private PreparedStatement workingOnDiscsStmt;
	private boolean discsAreSetted;
	private MySQLConnectionFactory connFact;

	/*
	 * 
	 */
	public MySQLArtist(@Nonnull String name) throws SQLException,
			SQLDBNoDataException, ClassNotFoundException {
		setupGlobal();
		Preconditions.checkNotNull(name, "name cannot be null");
		this.name = name;
		Preconditions.checkState(!nameStmt.isClosed(),
				"cannot execute query if" + " statement is closed");
		nameStmt.setString(1, name);
		try (ResultSet rs = nameStmt.executeQuery()) {
			if (rs.first()) {
				this.id = rs.getLong("id_artist");
				setupArtist(rs);
			} else {
				throw new SQLDBNoDataException("no data on artist table");
			}
		}

	}

	/*
	 * 
	 */
	public MySQLArtist(@Nonnull Long id) throws SQLException,
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
				setupArtist(rs);
			} else {
				throw new SQLDBNoDataException("no data on artist table");
			}
		}
	}

	private final void setupGlobal() throws SQLException, ClassNotFoundException {
		this.connFact = new MySQLConnectionFactory();
		this.con = connFact.getConnection();
		this.nameStmt = con
				.prepareStatement("SELECT * FROM artist WHERE name=?");
		this.idStmt = con
				.prepareStatement("SELECT * FROM artist WHERE id_artist=?");
		this.workingOnDiscsStmt = con
				.prepareStatement("SELECT * FROM `disc_artist-workingOn`"
						+ " WHERE id_artist=?");
		this.discsAreSetted = false;
	}

	private void setupArtist(ResultSet rs) throws SQLException {
		java.sql.Date birthdaySQL = rs.getDate("birthday");
		if (birthdaySQL == null) {
			this.artist = new InMemoryArtistRW(this.name);
		} else {
			this.artist = new InMemoryArtistRW(this.name, new Date(
					birthdaySQL.getTime()));
		}
		// long mainDiscID = rs.getLong("id_disc");
		this.discWritableArtist = this.artist.asWritable(Disc.class);
		// this.discWritableArtist.add(new MySQLDisc(mainDiscID));
		// this.artist.setMain(new MySQLDisc(mainDiscID));

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
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Artist#getKnownDiscs()
	 */
	public Iterable<Disc> getKnownDiscs() {
		if (!this.discsAreSetted)
			try {
				workingOnDiscsStmt.setLong(1, this.id);
				try (ResultSet discs_rs = workingOnDiscsStmt.executeQuery()) {
					while (discs_rs.next()) {
						this.discWritableArtist.add(new MySQLDisc(discs_rs
								.getLong("id_artist")));
					}
				}
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		this.discsAreSetted = true;
		return this.artist.getKnownDiscs();
		// TODO write generate mainDiscs from discs's rows, and songs from
		// song's rows
		// Writable<Song> songWritableDisc = this.disc.asWritable(Song.class);
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Artist#getKnownMainDiscs()
	 */
	public Iterable<Disc> getKnownMainDiscs() {
		return this.artist.getKnownMainDiscs();
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Artist#getKnownSongs()
	 */
	public Iterable<Song> getKnownSongs() {
		return this.artist.getKnownSongs();
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Artist#getBirthday()
	 */
	public Date getBirthday() {
		return this.artist.getBirthday();
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
