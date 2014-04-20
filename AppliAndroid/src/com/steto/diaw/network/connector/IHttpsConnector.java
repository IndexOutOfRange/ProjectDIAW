package com.steto.diaw.network.connector;

import java.io.IOException;

import com.steto.diaw.network.Response;

public interface IHttpsConnector {

	Response getData(String url) throws IOException;

	Response postData(String url, String content) throws IOException;

	Response putData(String url, String content) throws IOException;

	Response deleteData(String url, String content) throws IOException;
}
