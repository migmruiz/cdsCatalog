package br.study.ebah.miguel.cdsCatalog;

import java.util.Date;

import br.study.ebah.miguel.cdsCatalog.elements.Disc;
import br.study.ebah.miguel.cdsCatalog.inMemory.InMemoryArtistRW;
import br.study.ebah.miguel.cdsCatalog.inMemory.InMemoryDisc;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) {
		try {
			InMemoryArtistRW artist = new InMemoryArtistRW("Eu", new Date());
			Disc disc = new InMemoryDisc("", new Date());
			System.out.println("Adding disc: " + disc);
			artist.asWritable(Disc.class).add(disc);
			System.out.println("Artist's known discs: "
					+ artist.getKnownDiscs());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
