package br.study.ebah.miguel.cdsCatalog.web.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;
import javax.servlet.ServletException;

import org.hibernate.ObjectNotFoundException;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;

public class CdsDAO {

	private final Repository<Disc> discRepository;

	private Map<String, List<Map<String, String>>> cdsContainer;
	private List<Map<String, String>> authorLinks;
	private Map<String, String> cdLink;

	public CdsDAO(@Nonnull final Repository<Disc> discRepository) {

		this.discRepository = discRepository;

	}

	public final Map<String, List<Map<String, String>>> getContainerWithArtists()
			throws ServletException {
		cdsContainer = new ConcurrentHashMap<>();

		Set<Disc> discs = Collections.synchronizedSet(new HashSet<Disc>());
		boolean goOn = true;
		try {
			for (long i = 1L; goOn; i++) {
				Disc gotIt = null;
				try {
					gotIt = discRepository.getById(i);
					if (i > 10L) {
						System.err.println("FORCE limit: escaping main data"
								+ " fetch loop");
						goOn = false;
					}
				} catch (RepositoryException | ObjectNotFoundException e) {
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
					authorLinks = Collections
							.synchronizedList(new ArrayList<Map<String, String>>());
					cdsContainer.put(disc.getName(), authorLinks);
					for (Artist artist : disc.getArtists()) {
						cdLink = new ConcurrentHashMap<>();
						cdLink.put("artist", artist.getName());
						Artist mainArtist = disc.getMainArtist();
						if (artist.equals(mainArtist)) {
							cdLink.put("mainArtist", mainArtist.getName());
						}
						authorLinks.add(cdLink);
					}
				}
			} catch (NullPointerException | RepositoryException
					| ExecutionException e) {
				throw new ServletException(e);
			} catch (ObjectNotFoundException e) {
				// TODO avoid ObjectNotFoundException, that is occurring
				System.err.println(e.getClass().getSimpleName() + " - cause : "
						+ e.getCause() + " - entity : " + e.getEntityName());
				try {
					authorLinks.add(cdLink);
				} catch (Exception ex) {
					throw new ServletException(ex);
				}
			}
		}
		return cdsContainer;
	}

}
