/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.entities.impl.admin;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
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

	private final Set<Long> knownComposedSongsIds;

	private final Repository<Song> songRepository;

	private Set<Song> knownComposedSongs;

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
		this.knownComposedSongsIds = new ConcurrentSkipListSet<Long>();

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
			knownComposedSongs = new HashSet<>();
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
