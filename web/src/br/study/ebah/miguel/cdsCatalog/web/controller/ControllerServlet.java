package br.study.ebah.miguel.cdsCatalog.web.controller;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.LocalDate;

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Composer;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryFactory;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryType;
import br.study.ebah.miguel.cdsCatalog.web.model.CdsDAO;

@SuppressWarnings("serial")
public class ControllerServlet extends HttpServlet {

	/*
	 * time-stamps
	 */
	private static final LocalDate creationDate = new LocalDate(2012, 7, 24);
	private static final LocalDate lastModifiedDate = new LocalDate(2012, 7, 29);
	private static final int expiresTimeInDays = 90;

	// @Override
	// public void init(ServletConfig config) throws ServletException {
	// super.init(config);
	//
	// }

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setCharacterEncoding("UTF-8");
		response.setDateHeader("Expires", lastModifiedDate.toDate().getTime()
				+ expiresTimeInDays * 24 * 60 * 60 * 1000);

		String title = "cdsCatalog";
		String author = "Miguel Mendes Ruiz";
		String date = creationDate.yearOfEra().getAsText()
				+ String.format("%02d", creationDate.monthOfYear().get())
				+ creationDate.dayOfMonth().getAsText();

		request.setAttribute("title", title);
		request.setAttribute("author", author);
		request.setAttribute("date", date);
		
		try (Repository<Artist> artistRepository = RepositoryFactory
				.getRepository(Artist.class, RepositoryType.Hibernate);
				Repository<Composer> composerRepository = RepositoryFactory
						.getRepository(Composer.class, RepositoryType.Hibernate);
				Repository<Disc> discRepository = RepositoryFactory
						.getRepository(Disc.class, RepositoryType.Hibernate);
				Repository<Song> songRepository = RepositoryFactory
						.getRepository(Song.class, RepositoryType.Hibernate)) {
			artistRepository.initialize();
			composerRepository.initialize();
			discRepository.initialize();
			songRepository.initialize();
			CdsDAO cdsDAO = new CdsDAO(discRepository);
			
			request.setAttribute("cdsWithArtistsContainer",
					cdsDAO.getContainerWithArtists());

		} catch (ExecutionException e) {
			throw new ServletException(e);
		} catch (Exception e1) {
			throw new ServletException(e1);
		}
		request.getRequestDispatcher("/WEB-INF/pages/MainPage.jsp").forward(
				request, response);
	}

	// @Override
	// public long getLastModified(HttpServletRequest req) {
	// return lastModifiedDate.toDate().getTime();
	// return System.currentTimeMillis();
	// }
}