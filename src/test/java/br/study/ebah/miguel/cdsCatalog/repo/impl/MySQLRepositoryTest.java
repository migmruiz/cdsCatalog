package br.study.ebah.miguel.cdsCatalog.repo.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryFactory;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryType;
import br.study.ebah.miguel.cdsCatalog.sql.SQLDBNoDataException;

/**
 * @author miguel
 * 
 */
public class MySQLRepositoryTest {

	private Disc disc;
	private Repository<Disc> discRepository;

	@Before
	public void setUp() throws Exception {
		discRepository = RepositoryFactory.getRepository(Disc.class,
				RepositoryType.MySQL);
		this.disc = discRepository.getById(1L);
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
	public void getMainArtistTest() throws RepositoryException,
			ExecutionException {
		Assert.assertNotNull(disc.getMainArtist().getName());
	}

	@Test
	public void allDiscsPrintingTest() throws Exception {
		List<Disc> discs = new ArrayList<Disc>();
		boolean goOn = true;
		try {
			for (long i = 1L; goOn; i++) {
				discs.add(discRepository.getById(i));
			}
		} catch (SQLDBNoDataException e) {
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
		discRepository.close();
	}

}
