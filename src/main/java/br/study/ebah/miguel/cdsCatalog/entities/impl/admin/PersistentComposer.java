/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.entities.impl.admin;

import javax.annotation.Nonnull;

import br.study.ebah.miguel.cdsCatalog.entities.Composer;
import br.study.ebah.miguel.cdsCatalog.entities.Song;

/**
 * @author miguel
 * 
 */
public class PersistentComposer extends PersistentArtist implements Composer {
	private Composer composer;

	public PersistentComposer(@Nonnull Composer composer) {
		super(composer);
		this.composer = composer;
	}

	@Override
	public Iterable<? extends Song> getKnownComposedSongs() {
		return this.composer.getKnownComposedSongs();
	}

}
