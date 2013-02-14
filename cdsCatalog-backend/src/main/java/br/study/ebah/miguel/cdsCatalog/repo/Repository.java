package br.study.ebah.miguel.cdsCatalog.repo;

import java.io.Closeable;

import javax.annotation.Nonnull;

import org.apache.avalon.framework.activity.Initializable;

/**
 * 
 * @author bruno
 * 
 */
public interface Repository<T> extends Closeable, Initializable {

	T getById(@Nonnull Long id) throws RepositoryException;

	T save(T t) throws RepositoryException;

	void delete(T t) throws RepositoryException;
}