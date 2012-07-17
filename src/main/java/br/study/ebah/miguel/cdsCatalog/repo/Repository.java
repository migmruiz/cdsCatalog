package br.study.ebah.miguel.cdsCatalog.repo;

/**
 * 
 * @author bruno
 *
 */
public interface Repository<T> {
	T getById(int id) throws RepositoryException;

	T save(T t) throws RepositoryException;

	void delete(T t) throws RepositoryException;
}