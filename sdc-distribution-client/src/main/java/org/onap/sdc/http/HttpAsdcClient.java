/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * Modifications copyright (C) 2019 Nokia. All rights reserved.
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
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.onap.sdc.api.consumer.IConfiguration;
import org.onap.sdc.utils.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpAsdcClient implements IHttpAsdcClient {

    private static final String TLS = "TLSv1.2";
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String HTTPS = "https://";
    public static final int AUTHORIZATION_SCOPE_PORT = 443;
    private static Logger log = LoggerFactory.getLogger(HttpAsdcClient.class.getName());
    private CloseableHttpClient httpClient = null;
    private String serverFqdn = null;
    private String authHeaderValue = "";

    public HttpAsdcClient(IConfiguration configuraion) {
        this.serverFqdn = configuraion.getAsdcAddress();

        String username = configuraion.getUser();
        String password = configuraion.getPassword();
        initSSL(username, password, configuraion.getKeyStorePath(), configuraion.getKeyStorePassword(), configuraion.activateServerTLSAuth());

        String userNameAndPassword = username + ":" + password;
        this.authHeaderValue = "Basic " + Base64.getEncoder().encodeToString(userNameAndPassword.getBytes());
    }

    // @SuppressWarnings("deprecation")
    private void initSSL(String username, String password, String keyStorePath, String keyStoePass, boolean isSupportSSLVerification) {

        try {
            HostnameVerifier hostnameVerifier = new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };

            // SSLContextBuilder is not thread safe
            // @SuppressWarnings("deprecation")
            CredentialsProvider credsProvider = new BasicCredentialsProvider();
            credsProvider.setCredentials(new AuthScope("localhost", AUTHORIZATION_SCOPE_PORT), new UsernamePasswordCredentials(username, password));
            SSLContext sslContext;
            sslContext = SSLContext.getInstance(TLS);
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            KeyStore trustStore = null;
            tmf.init(trustStore);
            TrustManager[] tms = tmf.getTrustManagers();
            if (isSupportSSLVerification) {

                if (keyStorePath != null && !keyStorePath.isEmpty()) {
                    // trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    // trustStore.load(new FileInputStream(keyStorePath), keyStoePass.toCharArray());

                    // Using null here initialises the TMF with the default trust store.

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
                    trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                    trustStore.load(new FileInputStream(keyStorePath), keyStoePass.toCharArray());

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
                                // This will throw another CertificateException if this fails too.
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

                    tms = new TrustManager[]{customTm};

                }

                sslContext.init(null, tms, null);
                SSLContext.setDefault(sslContext);


            } else {

                SSLContextBuilder builder = new SSLContextBuilder();

                builder.loadTrustMaterial(null, new TrustStrategy() {
                    public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                        return true;
                    }
                });

                sslContext = builder.build();
            }

            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, new String[]{"TLSv1.2"}, null, hostnameVerifier);
            httpClient = HttpClientBuilder.create().
                    setDefaultCredentialsProvider(credsProvider).
                    setSSLSocketFactory(sslsf).
                    build();

        } catch (Exception e) {
            log.error("Failed to create https client", e);

        }

        return;
    }

    public HttpAsdcResponse postRequest(String requestUrl, HttpEntity entity, Map<String, String> headersMap) {
        return postRequest(requestUrl, entity, headersMap, true).getFirst();
    }

    public Pair<HttpAsdcResponse, CloseableHttpResponse> postRequest(String requestUrl, HttpEntity entity, Map<String, String> headersMap, boolean closeTheRequest) {
        Pair<HttpAsdcResponse, CloseableHttpResponse> ret;
        CloseableHttpResponse httpResponse = null;
        HttpAsdcResponse response = null;
        HttpPost httpPost = new HttpPost(HTTPS + serverFqdn + requestUrl);
        List<Header> headers = addHeadersToHttpRequest(headersMap);
        for (Header header : headers) {
            httpPost.addHeader(header);
        }

        httpPost.setHeader(AUTHORIZATION_HEADER, this.authHeaderValue);

        httpPost.setEntity(entity);
        try {
            httpResponse = httpClient.execute(httpPost);
            response = new HttpAsdcResponse(httpResponse.getStatusLine().getStatusCode(), httpResponse.getEntity());

        } catch (IOException e) {
            log.error("failed to send request to url: " + requestUrl);
            StringEntity errorEntity = null;
            try {
                errorEntity = new StringEntity("failed to send request");
            } catch (UnsupportedEncodingException e1) {
            }

            response = new HttpAsdcResponse(HttpStatus.SC_INTERNAL_SERVER_ERROR, errorEntity);

        } finally {
            if (closeTheRequest) {
                if (httpResponse != null) {
                    try {
                        httpResponse.close();

                    } catch (IOException e) {
                        log.error("failed to close http response");
                    }
                }
                ret = new Pair<>(response, null);
            } else {
                ret = new Pair<>(response, httpResponse);
            }
        }

        return ret;
    }

    public HttpAsdcResponse getRequest(String requestUrl, Map<String, String> headersMap) {

        return getRequest(requestUrl, headersMap, true).getFirst();

    }

    public Pair<HttpAsdcResponse, CloseableHttpResponse> getRequest(String requestUrl, Map<String, String> headersMap, boolean closeTheRequest) {
        Pair<HttpAsdcResponse, CloseableHttpResponse> ret;
        CloseableHttpResponse httpResponse = null;
        String url = HTTPS + serverFqdn + requestUrl;
        log.debug("url to send {}", url);
        HttpGet httpGet = new HttpGet(url);
        List<Header> headers = addHeadersToHttpRequest(headersMap);
        for (Header header : headers) {
            httpGet.addHeader(header);
        }

        httpGet.setHeader(AUTHORIZATION_HEADER, this.authHeaderValue);

        HttpAsdcResponse response = null;
        try {
            httpResponse = httpClient.execute(httpGet);

            log.debug("GET Response Status {}", httpResponse.getStatusLine().getStatusCode());
            Header[] headersRes = httpResponse.getAllHeaders();
            Map<String, String> headersResMap = new HashMap<>();
            for (Header header : headersRes) {
                headersResMap.put(header.getName(), header.getValue());
            }
            response = new HttpAsdcResponse(httpResponse.getStatusLine().getStatusCode(), httpResponse.getEntity(), headersResMap);

        } catch (UnknownHostException | ConnectException e) {
            log.error("failed to connect to url: {}", requestUrl, e);
            StringEntity errorEntity = null;
            try {
                errorEntity = new StringEntity("failed to connect");
            } catch (UnsupportedEncodingException e1) {
            }

            response = new HttpAsdcResponse(HttpStatus.SC_BAD_GATEWAY, errorEntity);

        } catch (IOException e) {
            log.error("failed to send request to url: " + requestUrl + " error " + e.getMessage());
            StringEntity errorEntity = null;
            try {
                errorEntity = new StringEntity("failed to send request " + e.getMessage());
            } catch (UnsupportedEncodingException e1) {
            }

            response = new HttpAsdcResponse(HttpStatus.SC_BAD_GATEWAY, errorEntity);

        } finally {

            if (closeTheRequest) {
                if (httpResponse != null) {
                    try {
                        httpResponse.close();

                    } catch (IOException e) {
                        log.error("failed to close http response");
                    }
                }
                ret = new Pair<HttpAsdcResponse, CloseableHttpResponse>(response, null);
            } else {
                ret = new Pair<HttpAsdcResponse, CloseableHttpResponse>(response, httpResponse);
            }
        }

        return ret;

    }

    public void closeHttpClient() {
        try {
            httpClient.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            log.error("failed to close http client");
        }

    }

    private List<Header> addHeadersToHttpRequest(Map<String, String> headersMap) {

        List<Header> requestHeaders = new ArrayList<Header>();

        Set<String> headersKyes = headersMap.keySet();
        for (String key : headersKyes) {
            Header requestHeader = new BasicHeader(key, headersMap.get(key));
            requestHeaders.add(requestHeader);
        }

        return requestHeaders;
    }

}
