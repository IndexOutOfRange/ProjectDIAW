package com.steto.diaw;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import roboguice.RoboGuice;
import roboguice.util.Ln;
import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;

import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.dao.EpisodeDao;
import com.steto.diaw.dao.ShowDao;
import com.steto.diaw.model.Episode;
import com.steto.diaw.model.Results;
import com.steto.diaw.model.Show;
import com.steto.diaw.parser.ShowParser;
import com.steto.diaw.web.QueryString;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

	private static final String BASE_URL = "https://api.parse.com/1/classes/Show";
	private static final String DIAW_APPKEY = "NWvYWhOOjIfE3cwQhHGH4Ic6Sdc8FYbTWBKYwPR8";
	private static final String DIAW_RESTAPIKEY = "Pq2pfW4DLkU1TZfcotp2igvsAosgNhDN0UMIRV87";

	private static final int NET_CONNECT_TIMEOUT_MILLIS = 15000; // 15 seconds
	private static final int NET_READ_TIMEOUT_MILLIS = 10000; // 10 seconds

	private DatabaseHelper mDatabaseHelper;

	/**
	 * Set up the sync adapter
	 */
	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
		mDatabaseHelper = RoboGuice.getInjector(context).getInstance(DatabaseHelper.class);
	}

	/**
	 * Called by the Android system in response to a request to run the sync adapter. The work
	 * required to read data from the network, parse it, and store it in the content provider is
	 * done here. Extending AbstractThreadedSyncAdapter ensures that all methods within SyncAdapter
	 * run on a background thread. For this reason, blocking I/O and other long-running tasks can be
	 * run <em>in situ</em>, and you don't have to set up a separate thread for them.
	 * .
	 * <p>
	 * This is where we actually perform any work required to perform a sync. {@link AbstractThreadedSyncAdapter} guarantees that this will be called on a
	 * non-UI thread, so it is safe to peform blocking I/O here.
	 * <p>
	 * The syncResult argument allows you to pass information back to the method that triggered the sync.
	 */
	@Override
	public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
		Ln.i("Beginning network synchronization");
		try {
			final URL location = new URL(BASE_URL + createQueryString(account.name));
			InputStream stream = null;

			try {
				Ln.i("Streaming data from network: " + location);
				stream = downloadUrl(location);
				updateLocalFeedData(stream, syncResult);
			}
			finally {
				if (stream != null) {
					stream.close();
				}
			}
		} catch (MalformedURLException e) {
			Ln.e("Feed URL is malformed", e);
			syncResult.stats.numParseExceptions++;
			return;
		} catch (IOException e) {
			Ln.e("Error reading from network: " + e.toString());
			syncResult.stats.numIoExceptions++;
			return;
		} catch (SQLException e) {
			Ln.e("Error saving episodes in database: " + e.toString());
			syncResult.stats.numParseExceptions++;
			return;
		}
		Ln.i("Network synchronization complete");
	}

	/**
	 * Read JSON from an input stream, storing it into the database
	 */
	public void updateLocalFeedData(final InputStream stream, final SyncResult syncResult) throws SQLException {
		final ShowParser showParser = new ShowParser();

		Ln.i("Parsing stream...");
		Results res = showParser.parse(stream);
		final List<Episode> episodes = res == null || res.results == null ? new ArrayList<Episode>() : Arrays.asList(res.results);
		Ln.i("Parsing complete. Found " + episodes.size() + " episodes");

		EpisodeDao epDAO = mDatabaseHelper.getDao(Episode.class);
		ShowDao showDAO = mDatabaseHelper.getDao(Show.class);
		int nbCreated = epDAO.createOrUpdate(episodes);
		Ln.d(nbCreated + " episodes added in the database");
		for (Episode episode : episodes) {
			Show currentShow = new Show(episode.getShowName());
			showDAO.createIfNotExists(currentShow);
		}
	}

	/**
	 * Given a string representation of a URL, sets up a connection and gets an input stream.
	 */
	private InputStream downloadUrl(final URL url) throws IOException {
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setReadTimeout(NET_READ_TIMEOUT_MILLIS /* milliseconds */);
		conn.setConnectTimeout(NET_CONNECT_TIMEOUT_MILLIS /* milliseconds */);
		conn.setRequestMethod("GET");
		conn.setRequestProperty("X-Parse-Application-Id", DIAW_APPKEY);
		conn.setRequestProperty("X-Parse-REST-API-Key", DIAW_RESTAPIKEY);
		conn.setRequestProperty("Content-Type", "application/json");
		conn.setDoInput(true);

		// Starts the query
		conn.connect();
		return conn.getInputStream();
	}

	private String createQueryString(String login) {
		QueryString myQuery = new QueryString();
		myQuery.add("limit", "100");
		myQuery.add("order", "-updatedAt");
		myQuery.add("where", "{\"login\": \"" + login + "\"}");
		return myQuery.toString();
	}
}
