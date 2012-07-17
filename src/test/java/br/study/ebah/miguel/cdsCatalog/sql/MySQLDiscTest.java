/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.sql;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;

import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;
import br.study.ebah.miguel.cdsCatalog.sql.access.SQLDBNoDataException;
import br.study.ebah.miguel.cdsCatalog.sql.elements.MySQLDisc;

/**
 * @author miguel
 * 
 */
public class MySQLDiscTest {
	private Disc disc;

	@Before
	public void setUp() throws Exception {
		this.disc = new MySQLDisc(1L);
	}

	@Test
	public void constructorTest() {
		Assert.assertNotNull(disc);
	}

	@Test
	public void getNameTest() {
		Assert.assertNotNull(disc.getName());
	}

	@Test
	public void getMainArtistTest() throws RepositoryException {
		Assert.assertNotNull(disc.getMainArtist().getName());
	}

	@Test
	public void allDiscsPrintingTest() throws Exception {
		List<Disc> discs = new ArrayList<Disc>();
		boolean goOn = true;
		try {
			for (long i = 1L; goOn; i++) {
				discs.add(new MySQLDisc(i));
			}
		} catch (SQLException | SQLDBNoDataException e) {
			goOn = false;
		} finally {
			try {
				for (Disc disc : discs) {
					System.out.println(disc.getName() + " - "
							+ disc.getMainArtist().getName());
				}
			} catch (NullPointerException e) {
				Assert.fail(e.getMessage());
			}
		}
	}

	@After
	public void close() throws Exception {
		if (disc != null && disc instanceof MySQLDisc) {
			((MySQLDisc) disc).close();
		}
	}

}
