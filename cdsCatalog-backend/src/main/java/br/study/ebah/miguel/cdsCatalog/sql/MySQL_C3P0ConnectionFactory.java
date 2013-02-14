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
public class MySQL_C3P0ConnectionFactory implements ConnectionFactory {

	private DataSource ds_pooled;

	private MySQL_C3P0ConnectionFactory() {
	}

	private static class SingletonHolder {

		private static final MySQL_C3P0ConnectionFactory instance;

		static {
			try {
				instance = new MySQL_C3P0ConnectionFactory();
				instance.initialize();
			} catch (Throwable e) {
				throw new Error(e);
			}
		}
	}

	public static ConnectionFactory getInstance() {
		return SingletonHolder.instance;
	}

	private void initialize() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		DataSource ds_unpooled = DataSources.unpooledDataSource(
				"jdbc:mysql://localhost/cdsCatalog", "logmanager",
				"cdscatalogmanager");
		ds_pooled = DataSources.pooledDataSource(ds_unpooled);
	}

	@Override
	public Connection getConnection() throws SQLException {
		return ds_pooled.getConnection();
	}

	public void destroyDataSource() throws SQLException {
		DataSources.destroy(ds_pooled);
	}

}
