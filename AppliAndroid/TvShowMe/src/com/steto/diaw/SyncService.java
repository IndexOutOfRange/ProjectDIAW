package com.steto.diaw;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import roboguice.util.Ln;

/**
 * Service to handle sync requests.
 * <p>
 * This service is invoked in response to Intents with action android.content.SyncAdapter, and returns a Binder connection to SyncAdapter.
 * <p>
 * For performance, only one sync adapter will be initialized within this application's context.
 * <p>
 * Note: The SyncService itself is not notified when a new sync occurs. It's role is to manage the lifecycle of our {@link SyncAdapter} and provide a handle to
 * said SyncAdapter to the OS on request.
 */
public class SyncService extends Service {

	private static final Object sSyncAdapterLock = new Object();
	private static SyncAdapter sSyncAdapter = null;

	/**
	 * Thread-safe constructor, creates static {@link SyncAdapter} instance.
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		Ln.i("Service created");
		synchronized (sSyncAdapterLock) {
			if (sSyncAdapter == null) {
				sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
			}
		}
	}

	/**
	 * Logging-only destructor.
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();
		Ln.i("Service destroyed");
	}

	/**
	 * Return Binder handle for IPC communication with {@link SyncAdapter}.
	 * <p>
	 * New sync requests will be sent directly to the SyncAdapter using this channel.
	 * 
	 * @param intent Calling intent
	 * @return Binder handle for {@link SyncAdapter}
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return sSyncAdapter.getSyncAdapterBinder();
	}
}
