package com.steto.diaw.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.ResultReceiver;

import com.steto.diaw.model.Episode;
import com.steto.diaw.parser.ShowParser;
import com.steto.diaw.web.HttpConnection;

public class ShowService extends IntentService{
	
	public static final String INTENT_RESULT_RECEIVER = "INTENT_RESULT_RECEIVER";
	public static final String INTENT_LOGIN = "INTENT_LOGIN";
	public static final String INTENT_PASS = "INTENT_PASS";
	public static final String URL_SHOW = "/1/classes/Show";
	public static final int RESULT_CODE_OK = 0;
	public static final String RESULT_DATA = "RESULT_DATA";

	public ShowService() {
		super("ShowService");
	}
	
	public ShowService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		ResultReceiver sender = (ResultReceiver)intent.getExtras().get(INTENT_RESULT_RECEIVER);
		String login = intent.getExtras().getString(INTENT_LOGIN);
		String pass = intent.getExtras().getString(INTENT_PASS);
		
		HttpConnection myWeb = new HttpConnection();
		try {
			InputStream response = myWeb.httpsGet(URL_SHOW);
			ShowParser myParser = new ShowParser();
			List<Episode> allEp = myParser.parse(response);
			Bundle ret = new Bundle();
			ret.putSerializable(RESULT_DATA, (Serializable)allEp);
			sender.send(RESULT_CODE_OK, ret);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
