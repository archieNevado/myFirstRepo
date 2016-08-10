package com.coremedia.livecontext.ecommerce.ibm.common;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.net.ssl.SSLHandshakeException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

import static org.junit.Assert.assertEquals;

public class HttpClientFactoryTest {

  private static final String TEST_URL = "https://localhost:4243/";

  private Server server;

  @Before
  public void startServer() throws Exception {
    server = new Server(4280);
    server.setStopAtShutdown(true);
    server.setConnectors(new Connector[]{createSecureConnector()});
    server.start();
  }

  @After
  public void shutdownServer() throws Exception {
    server.stop();
  }

  private Connector createSecureConnector() throws URISyntaxException {
    URL keystoreUrl = getClass().getClassLoader().getResource("testkeystore.jks");
    String keystore = new File(keystoreUrl.toURI()).getAbsolutePath();

    String keyPassword = "password";

    SslSocketConnector connector = new SslSocketConnector();
    connector.setPort(4243);
    connector.setKeystore(keystore);
    connector.setKeyPassword(keyPassword);
    return connector;
  }

  @Test
  public void testCreateTrustAllHttpClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
    HttpClient httpClient = HttpClientFactory.createHttpClient(true);
    HttpResponse httpResponse = httpClient.execute(new HttpGet(TEST_URL));
    assertEquals(404, httpResponse.getStatusLine().getStatusCode());
  }

  @Test(expected = SSLHandshakeException.class)
  public void testSystemDefault() throws IOException {
    HttpClient httpClient = HttpClientFactory.createHttpClient(false);
    httpClient.execute(new HttpGet(TEST_URL));
  }
}
