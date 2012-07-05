/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.actions;


/**
 * @author miguel
 *
 */
public interface IsWritable {
	/*
	 * 
	 */
	public <T> Writable<T> asAddable(Class<T> type) throws IllegalArgumentException;

}

/* 
 * IsWritable usage example
 */
// static {
//	 InMemoryArtistRW artist = new InMemoryArtistRW("Eu", new Date());
//	 try {
//	 	Disc disc = new InMemoryDisc("", new Date());
//	 	System.out.println("Adding disc: " + disc);
//	 	artist.asAddable(Disc.class).add(disc);
//	 } catch (Exception e) {
//	 	e.printStackTrace();
//	 }
//	System.out.println("Artist's known discs: " + artist.getKnownDiscs());
//}