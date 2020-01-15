package de.gabrieldaennermedien.kassenschnitt;

//imports
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;

/**
 * DbHelper is an extensions of SQLiteOpenHelper and creates the database.
 */
public class DbHelper extends SQLiteOpenHelper {
    //private static final instances
    private static final String DB_DIR = "Kassenschnitt";
    private static final String DB_NAME = "data.db";
    private static final String LOG_TAG = DbHelper.class.getSimpleName();

    //private static final values
    private static final int DB_VERSION = 1;

    //package private static instances
    static final String COLUMN_DAY     = "day";
    static final String COLUMN_ID      = "_id";
    static final String COLUMN_MONEY   = "money";
    static final String COLUMN_WEEKDAY = "weekday";
    static final String COLUMN_YEAR    = "year";
    static final String TABLE_DATA     = "data";

    //the core sql statement which creates the database
    private static final String SQL_CREATE = "CREATE TABLE " + TABLE_DATA + "(" + COLUMN_ID
            + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_DAY + " TEXT NOT NULL, "
            + COLUMN_YEAR + " TEXT NOT NULL, " + COLUMN_WEEKDAY + " TEXT NOT NULL, "
            + COLUMN_MONEY + " DOUBLE NOT NULL);";

    /**
     * Constructor.
     * @param context the application context.
     */
    @SuppressWarnings("deprecation")
    DbHelper(Context context) {
        super(context, Environment.getExternalStorageDirectory() + File.separator
                + DB_DIR + File.separator + DB_NAME, null, DB_VERSION);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(SQL_CREATE);
        }
        catch (Exception exc) {
            Log.e(LOG_TAG, "ERROR while creating table/database: " + exc.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //nothing to do here
    }
}
