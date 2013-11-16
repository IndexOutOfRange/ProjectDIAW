package com.steto.diaw.account;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

/**
 * A bound Service that instantiates the authenticator
 * when started.
 */
public class AccountService extends Service {
	
	private static final String ACCOUNT_TYPE = "com.steto.diaw";

	// Instance field that stores the authenticator object
	private Authenticator mAuthenticator;

	

    /**
     * Obtain a handle to the {@link android.accounts.Account} used for sync in this application.
     *
     * @return Handle to application's account (not guaranteed to resolve unless CreateSyncAccount()
     *         has been called)
     */
    public static Account GetAccount(String accountName) {
        return new Account(accountName, ACCOUNT_TYPE);
    }
	@Override
	public void onCreate() {
		mAuthenticator = new Authenticator(this);
	}

	/*
	 * When the system binds to this Service to make the RPC call
	 * return the authenticator's IBinder.
	 */
	@Override
	public IBinder onBind(Intent intent) {
		return mAuthenticator.getIBinder();
	}
	
	public class Authenticator extends AbstractAccountAuthenticator {
		
		public Authenticator(Context context) {
	        super(context);
	    }

		@Override
		public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options)
				throws NetworkErrorException {
			return null;
		}

		@Override
		public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
			return null;
		}

		@Override
		public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
	        throw new UnsupportedOperationException();
		}

		@Override
		public String getAuthTokenLabel(String authTokenType) {
	        throw new UnsupportedOperationException();
		}

		@Override
		public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
	        throw new UnsupportedOperationException();
		}

		@Override
		public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
	        throw new UnsupportedOperationException();
		}
	}
}