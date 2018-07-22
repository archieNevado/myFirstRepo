package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.UnauthorizedException;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import javax.inject.Inject;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.apache.http.HttpVersion.HTTP_1_1;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpMethod.GET;

public class WcRestConnectorUnauthorizedTest extends AbstractWrapperServiceTestCase {

  @Inject
  protected Commerce commerce;
  protected CommerceConnection connection;

  @Before
  public void setup() {
    connection = commerce.findConnection("wcs1")
            .orElseThrow(() -> new IllegalStateException("Could not obtain commerce connection."));

    storeInfoService.getWcsVersion().ifPresent(testConfig::setWcsVersion);
    connection.setStoreContext(testConfig.getStoreContext());
    CurrentCommerceConnection.set(connection);
  }

  @After
  public void teardown() {
    CurrentCommerceConnection.remove();
  }

  @Test(expected = UnauthorizedException.class)
  public void testUnauthorizedExceptionBranch() throws Exception {
    HttpResponse httpResponse = createHttpResponse(401, "Unauthorized");
    HttpClient httpClient = mockHttpClient(httpResponse);

    WcRestServiceMethod<Map, Map> serviceMethod = WcRestServiceMethod.builder(GET, "store/12345/person/@self",
            Map.class, Map.class)
            .secure(true)
            .requiresAuthentication(true)
            .build();
    List<String> variableValues = emptyList();
    Map<String, String[]> optionalParameters = emptyMap();
    Map bodyData = null;
    StoreContext storeContext = null;
    UserContext userContext = null;

    WcRestConnector wcRestConnector = new TestWcRestConnector(httpClient);

    wcRestConnector.callServiceInternal(serviceMethod, variableValues, optionalParameters, bodyData, storeContext,
            userContext);
  }

  @NonNull
  private HttpClient mockHttpClient(@NonNull HttpResponse response) throws IOException {
    HttpClient httpClient = mock(HttpClient.class);

    when(httpClient.execute(any(HttpUriRequest.class))).thenReturn(response);

    return httpClient;
  }

  @NonNull
  private HttpResponse createHttpResponse(int statusCode, @Nullable String reasonPhrase) {
    StatusLine statusLine = new BasicStatusLine(HTTP_1_1, statusCode, reasonPhrase);

    return new BasicHttpResponse(statusLine);
  }

  private static class TestWcRestConnector extends WcRestConnector {

    private HttpClient httpClient;

    private TestWcRestConnector(HttpClient httpClient) {
      this.httpClient = httpClient;
    }

    @Override
    @NonNull
    protected HttpClient getHttpClient() {
      return httpClient;
    }

    @Nullable
    @Override
    public String getServiceSslEndpoint(@Nullable StoreContext storeContext) {
      return "https://shop-ref.ecommerce.coremedia.com/wcs/resources";
    }

    @Nullable
    @Override
    public String getServiceEndpoint(@Nullable StoreContext storeContext) {
      return "http://shop-ref.ecommerce.coremedia.com/wcs/resources";
    }
  }
}
