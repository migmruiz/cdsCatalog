package br.study.ebah.miguel.cdsCatalog.main;

import java.util.Date;

import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.impl.InMemoryDisc;
import br.study.ebah.miguel.cdsCatalog.entities.impl.admin.TransientArtist;
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
			Disc disc = new InMemoryDisc("", new Date());
			System.out.println("Adding disc: " + disc);
			artist.asWritable(Disc.class).add(disc.getId());
			System.out.println("Artist's known discs: "
					+ artist.getKnownDiscs());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
