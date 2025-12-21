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
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContexts;
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
        return new Pair<>(HTTPS, initSSLMtls(configuration));
    }

    private Pair<String, CloseableHttpClient> createHttpClient(IConfiguration configuration) {
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope("localhost", AUTHORIZATION_SCOPE_PLAIN_PORT),
                new UsernamePasswordCredentials(configuration.getUser(), configuration.getPassword()));
        return new Pair<>(HTTP, HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider)
                .setProxy(getHttpProxyHost()).build());
    }

    private CloseableHttpClient initSSLMtls(IConfiguration configuration) {

        try (FileInputStream kis = new FileInputStream(configuration.getKeyStorePath());
            FileInputStream tis = new FileInputStream(configuration.getTrustStorePath())) {

            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope("localhost", AUTHORIZATION_SCOPE_PORT),
                new UsernamePasswordCredentials(configuration.getUser(), configuration.getPassword()));

            final KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(kis, configuration.getKeyStorePassword().toCharArray());
            final KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(ks, configuration.getKeyStorePassword().toCharArray());

            final KeyStore ts = KeyStore.getInstance("JKS");
            ts.load(tis, configuration.getTrustStorePassword().toCharArray());
            final TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(ts);

            final SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(ts, new TrustSelfSignedStrategy()).loadKeyMaterial(ks, configuration.getKeyStorePassword().toCharArray()).build();
            HostnameVerifier hostnameVerifier = (hostname, session) -> hostname.equalsIgnoreCase(session.getPeerHost());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new String[] { TLS }, null,
                hostnameVerifier);

            return HttpClientBuilder.create().setDefaultCredentialsProvider(credsProvider).setProxy(getHttpsProxyHost())
                .setSSLSocketFactory(sslsf).build();
        } catch (Exception e) {
            throw new HttpSdcClientException("Failed to create https client", e);
        }
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
