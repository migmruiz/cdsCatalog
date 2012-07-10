/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.sql.acccess;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author miguel
 * 
 */
public class MySQLConnectionFactory {
	Connection con;
	
	/**
	 * Default constructor
	 */
	public MySQLConnectionFactory() {
		try {
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost/cdsCatalog", "logmanager",
					"cdscatalogmanager");
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public Connection getConnection() {
		return this.con;
	}

}
