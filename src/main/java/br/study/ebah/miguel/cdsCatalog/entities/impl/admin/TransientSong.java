/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.entities.impl.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import br.study.ebah.miguel.cdsCatalog.actions.IsWritable;
import br.study.ebah.miguel.cdsCatalog.actions.Writable;
import br.study.ebah.miguel.cdsCatalog.entities.Artist;
import br.study.ebah.miguel.cdsCatalog.entities.Composer;
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
public class TransientSong implements Song, IsWritable {
	private Optional<Long> id = Optional.absent();
	private final String name;
	private final List<Long> knownDiscsIds;
	private final List<Long> knownArtistsIds;
	private final Optional<Long> composerId = Optional.absent();
	private final Date firstReleaseDate;

	private final Repository<Artist> artistRepository;
	private final Repository<Disc> discRepository;

	private List<Disc> knownDiscs;
	private List<Artist> knownArtists;
	private Composer composer;

	/*
	 * 
	 */
	public TransientSong(String name, RepositoryType repoType)
			throws ExecutionException {
		this(name, null, repoType);
	}

	/*
	 * 
	 */
	public TransientSong(String name, Date releaseDate, RepositoryType repoType)
			throws ExecutionException {
		this.name = name;

		this.knownDiscsIds = Collections
				.synchronizedList(new ArrayList<Long>());
		this.knownArtistsIds = Collections
				.synchronizedList(new ArrayList<Long>());

		this.artistRepository = RepositoryFactory.getRepository(Artist.class,
				repoType);
		this.discRepository = RepositoryFactory.getRepository(Disc.class,
				repoType);

		this.firstReleaseDate = releaseDate;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Entity#getId()
	 */
	public Long getId() {
		return id.get();
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
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Entity#isTransient()
	 */
	public boolean isTransient() {
		return true;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.elements.Song#getName()
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Song#getKnownArtists()
	 */
	@Override
	public Iterable<Artist> getKnownArtists() {
		if (this.knownArtists == null) {
			knownArtists = new ArrayList<>();
			for (Long artistId : this.knownArtistsIds) {
				try {
					knownArtists.add(this.artistRepository.getById(artistId));
				} catch (RepositoryException e) {
					e.printStackTrace();
				}
			}
		}
		return this.knownArtists;
	}

	@Override
	public Composer getComposer() throws RepositoryException {
		if (this.composer == null) {
			// TODO create composer repo
			composer = (Composer) this.artistRepository
					.getById(this.composerId.get());
		}
		return this.composer;
	}
	

	public void setComposer(long long1) {
		// TODO Auto-generated method stub
		
	}
	
	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Song#getLyrics()
	 */
	@Override
	public String getLyrics() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Song#getFirstReleaseDate()
	 */
	@Override
	public Date getFirstReleaseDate() {
		if (this.firstReleaseDate == null) {
			System.err.println("Unknown release date.");
			return new Date();
		} else {
			return (Date) this.firstReleaseDate.clone();
		}
	}

	/*
	 * 
	 * @see br.study.ebah.miguel.cdsCatalog.entities.Song#getKnownDiscs()
	 */
	@Override
	public Iterable<Disc> getKnownDiscs() throws RepositoryException {
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
	 * @see
	 * br.study.ebah.miguel.cdsCatalog.actions.IsWritable#asAddable(java.lang
	 * .Class)
	 */
	@Override
	@SuppressWarnings(value = "unchecked")
	public <T> Writable<T> asWritable(Class<T> type)
			throws IllegalArgumentException {
		if (type == Disc.class) {
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
