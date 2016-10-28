package org.fraunhofer.cese.madcap.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import org.fraunhofer.cese.madcap.MyApplication;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.fraunhofer.cese.madcap.R;

import java.sql.SQLException;

/**
 * A helper for accessing the ORMLite database and DAO objects.
 *
 * See <a href="http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#Android-Basics">ORMLite Documentation: Using With Android</a>
 */
public class DatabaseOpenHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = "Fraunhofer." + DatabaseOpenHelper.class.getSimpleName();

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "probe_data.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    private RuntimeExceptionDao<CacheEntry,String> dao;

    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlist_config);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            MyApplication.madcapLogger.i(TAG, "{onCreate}");
            TableUtils.createTable(connectionSource, CacheEntry.class);

        } catch (SQLException e) {
            MyApplication.madcapLogger.e(TAG, "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            MyApplication.madcapLogger.i(TAG, "{onUpgrade}");
            TableUtils.dropTable(connectionSource, CacheEntry.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            MyApplication.madcapLogger.e(TAG, "Can't drop databases", e);
            throw new RuntimeException(e);
        }

    }

    /**
     * The DAO used to perform database operations. This is an instance of RuntimeExceptionDao, which wraps all normal SQLExceptions
     * with RuntimeExceptions to be consistent with Android's exception handling structure. See http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#RuntimeExceptionDao
     *
     * @return the dao used to access the SQLLite database
     */
    public RuntimeExceptionDao<CacheEntry, String> getDao() {
        if(dao == null) {
//            dao = getDao(CacheEntry.class);
            dao = getRuntimeExceptionDao(CacheEntry.class);
        }
        return dao;
    }


}
