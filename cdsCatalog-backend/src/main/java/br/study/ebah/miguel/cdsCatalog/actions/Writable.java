/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.actions;

import br.study.ebah.miguel.cdsCatalog.repo.RepositoryException;

/**
 * @author miguel
 * 
 */
public interface Writable<T> {
	/*
	 * 
	 */
	void add(Long l);

	/*
	 * 
	 */
	void add(T t) throws RepositoryException;

}
