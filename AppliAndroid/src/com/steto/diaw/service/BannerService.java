package com.steto.diaw.service;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.http.HttpStatus;

import roboguice.util.Ln;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.inject.Inject;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.model.Show;
import com.steto.diaw.network.Response;
import com.steto.diaw.network.connector.BannerConnector;
import com.steto.diaw.network.connector.IHttpsConnector;
import com.steto.diaw.service.model.AbstractIntentService;

public class BannerService extends AbstractIntentService {

	private static final String NAME = "BannerService";
	public static final String EXTRA_INPUT_SHOW = "EXTRA_INPUT_SHOW";
	public static final String EXTRA_OUTPUT_BITMAP = "EXTRA_OUTPUT_BITMAP";

	@Inject
	private DatabaseHelper mDatabaseHelper;
	private Show mShow;
	private Bitmap mBitmap;

	public BannerService() {
		super(NAME);
	}

	@Override
	protected void processInputExtras(Bundle bundle) {
		super.processInputExtras(bundle);
		mShow = (Show) bundle.get(EXTRA_INPUT_SHOW);
	}

	@Override
	protected void processRequest() {
		if (mShow.getBanner() == null) {
			try {
				Response response = getResponse();
				if (response.getStatusCode() == HttpStatus.SC_OK) {
					mBitmap = BitmapFactory.decodeStream(response.getBody());
					if(mBitmap != null) {
						mShow.setBanner(mBitmap);
					} else {
						Ln.e("bitmap decodeStream returned null");
						setServiceResponseCode(ServiceResponseCode.KO);
						mServiceStatusCode = AbstractIntentService.PARSING_ERROR;
					}
					try {
						mDatabaseHelper.getDao(Show.class).update(mShow);
					} catch (SQLException e) {
						Ln.e(e);
						setServiceResponseCode(ServiceResponseCode.KO);
						mServiceStatusCode = AbstractIntentService.DATABASE_ERROR;
					}
				} else {
					setServiceResponseCode(ServiceResponseCode.KO);
					mServiceStatusCode = AbstractIntentService.HTTP_ERROR;
				}
			} catch (IOException e) {
				Ln.e(e);
				setServiceResponseCode(ServiceResponseCode.KO);
				mServiceStatusCode = AbstractIntentService.NETWORK_ERROR;
			}
		} else {
			mBitmap = mShow.getBannerAsBitmap();
		}
	}
	
	@Override
	protected void fillBundleResponse(Bundle bundle) {
		bundle.putParcelable(EXTRA_OUTPUT_BITMAP, mBitmap);
	}

	@Override
	protected String getQuery() {
		return mShow.getBannerURL();
	}

	@Override
	protected IHttpsConnector getConnector() {
		return new BannerConnector();
	}
}
