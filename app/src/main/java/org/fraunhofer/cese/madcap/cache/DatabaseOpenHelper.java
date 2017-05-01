package org.fraunhofer.cese.madcap.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import org.fraunhofer.cese.madcap.R;

import java.sql.SQLException;

import timber.log.Timber;

/**
 * A helper for accessing the ORMLite database and DAO objects.
 * <p>
 * See <a href="http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#Android-Basics">ORMLite Documentation: Using With Android</a>
 */
public class DatabaseOpenHelper extends OrmLiteSqliteOpenHelper {

    private static final String TAG = "Fraunhofer." + DatabaseOpenHelper.class.getSimpleName();

    // name of the database file for your application -- change to something appropriate for your app
    private static final String DATABASE_NAME = "probe_data.db";
    // any time you make changes to your database objects, you may have to increase the database version
    private static final int DATABASE_VERSION = 1;

    private RuntimeExceptionDao<CacheEntry, String> dao;

    @SuppressWarnings("WeakerAccess")
    public DatabaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlist_config);
    }

    /**
     * This is called when the database is first created. Usually you should call createTable statements here to create
     * the tables that will store your data.
     */
    @Override
    public void onCreate(SQLiteDatabase database, @SuppressWarnings("ParameterHidesMemberVariable") ConnectionSource connectionSource) {
        try {
            Timber.i("{onCreate}");
            TableUtils.createTable(connectionSource, CacheEntry.class);

        } catch (SQLException e) {
            Timber.e("Can't create database", e);
            //noinspection ProhibitedExceptionThrown
            throw new RuntimeException(e);
        }
    }

    /**
     * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
     * the various data to match the new version number.
     */
    @Override
    public void onUpgrade(SQLiteDatabase database, @SuppressWarnings("ParameterHidesMemberVariable") ConnectionSource connectionSource, int oldVersion, int newVersion) {
        Timber.d("{onUpgrade}. oldVersion: " + oldVersion + ", newVersion: " + newVersion);
    }

    /**
     * The DAO used to perform database operations. This is an instance of RuntimeExceptionDao, which wraps all normal SQLExceptions
     * with RuntimeExceptions to be consistent with Android's exception handling structure. See http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#RuntimeExceptionDao
     *
     * @return the dao used to access the SQLLite database
     */
    RuntimeExceptionDao<CacheEntry, String> getDao() {
        if (dao == null) {
            dao = getRuntimeExceptionDao(CacheEntry.class);
        }
        return dao;
    }


}
