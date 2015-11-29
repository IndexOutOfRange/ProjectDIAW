package com.steto.diaw.service.model;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.steto.diaw.network.Response;
import com.steto.diaw.network.connector.IHttpsConnector;
import com.steto.diaw.service.exception.NotConnectedException;
import com.steto.diaw.tools.NaiveTrustManager;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import roboguice.service.RoboIntentService;
import roboguice.util.Ln;

public abstract class AbstractIntentService extends RoboIntentService {
	public enum ServiceResponseCode {
		OK(0), KO(1);

		public int value;

		ServiceResponseCode(int value) {
			this.value = value;
		}
	}

	/**
	 * Code de retour associé au EXTRA_OUTPUT_DETAILED_RESULT_CODE
	 */
	public static final int NO_ERROR = 0;
	public static final int NOT_CONNECTED_ERROR = 1;
	public static final int NETWORK_ERROR = 2;
	public static final int PARSING_ERROR = 3;
	public static final int DATABASE_ERROR = 4;
	public static final int HTTP_ERROR = 5;

	public static final int WAIT_INTERRUPTED = -304;

	public static final String EXTRA_INPUT_RESULT_RECEIVER = "EXTRA_INPUT_RESULT_RECEIVER";
	public static final String EXTRA_OUTPUT_HTTP_RESPONSECODE = "EXTRA_OUTPUT_HTTP_RESPONSECODE";
	public static final String EXTRA_OUTPUT_INTENT = "EXTRA_OUTPUT_INTENT";
	public static final String EXTRA_OUTPUT_DETAILED_RESULT_CODE = "EXTRA_OUTPUT_DETAILED_RESULT_CODE";

	private ResultReceiver mResultReceiver;
	private ServiceResponseCode mServiceResponseCode = ServiceResponseCode.OK;
	protected int mServiceStatusCode;
	private Bundle mResponseBundle = new Bundle();

	public AbstractIntentService() {
		super("AbstractIntentService");
	}

	public AbstractIntentService(String name) {
		super(name);
	}

	/**
	 * Le service est disponible pour effectuer la tache, i.e. l'initialisation est finie. les fonctions getResponse et postResponse peuvent être utilisées si
	 * le service contient un appel réseau en GET ou en POST
	 */
	protected abstract void processRequest();

	/**
	 * @param bundle le bundle qui sera renvoyé à l'appelant du Service. Ce bundle contient deja : \
	 *            index : EXTRA_OUTPUT_INTENT , valeur : l'intent d'appel du service \
	 *            index : EXTRA_OUTPUT_HTTP_RESPONSECODE , valeur : le code retour Http de l'appel \
	 */
	protected abstract void fillBundleResponse(Bundle bundle);

	/**
	 * @return la query Http qui va etre executée. La chaine va être insérée après le nom de domaine de l'environnement Socle.
	 */
	protected abstract String getQuery();

	/**
	 * @return le type de Connector qui va etre utilisé.
	 */
	protected abstract IHttpsConnector getConnector();

	@Override
	protected final void onHandleIntent(Intent intent) {
		Ln.d("lancement de l'intent service " + getClass().getSimpleName());
		initDefaultSSLFactory();
		mResponseBundle.putParcelable(EXTRA_OUTPUT_INTENT, intent);

		processInputExtras(intent.getExtras());

		try {
			if (!isConnected()) {
				throw new NotConnectedException();
			} else {
				processRequest();
			}
		} catch (NotConnectedException e) {
			mServiceStatusCode = AbstractIntentService.NOT_CONNECTED_ERROR;
			setServiceResponseCode(ServiceResponseCode.KO);
			Ln.e("NotConnectedException", e);
		}

		sendResponse();
	}

	protected void processInputExtras(Bundle bundle) {
		if (bundle != null) {
			mResultReceiver = bundle.getParcelable(EXTRA_INPUT_RESULT_RECEIVER);
		}
	}

	protected final Response getResponse() throws IOException {
		IHttpsConnector connector = getConnector();
		Response response = connector.getData(getQuery());
		return processResponse(response);
	}

	protected final Response postResponse(String content) throws IOException {
		IHttpsConnector connector = getConnector();
		Response response = connector.postData(getQuery(), content);
		return processResponse(response);
	}

	protected final Response putResponse(String content) throws IOException {
		IHttpsConnector connector = getConnector();
		Response response = connector.putData(getQuery(), content);
		return processResponse(response);
	}

	protected final Response deleteResponse(String urlParam, String content) throws IOException {
		IHttpsConnector connector = getConnector();
		Response response = connector.deleteData(getQuery() + urlParam, content);
		return processResponse(response);
	}

	private Response processResponse(Response response) {
		int statusCode = response.getStatusCode();
		mResponseBundle.putInt(EXTRA_OUTPUT_HTTP_RESPONSECODE, statusCode);
		if (statusCode < HttpURLConnection.HTTP_OK || statusCode >= HttpURLConnection.HTTP_MULT_CHOICE) {
			mServiceResponseCode = ServiceResponseCode.KO;
		}
		return response;
	}

	private void sendResponse() {
		if (mResultReceiver != null) {
			mResponseBundle.putInt(EXTRA_OUTPUT_DETAILED_RESULT_CODE, getServiceStatusCode());
			fillBundleResponse(mResponseBundle);
			mResultReceiver.send(getServiceResponseCode().value, mResponseBundle);
		}
	}

	private void initDefaultSSLFactory() {
		SSLContext sSSLContext;
		try {
			sSSLContext = SSLContext.getInstance("TLS");
			sSSLContext.init(null, new TrustManager[] { new NaiveTrustManager() }, null);
		} catch (NoSuchAlgorithmException e) {
			Ln.e("Impossible to create TLS SSL Context", e);
		} catch (KeyManagementException e) {
			Ln.e("Impossible to use Trust Manager", e);
		}
	}

	/**
	 * Teste l'etat de la connection au reseau
	 * 
	 * @return <b>true</b> si on est connecte au reseau WiFi/3G, <b>false</b> sinon
	 */
	private boolean isConnected() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

		if (networkInfo == null) {
			Ln.i("MODE AVION");
			return false;
		}

		State networkState = networkInfo.getState();
		return !(networkState.compareTo(State.DISCONNECTED) == 0 || networkState.compareTo(State.DISCONNECTING) == 0);
	}

	protected void setServiceResponseCode(ServiceResponseCode serviceResponseCode) {
		this.mServiceResponseCode = serviceResponseCode;
	}

	protected ServiceResponseCode getServiceResponseCode() {
		return mServiceResponseCode;
	}

	protected ResultReceiver getResultReceiver() {
		return mResultReceiver;
	}

	protected Bundle getResponseBundle() {
		return mResponseBundle;
	}

	protected int getServiceStatusCode() {
		return mServiceStatusCode;
	}
}
