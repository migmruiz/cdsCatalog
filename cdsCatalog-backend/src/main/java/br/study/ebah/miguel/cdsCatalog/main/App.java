package br.study.ebah.miguel.cdsCatalog.main;

import java.util.Date;

import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.ArtistImpl;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.DiscImpl;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryType;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) {
		try {
			ArtistImpl artist = new ArtistImpl("Eu", new Date(),
					RepositoryType.InMemory);
			DiscImpl disc = new DiscImpl("", RepositoryType.InMemory);
			disc.setId(1L);
			System.out.println("Adding disc: " + disc);
			artist.asWritable(Disc.class).add(disc);
			System.out.println("Artist's known discs: "
					+ artist.getKnownDiscs());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
