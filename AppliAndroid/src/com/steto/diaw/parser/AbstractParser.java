package com.steto.diaw.parser;

/**
 * Created by Stephane on 02/06/13.
 */
public abstract class AbstractParser {

	//les erreurs de parsing vont de 0 a -10
	public static int PARSER_OK = 0;
	public static int PARSER_MALFORMED_JSON = -1;
	public static int PARSER_OBJECT_MALFORMED = -2;
	public static int PARSER_GENERAL_EXCEPTION = -3;

	private int mStatusCode = PARSER_OK;


	public int getStatusCode() {
		return mStatusCode;
	}

	public void setStatusCode(int mStatusCode) {
		this.mStatusCode = mStatusCode;
	}
}
