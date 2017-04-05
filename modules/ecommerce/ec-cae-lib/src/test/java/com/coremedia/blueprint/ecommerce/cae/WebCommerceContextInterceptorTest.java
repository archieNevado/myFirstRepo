package com.coremedia.blueprint.ecommerce.cae;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.base.multisite.SiteResolver;
import com.coremedia.cap.multisite.Site;
import com.coremedia.ecommerce.test.MockCommerceEnvBuilder;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebCommerceContextInterceptorTest {

  @Mock
  private SiteResolver siteLinkHelper;

  @Mock
  private Site site;

  @Mock
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Mock
  private CommerceConnection connection;

  private WebCommerceContextInterceptor testling = new WebCommerceContextInterceptor();

  @Before
  public void setup() {
    connection = MockCommerceEnvBuilder.create().setupEnv();
    when(commerceConnectionInitializer.findConnectionForSite(site)).thenReturn(Optional.of(connection));

    testling.setSiteResolver(siteLinkHelper);
    testling.setInitUserContext(false);
    testling.setCommerceConnectionInitializer(commerceConnectionInitializer);
    testling.setPreview(false);

    when(siteLinkHelper.findSiteBySegment("helios")).thenReturn(site);
  }

  // --- test base class features -----------------------------------

  @Test
  public void testPreHandle() {
    String path = "/helios";
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setPathInfo(path);
    when(testling.getSite(request, path)).thenReturn(site);

    testling.preHandle(request, null, null);

    verify(commerceConnectionInitializer).findConnectionForSite(any(Site.class));
    assertThat(SiteHelper.getSiteFromRequest(request)).isNotNull();
  }

  @Test
  public void testNoopPreHandle() {
    String path = "/nosite";
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setPathInfo(path);
    when(testling.getSite(request, path)).thenReturn(null);

    testling.preHandle(request, null, null);

    verify(connection.getStoreContextProvider(), never()).setCurrentContext(any(StoreContext.class));
    assertThat(SiteHelper.getSiteFromRequest(request)).isNull();
  }
}
