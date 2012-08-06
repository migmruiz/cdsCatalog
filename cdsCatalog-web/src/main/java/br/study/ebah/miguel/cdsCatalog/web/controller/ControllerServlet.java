package br.study.ebah.miguel.cdsCatalog.web.controller;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

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
	private static final LocalDate lastModifiedDate = new LocalDate(2012, 8, 6);
	private static final int expiresTimeInDays = 90;

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setCharacterEncoding("UTF-8");
		response.setDateHeader("Expires", lastModifiedDate.toDate().getTime()
				+ TimeUnit.DAYS.toMillis(expiresTimeInDays));

		String title = "cdsCatalog";
		String author = "Miguel Mendes Ruiz";

		DateTimeFormatter dateTimeFormatter = DateTimeFormat
				.forPattern("yyyyMMdd");

		String creationDateStr = creationDate.toString(dateTimeFormatter);

		System.out.println(creationDateStr);

		request.setAttribute("title", title);
		request.setAttribute("author", author);
		request.setAttribute("date", creationDateStr);

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

		} catch (Exception e) {
			throw new ServletException(e);
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