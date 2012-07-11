/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.sql.access;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author miguel
 * 
 */
public class MySQLConnectionFactory {
	private static Connection con;

	/**
	 * Default constructor
	 * 
	 * @throws SQLException
	 */
	public MySQLConnectionFactory() throws SQLException {
		if (con == null || con.isClosed()) {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost/cdsCatalog", "logmanager",
					"cdscatalogmanager");
		}
	}

	public Connection getConnection() {
		return con;
	}

}
