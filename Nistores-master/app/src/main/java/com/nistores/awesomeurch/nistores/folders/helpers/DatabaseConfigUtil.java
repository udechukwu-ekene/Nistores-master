package com.nistores.awesomeurch.nistores.folders.helpers;

import java.io.IOException;
import java.sql.SQLException;
import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;
/**
 * Created by Awesome Urch on 30/07/2018.
 *OrmLite Database Configuration Utility
 */

public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    public static void main(String[] args) throws SQLException, IOException {
        writeConfigFile("ormlite_config.txt");
    }
}