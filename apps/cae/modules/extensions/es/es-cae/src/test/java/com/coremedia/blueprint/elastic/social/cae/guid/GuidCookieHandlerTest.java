package com.coremedia.blueprint.elastic.social.cae.guid;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.common.contentbeans.CMChannel;
import com.coremedia.elastic.core.api.settings.Settings;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponents;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

import static com.coremedia.blueprint.base.links.UriConstants.Segments.PREFIX_DYNAMIC;
import static com.coremedia.blueprint.elastic.social.cae.guid.GuidCookieHandler.GUID_COOKIE_PREFIX;
import static com.coremedia.blueprint.links.BlueprintUriConstants.Prefixes.PREFIX_SERVICE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GuidCookieHandlerTest {

  @InjectMocks
  private GuidCookieHandler testling;

  @Mock
  private CMChannel rootChannelBean;

  @Mock
  private SettingsService settingsService;

  @Mock
  private Settings settings;

  @After
  public void cleanUp() {
    RequestContextHolder.resetRequestAttributes();
  }

  @Test
  public void testValidateGuidNoContent() {
    boolean isValid = testling.validateGuid("a");
    assertFalse(isValid);
  }

  @Test
  public void testValidateGuid() {
    String guid1 = testling.createGuid();
    String guid2 = testling.createGuid();
    assertNotSame(guid1, guid2);

    assertTrue(testling.validateGuid(guid1));
    assertTrue(testling.validateGuid(guid2));
  }

  @Test
  public void getCurrentGuid() {
    HttpServletRequest request = new MockHttpServletRequest();
    ServletRequestAttributes servletRequestAttributes = new ServletRequestAttributes(request);
    servletRequestAttributes.setAttribute("guid", "1234+5", ServletRequestAttributes.SCOPE_REQUEST);
    RequestContextHolder.setRequestAttributes(servletRequestAttributes);
    String guid = GuidCookieHandler.getCurrentGuid();
    assertEquals("1234+5", guid);
  }

  /**
   * test link generation
   */
  @Test
  public void buildLink() {
    when(rootChannelBean.getContentId()).thenReturn(42);
    when(rootChannelBean.isRoot()).thenReturn(true);

    UriComponents uriComponents = testling.buildGUIDLink(rootChannelBean);

    assertNotNull(uriComponents);

    String expectedURI = "/" + PREFIX_DYNAMIC + "/" + PREFIX_SERVICE + "/" + GUID_COOKIE_PREFIX + "/" + 42;
    assertEquals(expectedURI, uriComponents.toUriString());
  }

  /**
  * no cookie in the session scope => new cookie will be set
  */
  @Test
  public void handlerSetsCookie() {
    when(settingsService.nestedSetting(Arrays.asList("elasticSocial", "enabled"), Boolean.class, rootChannelBean)).thenReturn(true);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getCookies()).thenReturn(new Cookie[0]);

    testling.handleRequest(rootChannelBean, request, response);

    verify(response).addCookie(argThat(cookie -> "guid".equals(cookie.getName())));
    verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  /**
   * cookie is already there and it is correct => do nothing, return OK
   */
  @Test
  public void handlerExtractsExistingCookie() {
    when(settingsService.nestedSetting(Arrays.asList("elasticSocial", "enabled"), Boolean.class, rootChannelBean)).thenReturn(true);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    String existingGUID = testling.createGuid();
    when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("guid", existingGUID)});

    testling.handleRequest(rootChannelBean, request, response);

    verify(response, never()).addCookie(any());
    verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
  }

  /**
   * ES is turned off in settings => no cookie should be set
   */
  @Test
  public void handlerESisOFF() {
    when(settingsService.nestedSetting(Arrays.asList("elasticSocial", "enabled"), Boolean.class, rootChannelBean)).thenReturn(false);

    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    testling.handleRequest(rootChannelBean, request, response);

    verify(response, never()).addCookie(any());
    verify(response).setStatus(HttpServletResponse.SC_NO_CONTENT);
  }
}
