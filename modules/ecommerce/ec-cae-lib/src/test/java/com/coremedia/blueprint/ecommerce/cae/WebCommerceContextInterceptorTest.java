package com.coremedia.blueprint.ecommerce.cae;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.multisite.SiteHelper;
import com.coremedia.blueprint.base.multisite.cae.SiteResolver;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.newStoreContext;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WebCommerceContextInterceptorTest {

  @Mock
  private SiteResolver siteResolver;

  @Mock
  private Site site;

  @Mock
  private CommerceConnectionInitializer commerceConnectionInitializer;

  private BaseCommerceConnection connection;

  private MockHttpServletRequest request;
  private HttpServletResponse response;
  private Object handler;

  private WebCommerceContextInterceptor testling;

  @Before
  public void setup() {
    testling = new WebCommerceContextInterceptor();
    StoreContext storeContext = newStoreContext();

    connection = new BaseCommerceConnection();
    connection.setStoreContext(storeContext);
    CurrentCommerceConnection.set(connection);

    when(commerceConnectionInitializer.findConnectionForSite(site)).thenReturn(Optional.of(this.connection));

    testling.setSiteResolver(siteResolver);
    testling.setInitUserContext(false);
    testling.setCommerceConnectionInitializer(commerceConnectionInitializer);
    testling.setPreview(false);

    request = new MockHttpServletRequest();
    response = new MockHttpServletResponse();
    handler = new Object();
  }

  @Test
  public void testPreHandle() {
    String path = "/helios";
    request.setPathInfo(path);

    when(siteResolver.findSiteByPath(path)).thenReturn(site);

    testling.preHandle(request, response, handler);

    verify(commerceConnectionInitializer).findConnectionForSite(any(Site.class));
    assertThat(SiteHelper.findSite(request)).isPresent();
  }

  @Test
  public void testNoopPreHandle() {
    String path = "/nosite";
    request.setPathInfo(path);

    StoreContext storeContextBefore = connection.getStoreContext();

    testling.preHandle(request, response, handler);

    verify(commerceConnectionInitializer, never()).findConnectionForSite(any(Site.class));
    assertThat(connection.getStoreContext()).isSameAs(storeContextBefore);
    assertThat(SiteHelper.findSite(request)).isNotPresent();
  }
}
