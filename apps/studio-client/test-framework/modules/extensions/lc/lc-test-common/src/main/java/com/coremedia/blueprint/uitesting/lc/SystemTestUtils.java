package com.coremedia.blueprint.uitesting.lc;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.HttpClientFactory;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static com.google.common.base.Preconditions.checkState;

public class SystemTestUtils {

  private static final Logger LOG = LoggerFactory.getLogger(SystemTestUtils.class);

  public static int callUrlAndReturnStatusCode(String url) throws IOException, URISyntaxException {
    HttpGet request = createGetRequest(url);
    LOG.info("callUrlAndReturnStatusCode() url: {}", url);

    HttpResponse response = executeRequest(request);

    StatusLine statusLine = response.getStatusLine();
    LOG.info("callUrlAndReturnStatusCode() statusLine: {}", statusLine);
    checkState(statusLine != null, "Response status line must not be null.");

    return statusLine.getStatusCode();
  }

  public static HttpResponse callUrlAndReturnResponse(String url) throws IOException, URISyntaxException {
    HttpGet request = createGetRequest(url);
    LOG.info("callUrlAndReturnStatusCode() url: {}", url);

    return executeRequest(request);
  }

  private static HttpGet createGetRequest(String url) throws URISyntaxException {
    HttpGet request = new HttpGet();
    request.setURI(new URI(url));
    return request;
  }

  public static HttpResponse executeRequest(HttpUriRequest request) throws IOException {
    HttpClient httpClient = HttpClientFactory.createHttpClient(true);
    return httpClient.execute(request);
  }
}
