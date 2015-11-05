package org.fraunhofer.cese.madcap.cache;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

/**
 *
 * This utility is needed to create a config file for OrmLite which speeds up the creation of the DAO.
 * See http://ormlite.com/javadoc/ormlite-core/doc-files/ormlite_4.html#Config-Optimization
 *
 * This utility must be run every time the database model classes are changed.
 *
 * To run correctly, create a DatabaseConfigUtil run configuration:
 *    1. Change the working directory to \<FULL_PATH_TO_PROJECT_ROOT\>/app/src/main
 *    2. In the "Before Launch" section, remove the Make command.
 *
 * The modification for AndroidStudio was found here: http://stackoverflow.com/questions/17298773/android-studio-run-configuration-for-ormlite-config-generation
 *
 * Created by Lucas on 10/5/2015.
 */
public class DatabaseConfigUtil extends OrmLiteConfigUtil {

    private static final Class<?> [] classes = new Class[] {CacheEntry.class};

    public static void main(String[] args) throws Exception {
        writeConfigFile("ormlist_config.txt", classes);
    }
}
