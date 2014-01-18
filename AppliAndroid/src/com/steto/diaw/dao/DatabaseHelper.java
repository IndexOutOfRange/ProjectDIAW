package com.steto.diaw.dao;

import java.sql.SQLException;

import roboguice.util.Ln;
import android.app.Application;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.steto.diaw.model.Episode;
import com.steto.diaw.model.Show;
import com.steto.diaw.tools.Tools;

@Singleton
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

	// les erreurs d'ecritures en BDD commencent a -10 et sont de plus en plus petit
	public static final int ERROR_BDD = -10;

	private static final String TAG = "DatabaseHelper";
	private static final String DATABASE_NAME = "diaw.db";
	private static final int DATABASE_VERSION = 14;

	@Inject
	private SharedPreferences mSharedPreferences;

	@Inject
	private DatabaseHelper(Application application) {
		super(application, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase sqliteDatabase, ConnectionSource connectionSource) {
		Ln.i("onCreate");
		try {
			TableUtils.createTable(connectionSource, Episode.class);
			TableUtils.createTable(connectionSource, Show.class);
		} catch (SQLException e) {
			Ln.e(e, "Unable to create databases");
		}
		Ln.i("databases created !");
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
		// reboot de la date de MJ de la base
		mSharedPreferences.edit().putLong(Tools.SHARED_PREF_LAST_UPDATE, 0).commit();
	}
}
