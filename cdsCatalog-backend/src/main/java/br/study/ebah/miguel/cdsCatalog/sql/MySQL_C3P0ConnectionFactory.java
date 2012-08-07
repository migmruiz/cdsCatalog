/**
 * 
 */
package br.study.ebah.miguel.cdsCatalog.sql;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.DataSources;

/**
 * @author miguel
 * 
 */
public class MySQL_C3P0ConnectionFactory {
	private final DataSource ds_pooled;

	/**
	 * Default constructor
	 * 
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	public MySQL_C3P0ConnectionFactory() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		DataSource ds_unpooled = DataSources.unpooledDataSource(
				"jdbc:mysql://localhost/cdsCatalog", "logmanager",
				"cdscatalogmanager");
		ds_pooled = DataSources.pooledDataSource(ds_unpooled);
	}

	public Connection getConnection() throws SQLException {
		return ds_pooled.getConnection();
	}

	public void destroyDataSource() throws SQLException {
		DataSources.destroy(ds_pooled);
	}

}
