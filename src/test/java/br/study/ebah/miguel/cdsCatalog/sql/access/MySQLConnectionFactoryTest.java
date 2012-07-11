/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.sql.access;

import java.sql.SQLException;

import junit.framework.Assert;

import org.junit.Test;

/**
 * @author miguel
 * 
 */
public class MySQLConnectionFactoryTest {

	@Test
	public void getConnectionTest() throws SQLException {
		Assert.assertNotNull(new MySQLConnectionFactory().getConnection());
	}

}
