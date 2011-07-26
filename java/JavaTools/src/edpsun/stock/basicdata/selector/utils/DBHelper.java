/**
 * JavaTools_declaration
 */
package edpsun.stock.basicdata.selector.utils;

import java.io.File;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;

import edpsun.stock.basicdata.selector.parser.StockInfo;

/**
 * @author esun
 * @version JavaTools_version
 * Create Date:May 2, 2011 6:25:48 PM
 */
public class DBHelper {
	File db4o = null;

	public DBHelper(File db4o) {
		this.db4o = db4o;
	}

	public boolean dbExists() {
		return db4o.exists();
	}

	public synchronized ObjectContainer getObejctContainer() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().objectClass(StockInfo.class).cascadeOnActivate(true);
		config.common().objectClass(StockInfo.class).cascadeOnUpdate(true);

		ObjectContainer db = Db4oEmbedded.openFile(config, db4o.getAbsolutePath());
		return db;
	}
}
