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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CdsDAO {

	final Logger logger = LoggerFactory.getLogger(CdsDAO.class);
	private final Repository<Disc> discRepository;

	public CdsDAO(@Nonnull final Repository<Disc> discRepository) {

		this.discRepository = discRepository;

	}

	public Map<String, List<Map<String, String>>> getContainerWithArtists()
			throws ServletException {
		Map<String, List<Map<String, String>>> cdsContainer = new ConcurrentHashMap<>();

		Set<Disc> discs = Collections.synchronizedSet(new HashSet<Disc>());
		boolean goOn = true;
		try {
			for (long i = 1L; goOn; i++) {
				Disc gotIt = null;
				try {
					gotIt = discRepository.getById(i);
					if (i > 1L) {
						logger.info("dao FORCE limit: escaping main data"
								+ " fetch loop");
						goOn = false;
					}
				} catch (RepositoryException e) {
					goOn = false;
				} catch (ObjectNotFoundException e) {
					goOn = false;
					// TODO avoid ObjectNotFoundException, that is occurring
					logger.info("dao on load " + e.getClass().getSimpleName()
							+ " - cause : " + e.getCause() + " - entity : "
							+ e.getEntityName());
				} finally {
					if (goOn) {
						discs.add(gotIt);
					}
				}
			}
		} finally {
			try {
				for (Disc disc : discs) {
					logger.debug("dao on read flag 1");
					List<Map<String, String>> authorLinks = Collections
							.synchronizedList(new ArrayList<Map<String, String>>());

					logger.trace("dao on read flag 1.1");
					try {
						cdsContainer.put(disc.getName(), authorLinks);
					} catch (Exception e) {
						logger.trace("dao on read error flag 1.2: "
								+ e.getMessage());
					}
					logger.debug("dao on read flag 2");
					for (Artist artist : disc.getArtists()) {
						logger.debug("dao on read flag 3");
						Map<String, String> cdLink = new ConcurrentHashMap<>();
						cdLink.put("artist", artist.getName());
						Artist mainArtist = disc.getMainArtist();
						if (artist.equals(mainArtist)) {
							cdLink.put("mainArtist", mainArtist.getName());
							logger.debug("dao on read flag 4");
						}
						authorLinks.add(cdLink);
					}
				}
			} catch (NullPointerException | RepositoryException
					| ExecutionException e) {
				throw new ServletException(e);
			} catch (ObjectNotFoundException e) {
				// TODO avoid ObjectNotFoundException, that is occurring
				logger.warn("dao on read " + e.getClass().getSimpleName()
						+ " - cause : " + e.getCause() + " - entity : "
						+ e.getEntityName());
			}
		}
		return cdsContainer;
	}

}
