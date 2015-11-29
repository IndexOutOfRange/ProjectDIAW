package com.steto.diaw.service;

import android.os.Bundle;
import android.text.TextUtils;

import com.steto.diaw.network.Response;
import com.steto.diaw.network.connector.IHttpsConnector;
import com.steto.diaw.network.connector.ParseConnector;
import com.steto.diaw.service.model.AbstractIntentService;

import java.io.IOException;
import java.net.HttpURLConnection;

/**
 * Created by Stephane on 18/02/2015.
 */
public class SigningUpService extends AbstractIntentService {
    public static final String EXTRA_INPUT_LOGIN = "EXTRA_INPUT_LOGIN";
    public static final String EXTRA_INPUT_PASS = "EXTRA_INPUT_PASS";
    private static String WS_QUERY_WHERE = "where";
    private String mLogin;
    private String mPass;

    @Override
    protected void processRequest() {

        if(!TextUtils.isEmpty(mLogin) && ! TextUtils.isEmpty(mPass)) {

            try {
                String content = "{\"username\":\"" + mLogin + "\",\"password\":\"" + mPass + "\"}";
                Response response = postResponse(content);
                if( response.getStatusCode() == HttpURLConnection.HTTP_CREATED) {
                    setServiceResponseCode(ServiceResponseCode.OK);
                } else {
                    setServiceResponseCode(ServiceResponseCode.KO);
                }
            } catch (IOException e) {
                setServiceResponseCode(ServiceResponseCode.KO);
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void processInputExtras(Bundle bundle) {
        super.processInputExtras(bundle);
        mLogin = bundle.getString(EXTRA_INPUT_LOGIN, null);
        mPass = bundle.getString(EXTRA_INPUT_PASS, null);
    }

    @Override
    protected void fillBundleResponse(Bundle bundle) {

    }

    @Override
    protected String getQuery() {
        return "/1/users";
    }

    @Override
    protected IHttpsConnector getConnector() {
        return new ParseConnector();
    }
}
