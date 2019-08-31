package com.coremedia.livecontext.ecommerce.ibm.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.ibm.common.CommerceConnectionImpl;
import com.coremedia.livecontext.ecommerce.ibm.common.IbmStoreContextBuilder;
import com.coremedia.livecontext.ecommerce.ibm.login.LoginService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PreviewTokenAppendingLinkTransformerTest {

  @Spy
  private PreviewTokenAppendingLinkTransformer testling;

  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private LoginService loginService;

  @Mock
  private HttpServletRequest request;

  @Before
  public void setup() {
    testling.setPreview(true);
    testling.setLoginService(loginService);

    CommerceConnectionImpl connection = new CommerceConnectionImpl();
    connection.setStoreContext(IbmStoreContextBuilder.from(connection, "any-site-id").build());

    CurrentCommerceConnection.set(connection);

    doReturn(false).when(testling).isInitialStudioRequest(request);
    doReturn(true).when(testling).isStudioPreviewRequest(request);
    when(loginService.getPreviewToken(any()).getPreviewToken()).thenReturn("aPreviewTokenStr");
  }

  @After
  public void tearDown() {
    CurrentCommerceConnection.remove();
  }

  @Test
  public void testLinkTransformerApply() {
    String link = testling.transform("//url/to/shop", null, null, request, new MockHttpServletResponse(), false);
    assertEquals("//url/to/shop?previewToken=aPreviewTokenStr", link);

    link = testling.transform("http://url/to/shop", null, null, request, new MockHttpServletResponse(), false);
    assertEquals("http://url/to/shop?previewToken=aPreviewTokenStr", link);

    link = testling.transform("https://url/to/shop", null, null, request, new MockHttpServletResponse(), false);
    assertEquals("https://url/to/shop?previewToken=aPreviewTokenStr", link);
  }

  @Test
  public void testLinkTransformerCopyExisting() {
    when(request.getParameter("previewToken")).thenReturn("existingTokenStr");
    String link = testling.transform("//url/to/shop", null, null, request, new MockHttpServletResponse(), false);
    assertEquals("//url/to/shop?previewToken=existingTokenStr", link);

    link = testling.transform("/blueprint/internal/url", null, null, request, new MockHttpServletResponse(), false);
    assertEquals("/blueprint/internal/url?previewToken=existingTokenStr", link);
  }

  @Test
  public void testLinkTransformerCopyExistingWhenParameterValueWasSetPreviously() {
    String link = testling.transform("//url/to/shop", null, null, request, new MockHttpServletResponse(), false);
    assertEquals("//url/to/shop?previewToken=aPreviewTokenStr", link);

    when(request.getParameter("previewToken")).thenReturn("anotherTokenStr");
    link = testling.transform("/blueprint/internal/url", null, null, request, new MockHttpServletResponse(), false);
    assertEquals("/blueprint/internal/url?previewToken=anotherTokenStr", link);
  }

  @Test
  public void testLinkTransformerMiss() {
    String link = testling.transform("/blueprint/url", null, null, request, new MockHttpServletResponse(), false);
    assertEquals("/blueprint/url", link);
  }

  @Test
  public void testLinkTransformerNoStoreContextAvailable() {
    //loginService returns null if no storeContext available
    when(loginService.getPreviewToken(any())).thenReturn(null);

    String link = testling.transform("//url/to/shop", null, null, request, new MockHttpServletResponse(), false);
    assertEquals("//url/to/shop", link);
  }
}