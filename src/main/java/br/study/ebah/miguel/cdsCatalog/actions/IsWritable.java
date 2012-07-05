/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.actions;

/**
 * @author miguel
 * 
 */
public interface IsWritable {
	/*
	 * 
	 */
	public <T> Writable<T> asAddable(Class<T> type)
			throws IllegalArgumentException;

}