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

import br.study.ebah.miguel.cdsCatalog.entities.Artist;
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
public class ComposerImpl extends ArtistImpl implements Composer {

	private final Set<Long> knownComposedSongsIds = new ConcurrentSkipListSet<Long>();

	private final Repository<Song> songRepository;

	/*
	 * 
	 */
	public ComposerImpl(Artist artist, RepositoryType repoType)
			throws ExecutionException {
		this(artist.getName(), artist.getBirthday(), repoType);
	}

	/*
	 * 
	 */
	public ComposerImpl(@Nonnull String name, RepositoryType repoType)
			throws ExecutionException {
		this(name, null, repoType);
	}

	/*
	 * 
	 */
	public ComposerImpl(@Nonnull String name, @Nullable Date birthday,
			RepositoryType repoType) throws ExecutionException {
		super(name, birthday, repoType);

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
		Set<Song> knownComposedSongs = new HashSet<>();
		for (Long songId : this.knownComposedSongsIds) {
			try {
				knownComposedSongs.add(songRepository.getById(songId));
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
		return knownComposedSongs;
	}

	/*
	 * 
	 */
	public void setComposed(Long songId) {
		this.knownComposedSongsIds.add(songId);
	}

}
