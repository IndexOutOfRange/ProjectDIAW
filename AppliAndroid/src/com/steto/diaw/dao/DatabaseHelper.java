package com.steto.diaw.dao;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.steto.diaw.model.Episode;
import com.steto.diaw.model.Show;
import com.steto.diaw.tools.Tools;

import java.sql.SQLException;
import java.util.Date;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    //les erreurs d'ecritures en BDD commencent a -10 et sont de plus en plus petit
    public static final int ERROR_BDD = -10;

	private static final String TAG = "DatabaseHelper";
	private static final String DATABASE_NAME = "diaw.db";
	private static final int DATABASE_VERSION = 9;

	private static DatabaseHelper instance;

	private EpisodeDao mEpisodeDao = null;
	private ShowDao mShowDao = null;
    private Context mContext;

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
	}

	public static DatabaseHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DatabaseHelper(context);
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
		Log.i(TAG, "onCreate");
		try {
			TableUtils.createTable(connectionSource, Episode.class);
			TableUtils.createTable(connectionSource, Show.class);
		} catch (SQLException e) {
			Log.e(TAG, "Unable to create databases", e);
		}
		Log.i(TAG, "databases created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			TableUtils.dropTable(connectionSource, Episode.class, true);
			TableUtils.dropTable(connectionSource, Show.class, true);

			onCreate(sqliteDatabase, connectionSource);

            reinitDateMAJ();

		} catch (SQLException e) {
			Log.e(TAG, "Unable to upgrade database from version " + oldVersion + " to new " + newVersion, e);
		}
	}

    private void reinitDateMAJ() {
        //reboot de la date de MJ de la base
        SharedPreferences settings = mContext.getSharedPreferences(Tools.SHARED_PREF_FILE, Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(Tools.SHARED_PREF_LAST_UPDATE, 0);
        editor.commit();
    }

	public EpisodeDao getEpisodeDao() throws SQLException {
		if (mEpisodeDao == null) {
			try {
				mEpisodeDao = DaoManager.createDao(getConnectionSource(), Episode.class);
			} catch (SQLException e) {
				Log.e(TAG, "Unable to get the EpisodeDao", e);
			}
		}
		return mEpisodeDao;
	}

	public ShowDao getShowDao() throws SQLException {
		if (mShowDao == null) {
			try {
				mShowDao = DaoManager.createDao(getConnectionSource(), Show.class);
			} catch (SQLException e) {
				Log.e(TAG, "Unable to get the ShowDao", e);
			}
		}
		return mShowDao;
	}


}
