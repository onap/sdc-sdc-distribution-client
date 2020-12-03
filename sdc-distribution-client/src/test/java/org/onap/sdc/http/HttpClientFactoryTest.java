package org.onap.sdc.http;

import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.onap.sdc.utils.Pair;
import org.onap.sdc.utils.TestConfiguration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


public class HttpClientFactoryTest {

    @Test
    public void shouldReturnSSLConnection(){
        TestConfiguration config = spy(new TestConfiguration());
        HttpClientFactory httpClientFactory = new HttpClientFactory(config);
        when(config.activateServerTLSAuth()).thenReturn(true);
        when(config.getKeyStorePath()).thenReturn("src/test/resources/asdc-client.jks");
        when(config.getKeyStorePassword()).thenReturn("Aa123456");
        Pair<String, CloseableHttpClient> client =  httpClientFactory.createInstance();
        SSLConnectionSocketFactory sslsf = spy(SSLConnectionSocketFactory.getSocketFactory());
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        CloseableHttpClient expectedHttpClient = HttpClientBuilder.create().
                setDefaultCredentialsProvider(credsProvider).
                setSSLSocketFactory(sslsf).
                build();
        Pair<String, CloseableHttpClient> expectedClient = new Pair<>("https://", expectedHttpClient);
        assertNotNull(client);
        assertEquals(expectedClient.getFirst(), client.getFirst());
    }

    @Test (expected = HttpAsdcClientException.class)
    public void shouldReturnSSLConnectionError() throws HttpAsdcClientException{
        TestConfiguration config = spy(new TestConfiguration());
        HttpClientFactory httpClientFactory = new HttpClientFactory(config);
        when(config.activateServerTLSAuth()).thenReturn(true);
        when(config.getKeyStorePath()).thenReturn("src/test/resources/dummy.jks");
        when(config.getKeyStorePassword()).thenReturn("Aa123456");
        httpClientFactory.createInstance();
    }

}