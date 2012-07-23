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
public class TransientArtist implements Artist, IsWritable {
	private Optional<Long> id = Optional.absent();
	private final String name;
	protected final List<Long> knownSongsIds;
	protected final List<Long> knownDiscsIds;
	protected final List<Long> knownMainDiscsIds;
	private final Date birthday;

	private final Repository<Song> songRepository;
	private final Repository<Disc> discRepository;

	private List<Song> knownSongs;
	private List<Disc> knownDiscs;
	private List<Disc> knownMainDiscs;

	/*
	 * 
	 */
	public TransientArtist(@Nonnull String name, RepositoryType store)
			throws ExecutionException {
		this(name, null, store);
	}

	/*
	 * 
	 */
	public TransientArtist(@Nonnull String name, @Nullable Date birthday,
			RepositoryType store) throws ExecutionException {
		this.name = name;

		this.knownSongsIds = Collections
				.synchronizedList(new ArrayList<Long>());
		this.knownDiscsIds = Collections
				.synchronizedList(new ArrayList<Long>());
		this.knownMainDiscsIds = Collections
				.synchronizedList(new ArrayList<Long>());

		this.discRepository = RepositoryFactory
				.getRepository(Disc.class, store);
		this.songRepository = RepositoryFactory
				.getRepository(Song.class, store);

		this.birthday = birthday;

	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Entity#getId()
	 */
	public Long getId() {
		return id.or(-1L);
	}

	/**
	 * Admin access only
	 * 
	 * @param id
	 */
	public void setId(Long id) {
		this.id = Optional.of(id);
	}

	/*
	 * 
	 * @see
	 * br.study.ebah.miguel.cdsCatalog.actions.IsWritable#asAddable(java.lang
	 * .Class)
	 */
	@SuppressWarnings(value = "unchecked")
	public <T> Writable<T> asWritable(Class<T> type)
			throws IllegalArgumentException {
		if (type == Song.class) {
			return (Writable<T>) new Writable<Song>() {

				public void add(Long t) {
					knownSongsIds.add(t);
				}

			};
		} else if (type == Disc.class) {
			return (Writable<T>) new Writable<Disc>() {

				public void add(Long t) {
					knownDiscsIds.add(t);
				}

			};
		} else {
			throw new IllegalArgumentException();
		}
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Entity#isTransient()
	 */
	public boolean isTransient() {
		return true;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Artist#getName()
	 */
	public final String getName() {
		return this.name;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Artist#getKnownSongs()
	 */
	public Iterable<Song> getKnownSongs() {
		if (this.knownSongs == null) {
			knownSongs = new ArrayList<>();
			for (Long songId : this.knownSongsIds) {
				try {
					knownSongs.add(this.songRepository.getById(songId));
				} catch (RepositoryException e) {
					e.printStackTrace();
				}
			}
		}
		return this.knownSongs;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Artist#getKnownDiscs()
	 */
	public Iterable<Disc> getKnownDiscs() {
		if (this.knownDiscs == null) {
			knownDiscs = new ArrayList<>();
			for (Long discId : this.knownDiscsIds) {
				try {
					knownDiscs.add(this.discRepository.getById(discId));
				} catch (RepositoryException e) {
					e.printStackTrace();
				}
			}
		}
		return this.knownDiscs;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Artist#getKnownMainDiscs()
	 */
	public Iterable<Disc> getKnownMainDiscs() {
		if (this.knownMainDiscs == null) {
			knownMainDiscs = new ArrayList<>();
			for (Long discId : this.knownMainDiscsIds) {
				try {
					knownMainDiscs.add(this.discRepository.getById(discId));
				} catch (RepositoryException e) {
					e.printStackTrace();
				}
			}
		}
		return this.knownMainDiscs;
	}

	/*
	 * 
	 */
	public void setMain(Long discId) {
		if (this.knownDiscsIds.contains(discId)) {
			this.knownMainDiscsIds.add(discId);
		} else {
			throw new UnsupportedOperationException(
					"discId must be under knownDiscsIds first");
		}
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Artist#getBirthday()
	 */
	public Date getBirthday() {
		return (Date) this.birthday.clone();
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
