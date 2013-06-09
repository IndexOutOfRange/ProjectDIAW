package com.steto.diaw.service;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.ResultReceiver;
import com.steto.diaw.dao.DatabaseHelper;
import com.steto.diaw.model.Show;
import com.steto.diaw.web.BannerConnector;
import com.steto.diaw.web.WebConnector;
import org.apache.http.HttpStatus;

import java.sql.SQLException;

/**
 * Created by Stephane on 02/06/13.
 */
public class BannerService extends IntentService {

    private static final String NAME ="BannerService";
    public static final String INPUT_SERIE ="INPUT_SERIE";
    public static final String INPUT_RECEIVER ="INPUT_RECEIVER";
    public static final String OUTPUT_BITMAP ="OUTPUT_BITMAP";
    public static final int RESULT_CODE_OK = 0;

    public BannerService() {
        super(NAME);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Show show = (Show)intent.getExtras().get(INPUT_SERIE);
        ResultReceiver caller = (ResultReceiver)intent.getExtras().get(INPUT_RECEIVER);
        int resultCode = RESULT_CODE_OK;
        Bitmap bmp = null;
        if( show.getBanner() == null ) {
            BannerConnector myWeb = new BannerConnector();
            myWeb.requestFromNetwork(show.getBannerURL(), WebConnector.HTTPMethod.GET, null);
            if( myWeb.getStatusCode() == HttpStatus.SC_OK) {
                bmp = BitmapFactory.decodeStream(myWeb.getResponseBody());
                show.setBanner(bmp);
                try {
                    DatabaseHelper.getInstance(this).getShowDao().update(show);
                } catch (SQLException e) {
                    resultCode = DatabaseHelper.ERROR_BDD;
                    e.printStackTrace();
                }
            } else {
                resultCode = myWeb.getStatusCode();
            }
        } else {
            bmp = show.getBannerAsBitmap();
        }
        Bundle ret = new Bundle();
        ret.putParcelable(OUTPUT_BITMAP, bmp);
        caller.send(resultCode, ret);
    }
}
