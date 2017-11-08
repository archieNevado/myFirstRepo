package com.coremedia.livecontext.search;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommerceSearchHandlerTest {

  @Mock
  private HttpServletRequest request;

  private CommerceSearchHandler commerceSearchHandler = new CommerceSearchHandler();

  @Test
  public void encodeQueryParameters() {
    when(request.getScheme()).thenReturn("https");
    UriComponentsBuilder providedByCommercePropertyProvider = UriComponentsBuilder.fromUriString("http://localhost:8080/");
    String redirectUrl = commerceSearchHandler.getRedirectUrl("{evil knevil}", request, null, providedByCommercePropertyProvider);
    assertEquals("https://localhost:8080/?searchTerm=%7Bevil%20knevil%7D", redirectUrl);
  }

}