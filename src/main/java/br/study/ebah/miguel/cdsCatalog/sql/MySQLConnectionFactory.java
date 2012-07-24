/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.sql;

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
	 * @throws ClassNotFoundException 
	 */
	public MySQLConnectionFactory() throws SQLException, ClassNotFoundException {
		if (con == null || con.isClosed()) {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(
					"jdbc:mysql://localhost/cdsCatalog", "logmanager",
					"cdscatalogmanager");
		}
	}

	public Connection getConnection() {
		return con;
	}

}
