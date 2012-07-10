/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.sql;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.Before;

/**
 * @author miguel
 *
 */
public class MySQLDiscTest {
	MySQLDisc disc;
	
	@Before
	public void setUp() throws Exception {
		disc = new MySQLDisc(1);
	}
	
	@Test
	public void constructorTest() {
		Assert.assertNotNull(disc);
	}
	
	@Test
	public void getNameTest() {
		System.out.println(disc.getName());
	}

}
