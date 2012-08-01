package br.study.ebah.miguel.cdsCatalog.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Composer;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryFactory;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryType;

public class RepositoryFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		try (Repository<Artist> artistRepository = RepositoryFactory
				.getRepository(Artist.class, RepositoryType.Hibernate);
				Repository<Composer> composerRepository = RepositoryFactory
						.getRepository(Composer.class, RepositoryType.Hibernate);
				Repository<Disc> discRepository = RepositoryFactory
						.getRepository(Disc.class, RepositoryType.Hibernate);
				Repository<Song> songRepository = RepositoryFactory
						.getRepository(Song.class, RepositoryType.Hibernate)) {
			artistRepository.init();
			composerRepository.init();
			discRepository.init();
			songRepository.init();

			chain.doFilter(request, response);
		} catch (Throwable ex) {
			ex.printStackTrace();
			throw new ServletException(ex);
		}
	}

	public void init(FilterConfig filterConfig) throws ServletException {
	}

	public void destroy() {
	}

}
