package br.study.ebah.miguel.cdsCatalog.repo;

import javax.annotation.Nonnull;

/**
 * 
 * @author bruno
 * 
 */
public interface Repository<T> extends AutoCloseable {
	
	void init();
	
	T getById(@Nonnull Long id) throws RepositoryException;

	T save(T t) throws RepositoryException;

	void delete(T t) throws RepositoryException;
}