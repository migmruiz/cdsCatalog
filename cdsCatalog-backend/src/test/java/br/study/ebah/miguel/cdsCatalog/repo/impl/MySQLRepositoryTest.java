package br.study.ebah.miguel.cdsCatalog.repo.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.PersistentArtist;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.PersistentDisc;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.TransientArtist;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.TransientDisc;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryFactory;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryType;

/**
 * @author miguel
 * 
 */
public class MySQLRepositoryTest {

	private static Disc disc;
	private static Repository<Disc> discRepository;

	@BeforeClass
	public static void setUp() throws Exception {
		discRepository = RepositoryFactory.getRepository(Disc.class,
				RepositoryType.MySQL);
		disc = discRepository.getById(1L);
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
	public void saveTest() throws Exception {
		Repository<Artist> artistRepository = RepositoryFactory.getRepository(
				Artist.class, RepositoryType.MySQL);
		
		TransientDisc transientDisc = new TransientDisc("Nevermind",
				new LocalDate(1991, 9, 24).toDate(), RepositoryType.MySQL);
		Disc persistentDisc = discRepository.save(transientDisc);
		transientDisc.setId(persistentDisc.getId());
		
		TransientArtist transientArtist = new TransientArtist("Dave Grohl",
				new LocalDate(1969, 1, 14).toDate(), RepositoryType.MySQL);
		Artist persistentArtist = artistRepository.save(transientArtist);
		transientArtist.setId(persistentArtist.getId());

		transientDisc.asWritable(Artist.class).add(transientArtist);
		transientDisc.setMain(transientArtist.getId());

		persistentDisc = new PersistentDisc(transientDisc);

		discRepository.save(persistentDisc);

		transientArtist.asWritable(Disc.class).add(persistentDisc);
		transientArtist.setMain(persistentDisc.getId());

		persistentArtist = artistRepository.save(new PersistentArtist(
				transientArtist));
	}

	@Test
	public void allDiscsPrintingTest() throws Exception {
		List<Disc> discs = new ArrayList<Disc>();
		boolean goOn = true;
		try {
			for (long i = 1L; goOn || i < 20L; i++) {
				try {
					discs.add(discRepository.getById(i));
				} catch (RepositoryException e) {
					goOn = false;
				}
			}
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

	@AfterClass
	public static void close() throws Exception {
		discRepository.close();
		RepositoryFactory.destroy();
	}

}
