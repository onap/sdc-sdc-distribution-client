/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * Modifications copyright (C) 2020 Nokia. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */
package org.onap.sdc.http;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.onap.sdc.api.consumer.IConfiguration;
import org.onap.sdc.utils.Pair;

public class HttpClientFactory {
    private static final int      AUTHORIZATION_SCOPE_PLAIN_PORT = 80;
    private static final int      AUTHORIZATION_SCOPE_PORT       = 443;
    private static final KeyStore DEFAULT_INIT_KEY_STORE_VALUE   = null;
    private static final String   TLS                            = "TLSv1.2";
    static final String           HTTP                           = "http://";
    static final String           HTTPS                          = "https://";
    private final IConfiguration  configuration;

    public HttpClientFactory(IConfiguration configuration) {
        this.configuration = configuration;
    }

    public Pair<String, CloseableHttpClient> createInstance() {
        boolean isHttpsRequired = configuration.isUseHttpsWithSDC();
        Pair<String, CloseableHttpClient> httpClientPair;
        if (isHttpsRequired) {
            httpClientPair = createHttpsClient(configuration);
        } else {
            httpClientPair = createHttpClient(configuration);
        }
        return httpClientPair;
    }

    private Pair<String, CloseableHttpClient> createHttpsClient(IConfiguration configuration) {
        return new Pair<>(HTTPS,
                initSSL(configuration.getUser(), configuration.getPassword(), configuration.getKeyStorePath(),
                        configuration.getKeyStorePassword(), configuration.activateServerTLSAuth()));
    }

    private Pair<String, CloseableHttpClient> createHttpClient(IConfiguration configuration) {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope("localhost", AUTHORIZATION_SCOPE_PLAIN_PORT),
                new UsernamePasswordCredentials(configuration.getUser(), configuration.getPassword()));
        return new Pair<>(HTTP, HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider)
                .setProxy(getHttpProxyHost()).build());
    }

    private CloseableHttpClient initSSL(String username, String password, String keyStorePath, String keyStorePass,
            boolean isSupportSSLVerification) {

        try {

            // SSLContextBuilder is not thread safe
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope("localhost", AUTHORIZATION_SCOPE_PORT),
                    new UsernamePasswordCredentials(username, password));
            SSLContext sslContext;
            sslContext = SSLContext.getInstance(TLS);
            TrustManagerFactory tmf = createTrustManagerFactory();
            TrustManager[] tms = tmf.getTrustManagers();
            if (isSupportSSLVerification) {

                if (keyStorePath != null && !keyStorePath.isEmpty()) {
                    // Using null here initialises the TMF with the default
                    // trust store.

                    // Get hold of the default trust manager
                    X509TrustManager defaultTm = null;
                    for (TrustManager tm : tmf.getTrustManagers()) {
                        if (tm instanceof X509TrustManager) {
                            defaultTm = (X509TrustManager) tm;
                            break;
                        }
                    }

                    // Do the same with your trust store this time
                    // Adapt how you load the keystore to your needs
                    KeyStore trustStore = loadKeyStore(keyStorePath, keyStorePass);

                    tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                    tmf.init(trustStore);

                    // Get hold of the default trust manager
                    X509TrustManager myTm = null;
                    for (TrustManager tm : tmf.getTrustManagers()) {
                        if (tm instanceof X509TrustManager) {
                            myTm = (X509TrustManager) tm;
                            break;
                        }
                    }

                    // Wrap it in your own class.
                    final X509TrustManager finalDefaultTm = defaultTm;
                    final X509TrustManager finalMyTm = myTm;
                    X509TrustManager customTm = new X509TrustManager() {
                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            // If you're planning to use client-cert auth,
                            // merge results from "defaultTm" and "myTm".
                            return finalDefaultTm.getAcceptedIssuers();
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {
                            try {
                                finalMyTm.checkServerTrusted(chain, authType);
                            } catch (CertificateException e) {
                                // This will throw another CertificateException
                                // if this fails too.
                                finalDefaultTm.checkServerTrusted(chain, authType);
                            }
                        }

                        @Override
                        public void checkClientTrusted(X509Certificate[] chain, String authType)
                                throws CertificateException {
                            // If you're planning to use client-cert auth,
                            // do the same as checking the server.
                            finalDefaultTm.checkClientTrusted(chain, authType);
                        }
                    };

                    tms = new TrustManager[] { customTm };

                }

                sslContext.init(null, tms, null);
                SSLContext.setDefault(sslContext);

            } else {

                SSLContextBuilder builder = new SSLContextBuilder();

                builder.loadTrustMaterial(null, (chain, authType) -> true);

                sslContext = builder.build();
            }

            HostnameVerifier hostnameVerifier = (hostname, session) -> hostname.equalsIgnoreCase(session.getPeerHost());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new String[] { TLS }, null,
                    hostnameVerifier);
            return HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider).setProxy(getHttpsProxyHost())
                    .setSSLSocketFactory(sslsf).build();
        } catch (Exception e) {
            throw new HttpSdcClientException("Failed to create https client", e);
        }
    }

    private TrustManagerFactory createTrustManagerFactory() throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(DEFAULT_INIT_KEY_STORE_VALUE);
        return tmf;
    }

    private KeyStore loadKeyStore(String keyStorePath, String keyStorePass)
            throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream keyStoreData = new FileInputStream(keyStorePath)) {
            trustStore.load(keyStoreData, keyStorePass.toCharArray());
        }
        return trustStore;
    }

    private HttpHost getHttpProxyHost() {
        HttpHost proxyHost = null;
        if (configuration.isUseSystemProxy() && System.getProperty("http.proxyHost") != null
                && System.getProperty("http.proxyPort") != null) {
            proxyHost = new HttpHost(System.getProperty("http.proxyHost"),
                    Integer.valueOf(System.getProperty("http.proxyPort")));
        } else if (configuration.getHttpProxyHost() != null && configuration.getHttpProxyPort() != 0) {
            proxyHost = new HttpHost(configuration.getHttpProxyHost(), configuration.getHttpProxyPort());
        }
        return proxyHost;
    }

    private HttpHost getHttpsProxyHost() {
        HttpHost proxyHost = null;
        if (configuration.isUseSystemProxy() && System.getProperty("https.proxyHost") != null
                && System.getProperty("https.proxyPort") != null) {
            proxyHost = new HttpHost(System.getProperty("https.proxyHost"),
                    Integer.valueOf(System.getProperty("https.proxyPort")));
        } else if (configuration.getHttpsProxyHost() != null && configuration.getHttpsProxyPort() != 0) {
            proxyHost = new HttpHost(configuration.getHttpsProxyHost(), configuration.getHttpsProxyPort());
        }
        return proxyHost;
    }

}
