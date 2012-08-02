package br.study.ebah.miguel.cdsCatalog.repo.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.ObjectNotFoundException;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
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
public class HibernateRepositoryTest {

	private static Disc disc;
	private static Artist artist;
	private static Repository<Disc> discRepository;
	private static Repository<Artist> artistRepository;

	@BeforeClass
	public static void setUp() throws Exception {
		discRepository = RepositoryFactory.getRepository(Disc.class,
				RepositoryType.Hibernate);
		artistRepository = RepositoryFactory.getRepository(Artist.class,
				RepositoryType.Hibernate);

		discRepository.initialize();
		artistRepository.initialize();
	}

	// @Test
	public void saveTest() throws Exception {
		TransientArtist localArtist = new TransientArtist(
				"Antônio Carlos Jobim", new LocalDate(1973, 1, 22).toDate(),
				RepositoryType.Hibernate);
		localArtist.setId(1L);
		TransientDisc localDisc = new TransientDisc("Matita Perê",
				new LocalDate(1973, 1, 22).toDate(), RepositoryType.Hibernate);
		localDisc.setId(1L);
		localDisc.asWritable(Artist.class).add(localArtist.getId());
		localDisc.setMain(localArtist.getId());
		localArtist.asWritable(Disc.class).add(localDisc.getId());
		localArtist.setMain(localDisc.getId());
		artistRepository.save(localArtist);
		discRepository.save(localDisc);
	}

	@Test
	public void getByIdTest() throws Exception {
		disc = discRepository.getById(1L);
		artist = artistRepository.getById(1L);
		Assert.assertNotNull(artist);
		Assert.assertNotNull(disc);
	}

	@Test
	public void getNameTest() {
		Assert.assertNotNull(artist.getName());
		Assert.assertNotNull(disc.getName());
	}

	@Test
	public void getMainArtistTest() throws Exception {
		Assert.assertNotNull(disc.getMainArtist().getName());
		Assert.assertEquals(artist, disc.getMainArtist());
	}

	@Test
	public void allDiscsPrintingTest() throws Exception {
		List<Disc> discs = new ArrayList<Disc>();
		boolean goOn = true;
		try {
			for (long i = 1L; goOn; i++) {
				Disc gotIt = null;
				try {
					gotIt = discRepository.getById(i);
					if (i > 1L) {
						System.err.println("FORCE limit");
						goOn = false;
					}
				} catch (RepositoryException e) {
					goOn = false;
				} finally {
					if (goOn) {
						discs.add(gotIt);
					}
				}
			}
		} finally {
			try {
				for (Disc disc : discs) {
					System.out.println(disc.getName() + " - "
							+ disc.getMainArtist().getName());
				}
			} catch (NullPointerException | ObjectNotFoundException e) {
				Assert.fail(e.getMessage());
			}
		}
	}

	@AfterClass
	public static void close() throws Exception {
		artistRepository.close();
		discRepository.close();
	}

}
