package com.steto.diaw.service;

import android.os.Bundle;
import android.text.TextUtils;

import com.steto.diaw.network.Response;
import com.steto.diaw.network.connector.IHttpsConnector;
import com.steto.diaw.network.connector.ParseConnector;
import com.steto.diaw.service.model.AbstractIntentService;
import com.steto.diaw.web.QueryString;

import org.apache.http.HttpStatus;

import java.io.IOException;

/**
 * Created by Stephane on 18/02/2015.
 */
public class LoginService extends AbstractIntentService {
    public static final String EXTRA_INPUT_LOGIN = "EXTRA_INPUT_LOGIN";
    public static final String EXTRA_INPUT_PASS = "EXTRA_INPUT_PASS";
    private static String WS_QUERY_WHERE = "where";
    private String mLogin;
    private String mPass;

    @Override
    protected void processRequest() {

        if(!TextUtils.isEmpty(mLogin) && ! TextUtils.isEmpty(mPass)) {

            try {
                Response response = getResponse();
                if( response.getStatusCode() == HttpStatus.SC_OK) {
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
        return "/1/login" + createQueryString();
    }

    @Override
    protected IHttpsConnector getConnector() {
        return new ParseConnector();
    }

    private String createQueryString() {
        QueryString myQuery = new QueryString();
        myQuery.add("username", mLogin);
        myQuery.add("password", mLogin);
        return myQuery.toString();
    }
}
