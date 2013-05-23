package com.steto.diaw.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.steto.diaw.dao.EpisodeDao;
import com.steto.diaw.model.Episode;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	private static final String TAG = "DatabaseHelper";
	private static final String DATABASE_NAME = "diaw.db";
	private static final int DATABASE_VERSION = 2;

	private static DatabaseHelper instance;

	private EpisodeDao episodeDao = null;

	private DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public static DatabaseHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DatabaseHelper(context);
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase,
	                     ConnectionSource connectionSource) {
		Log.i(TAG, "onCreate");
		try {
			TableUtils.createTable(connectionSource, Episode.class);
		} catch (SQLException e) {
			Log.e(TAG, "Unable to create databases", e);
		}
		Log.i(TAG, "databases created");
	}

	@Override
	public void onUpgrade(SQLiteDatabase sqliteDatabase,
	                      ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			TableUtils.dropTable(connectionSource, Episode.class, true);

			onCreate(sqliteDatabase, connectionSource);
		} catch (SQLException e) {
			Log.e(TAG, "Unable to upgrade database from version " + oldVersion
					+ " to new " + newVersion, e);
		}
	}

	public EpisodeDao getEpisodeDao() throws SQLException {
		if (episodeDao == null) {
			try {
				episodeDao = DaoManager.createDao(getConnectionSource(), Episode.class);
			} catch (java.sql.SQLException e) {
				Log.e(TAG, "Unable to get the EpisodeDao", e);
			}
		}
		return episodeDao;
	}

}