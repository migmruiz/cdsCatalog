package br.study.ebah.miguel.cdsCatalog.main;

import java.util.Date;

import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.TransientArtist;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.TransientDisc;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryType;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) {
		try {
			TransientArtist artist = new TransientArtist("Eu", new Date(),
					RepositoryType.InMemory);
			TransientDisc disc = new TransientDisc("", RepositoryType.InMemory);
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
