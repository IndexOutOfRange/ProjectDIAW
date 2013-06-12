package com.steto.diaw.tools;
/*
 *  Copyright 2010 Kevin Gaudin
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import javax.net.ssl.X509TrustManager;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Accepts any certificate, ideal for self-signed certificates.
 * source : http://code.google.com/p/acra/source/browse/trunk/acra/src/main/java/org/acra/util/NaiveTrustManager.java?r=733
 */
class NaiveTrustManager implements X509TrustManager {
	/*
	 * (non-Javadoc)
	 *
	 * @see javax.net.ssl.X509TrustManager#getAcceptedIssuers()
	 */
	public X509Certificate[] getAcceptedIssuers() {
		return new X509Certificate[0];
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * javax.net.ssl.X509TrustManager#checkClientTrusted(java.security.cert.
	 * X509Certificate[], java.lang.String)
	 */
	public void checkClientTrusted(X509Certificate[] x509CertificateArray,
	                               String string) throws CertificateException {
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * javax.net.ssl.X509TrustManager#checkServerTrusted(java.security.cert.
	 * X509Certificate[], java.lang.String)
	 */
	public void checkServerTrusted(X509Certificate[] x509CertificateArray,
	                               String string) throws CertificateException {
	}
}