/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.sql.access;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author miguel
 * 
 */
public class MySQLConnectionFactoryTest {

	@Test
	public void getConnectionTest() {
		Assert.assertNotNull(new MySQLConnectionFactory().getConnection());
	}

}
