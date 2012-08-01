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
	protected final Set<Long> knownSongsIds;
	private final Set<Long> knownDiscsIds;
	private final Set<Long> knownMainDiscsIds;
	private final Date birthday;

	private final Repository<Song> songRepository;
	private final Repository<Disc> discRepository;

	private Set<Song> knownSongs;
	private Set<Disc> knownDiscs;
	private Set<Disc> knownMainDiscs;

	/*
	 * 
	 */
	public TransientArtist(@Nonnull String name, RepositoryType repoType)
			throws ExecutionException {
		this(name, null, repoType);
	}

	/*
	 * 
	 */
	public TransientArtist(@Nonnull String name, @Nullable Date birthday,
			RepositoryType repoType) throws ExecutionException {
		this.name = name;

		this.knownSongsIds = new ConcurrentSkipListSet<Long>();
		this.knownDiscsIds = new ConcurrentSkipListSet<Long>();
		this.knownMainDiscsIds = new ConcurrentSkipListSet<Long>();

		this.discRepository = RepositoryFactory.getRepository(Disc.class,
				repoType);
		this.songRepository = RepositoryFactory.getRepository(Song.class,
				repoType);

		this.birthday = birthday;

	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Entity#getId()
	 */
	@Override
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
					knownSongsIds.add(l);
				}

			};
		} else if (type == Disc.class) {
			return (Writable<T>) new Writable<Disc>() {

				@Override
				public void add(Disc t) throws RepositoryException {
					add(t.getId());
					discRepository.save(t);
				}

				@Override
				public void add(Long l) {
					knownDiscsIds.add(l);
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
	@Override
	public boolean isTransient() {
		return true;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Artist#getName()
	 */
	@Override
	public final String getName() {
		return this.name;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Artist#getKnownSongs()
	 */
	@Override
	public Iterable<Song> getKnownSongs() {
		if (this.knownSongs == null) {
			knownSongs = new HashSet<>();
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
	@Override
	public Iterable<Disc> getKnownDiscs() {
		if (this.knownDiscs == null) {
			knownDiscs = new HashSet<>();
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
	@Override
	public Iterable<Disc> getKnownMainDiscs() {
		if (this.knownMainDiscs == null) {
			knownMainDiscs = new HashSet<>();
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
	@Override
	public Date getBirthday() {
		if (this.birthday == null) {
			System.err.println("Unknown release date.");
			return new Date();
		} else {
			return (Date) this.birthday.clone();
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
