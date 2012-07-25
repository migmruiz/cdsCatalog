package br.study.ebah.miguel.cdsCatalog.web.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryFactory;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryType;

public class CdsDAO {

	private final Repository<Disc> discRepository;

	private Map<String, List<Map<String, String>>> cdsContainer;
	private List<Map<String, String>> authorLinks;
	private Map<String, String> cdLink;

	public CdsDAO() throws ServletException {
		try {
			discRepository = RepositoryFactory.getRepository(Disc.class,
					RepositoryType.MySQL);
		} catch (ExecutionException e) {
			throw new ServletException(e);
		}
	}

	public final Map<String, List<Map<String, String>>> getContainerWithArtists()
			throws ServletException {
		cdsContainer = new ConcurrentHashMap<>();

		List<Disc> discs = Collections.synchronizedList(new ArrayList<Disc>());
		boolean goOn = true;
		try {
			for (long i = 1L; goOn; i++) {
				try {
					discs.add(discRepository.getById(i));
				} catch (RepositoryException e) {
					goOn = false;
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
						if (artist.equals(disc.getMainArtist())) {
							cdLink.put("mainArtist", disc.getMainArtist()
									.getName());
						}
						authorLinks.add(cdLink);
					}
				}
			} catch (NullPointerException | RepositoryException
					| ExecutionException e) {
				throw new ServletException(e);
			}
		}
		return cdsContainer;

	}

}
