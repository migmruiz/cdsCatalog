/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.entities.impl.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;

import br.study.ebah.miguel.cdsCatalog.actions.IsWritable;
import br.study.ebah.miguel.cdsCatalog.actions.Writable;
import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Disc;
import br.study.ebah.miguel.cdsCatalog.entities.Song;
import br.study.ebah.miguel.cdsCatalog.repo.Repository;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryFactory;
import br.study.ebah.miguel.cdsCatalog.repo.RepositoryType;

import com.google.common.base.Optional;

/**
 * @author miguel
 * 
 */
public class DiscImpl extends AbstractEntity implements Disc, IsWritable {
	private final String name;

	private final List<Long> songsIds = Collections
			.synchronizedList(new ArrayList<Long>());
	private final Set<Long> artistsIds = new ConcurrentSkipListSet<>();

	private Optional<Long> mainArtistId = Optional.absent();
	private final Date releaseDate;

	private final Repository<Artist> artistRepository;
	private final Repository<Song> songRepository;

	/*
	 * 
	 */
	public DiscImpl(String name, RepositoryType repoType)
			throws ExecutionException {
		this(name, null, repoType);
	}

	/*
	 * 
	 */
	public DiscImpl(String name, Date releaseDate, RepositoryType repoType)
			throws ExecutionException {
		this.name = name;

		this.artistRepository = RepositoryFactory.getRepository(Artist.class,
				repoType);
		this.songRepository = RepositoryFactory.getRepository(Song.class,
				repoType);

		this.releaseDate = releaseDate;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Disc#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Disc#getArtists()
	 */
	@Override
	public Iterable<Artist> getArtists() {
		Set<Artist> artists = new HashSet<>();
		for (Long artistId : this.artistsIds) {
			try {
				artists.add(this.artistRepository.getById(artistId));
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
		return artists;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Disc#getMainArtist()
	 */
	@Override
	public Artist getMainArtist() throws RepositoryException,
			ExecutionException {
		if (this.mainArtistId.isPresent()) {
			return this.artistRepository.getById(this.mainArtistId.get());
		} else {
			throw new RepositoryException();
		}
	}

	public void setMain(long artistId) {
		this.mainArtistId = Optional.of(artistId);
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Disc#getSongs()
	 */
	@Override
	public Iterable<Song> getSongs() {
		List<Song> songs = new ArrayList<>();
		for (Long songId : this.songsIds) {
			try {
				songs.add(this.songRepository.getById(songId));
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}

		return songs;
	}

	/*
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Disc#getReleaseDate()
	 */
	@Override
	public Date getReleaseDate() {
		if (this.releaseDate == null) {
			System.err.println("Unknown release date.");
			return new Date();
		} else {
			return (Date) this.releaseDate.clone();
		}
	}

	/*
	 * 
	 * @see
	 * br.study.ebah.miguel.cdsCatalog.actions.IsWritable#asAddable(java.lang
	 * .Class)
	 */
	@Override
	@SuppressWarnings(value = "unchecked")
	public <T> Writable<T> asWritable(Class<T> type)
			throws IllegalArgumentException {
		if (type == Song.class) {
			return (Writable<T>) new Writable<Song>() {

				@Override
				public void add(Song t) throws RepositoryException {
					add(t.getId());
					songRepository.save(t);
				}

				@Override
				public void add(Long l) {
					songsIds.add(l);
				}

			};
		} else if (type == Artist.class) {
			return (Writable<T>) new Writable<Artist>() {

				@Override
				public void add(Artist t) throws RepositoryException {
					add(t.getId());
					artistRepository.save(t);
				}

				@Override
				public void add(Long l) {
					artistsIds.add(l);
				}

			};
		} else {
			throw new IllegalArgumentException();
		}
	}

	/*
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// TODO Override Object method
		return super.equals(obj);
	}

	/*
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Override Object method
		return getClass().getName() + "@" + Integer.toHexString(hashCode());
	}

	/*
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public int hashCode() {
		// TODO Override Object method
		return super.hashCode();
	}

}
