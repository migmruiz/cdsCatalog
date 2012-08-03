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
	public <T> Writable<T> asWritable(Class<T> type)
			throws IllegalArgumentException;

}