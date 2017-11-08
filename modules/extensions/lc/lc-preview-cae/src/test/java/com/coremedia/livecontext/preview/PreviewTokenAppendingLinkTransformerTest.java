package com.coremedia.livecontext.preview;


import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.ecommerce.common.CommercePropertyProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class PreviewTokenAppendingLinkTransformerTest {

  @Spy
  private PreviewTokenAppendingLinkTransformer testling;

  @Mock
  private CommercePropertyProvider previewTokenProvider;

  @Mock
  private BaseCommerceConnection connection;

  private MockCommerceEnvBuilder envBuilder;

  @Before
  public void setup(){
    testling.setPreview(true);
    testling.setPreviewTokenProvider(previewTokenProvider);
    envBuilder = MockCommerceEnvBuilder.create();
    connection = envBuilder.setupEnv();
    connection.setVendorName("IBM");

    doReturn(false).when(testling).isInitialStudioRequest();
    doReturn(true).when(testling).isStudioPreviewRequest();
    when(previewTokenProvider.provideValue(anyMap())).thenReturn("aPreviewTokenStr");
  }

  @After
  public void tearDown() throws Exception {
    envBuilder.tearDownEnv();
  }

  @Test
  public void testLinkTransformerApply() {
    String link = testling.transform("//url/to/shop", null, null, mock(HttpServletRequest.class), new MockHttpServletResponse(), false);
    assertEquals("//url/to/shop?previewToken=aPreviewTokenStr", link);

    link = testling.transform("http://url/to/shop", null, null, mock(HttpServletRequest.class), new MockHttpServletResponse(), false);
    assertEquals("http://url/to/shop?previewToken=aPreviewTokenStr", link);

    link = testling.transform("https://url/to/shop", null, null, mock(HttpServletRequest.class), new MockHttpServletResponse(), false);
    assertEquals("https://url/to/shop?previewToken=aPreviewTokenStr", link);
  }

  @Test
  public void testLinkTransformerCopyExsiting() {
    HttpServletRequest request = mock(HttpServletRequest.class);
    when(request.getParameter("previewToken")).thenReturn("existingTokenStr");

    String link = testling.transform("//url/to/shop", null, null, request, new MockHttpServletResponse(), false);
    assertEquals("//url/to/shop?previewToken=existingTokenStr", link);

    link = testling.transform("/blueprint/internal/url", null, null, request, new MockHttpServletResponse(), false);
    assertEquals("/blueprint/internal/url?previewToken=existingTokenStr", link);
  }

  @Test
  public void testLinkTransformerMiss() {
    doReturn(false).when(testling).isStudioPreviewRequest();

    String link = testling.transform("/blueprint/url", null, null, mock(HttpServletRequest.class), new MockHttpServletResponse(), false);
    assertEquals("/blueprint/url", link);
  }

  @Test
  public void testLinkTransformerNoStoreContextAvailable() {
    //previewTokenProvider returns null if no storeContext available
    when(previewTokenProvider.provideValue(anyMap())).thenReturn(null);

    String link = testling.transform("//url/to/shop", null, null, mock(HttpServletRequest.class), new MockHttpServletResponse(), false);
    assertEquals("//url/to/shop", link);
  }


}