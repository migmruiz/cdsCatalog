/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.entities.impl.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import br.study.ebah.miguel.cdsCatalog.entities.Composer;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryFactory;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryType;

/**
 * @author miguel
 * 
 */
public class TransientComposer extends TransientArtist implements Composer {

	private final List<Long> knownComposedSongsIds;

	private final Repository<Song> songRepository;

	private List<Song> knownComposedSongs;

	/*
	 * 
	 */
	public TransientComposer(@Nonnull String name, RepositoryType repoType)
			throws ExecutionException {
		this(name, null, repoType);
	}

	/*
	 * 
	 */
	public TransientComposer(@Nonnull String name, @Nullable Date birthday,
			RepositoryType repoType) throws ExecutionException {
		super(name, birthday, repoType);
		this.knownComposedSongsIds = Collections
				.synchronizedList(new ArrayList<Long>());

		this.songRepository = RepositoryFactory.getRepository(Song.class,
				repoType);
	}

	/*
	 * 
	 * @see
	 * br.study.ebah.miguel.cdsCatalog.entities.Composer#getKnownComposedSongs()
	 */
	@Override
	public Iterable<Song> getKnownComposedSongs() {
		if (this.knownComposedSongs == null) {
			knownComposedSongs = new ArrayList<>();
			for (Long songId : this.knownComposedSongsIds) {
				try {
					knownComposedSongs.add(songRepository.getById(songId));
				} catch (RepositoryException e) {
					e.printStackTrace();
				}
			}
		}
		return knownComposedSongs;
	}

	/*
	 * 
	 */
	public void setMain(Long songId) {
		if (super.knownSongsIds.contains(songId)) {
			this.knownComposedSongsIds.add(songId);
		} else {
			throw new UnsupportedOperationException(
					"songId must be under knownSongsIds first");
		}
	}

}
