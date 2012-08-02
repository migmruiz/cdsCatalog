package br.study.ebah.miguel.cdsCatalog.repo.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.ObjectNotFoundException;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.jpa.JPAArtist;
import br.study.ebah.miguel.cdsCatalog.entities.jpa.JPADisc;
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

//	@Test
	public void saveTest() throws Exception {
		JPAArtist localArtist = new JPAArtist();
		localArtist.setName("Antônio Carlos Jobim");
		localArtist.setBirthday(new LocalDate(1973, 1, 22).toDate());
		JPADisc localDisc = new JPADisc();
		localDisc.setName("Matita Perê");
		localDisc.setReleaseDate(new LocalDate(1973, 1, 22).toDate());
		Set<Artist> artists = new HashSet<>();
		artists.add(localArtist);
		localDisc.setArtists(artists);
		localDisc.setMainArtist(localArtist);
		Set<Disc> discs = new HashSet<>();
		discs.add(localDisc);
		localArtist.setKnownMainDiscs(discs);
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
