package com.coremedia.blueprint.ecommerce.cae;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.Commerce;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionInitializer;
import com.coremedia.blueprint.base.multisite.SiteResolver;
import com.coremedia.blueprint.common.datevalidation.ValidityPeriodValidator;
import com.coremedia.blueprint.links.BlueprintUriConstants;
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

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletRequest;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.PREVIEW_DATE;
import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.WORKSPACE_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractCommerceContextInterceptorTest {

  @Mock
  private Site site;

  @Mock
  private CommerceConnectionInitializer commerceConnectionInitializer;

  @Mock
  private HttpServletRequest request;

  @Mock
  private SiteResolver siteResolver;

  private AbstractCommerceContextInterceptor testling;

  // --- setup ------------------------------------------------------

  @Before
  public void setup() {
    BaseCommerceConnection connection = MockCommerceEnvBuilder.create().setupEnv();
    when(commerceConnectionInitializer.getCommerceConnectionForSite(site)).thenReturn(connection);

    testling = new NonAbstractTestling();

    // Set all @Required properties to make it afterPropertiesSet safe.
    // Tests may override, so do not call afterPropertiesSet yet.
    testling.setSiteResolver(siteResolver);
    testling.setCommerceConnectionInitializer(commerceConnectionInitializer);
  }

  // --- tests ------------------------------------------------------

  @Test
  public void testNormalizePath() {
    String path = "/helios";

    String normalizedPath = AbstractCommerceContextInterceptor.normalizePath(path);

    assertThat(normalizedPath).as("changed path").isEqualTo(path);
  }

  @Test
  public void testNormalizeDynamicFragmentPath() {
    String path = "/cart/helios/action/cart";
    String dynamicPath = "/" + BlueprintUriConstants.Prefixes.PREFIX_DYNAMIC + path;

    String normalizedPath = AbstractCommerceContextInterceptor.normalizePath(dynamicPath);

    assertThat(normalizedPath).as("path not normalized").isEqualTo(path);
  }

  @Test
  public void testNormalizePathWithNull() {
    String path = null;

    String normalizedPath = AbstractCommerceContextInterceptor.normalizePath(path);

    assertThat(normalizedPath).isNull();
  }

  @Test
  public void testInitStoreContextProvider() {
    // This does not work with the @Mock request.
    MockHttpServletRequest request = new MockHttpServletRequest();

    CommerceConnection connection = testling.getCommerceConnectionWithConfiguredStoreContext(site, request);
    assertThat(connection).isNotNull();
    assertThat(connection.getStoreContext()).isNotNull();
  }

  @Test
  public void testInitStoreContextProviderWithPreviewParameters() {
    when(request.getParameter(ValidityPeriodValidator.REQUEST_PARAMETER_PREVIEW_DATE)).thenReturn("12-06-2014 13:00 Europe/Berlin");
    when(request.getParameter(AbstractCommerceContextInterceptor.QUERY_PARAMETER_WORKSPACE_ID)).thenReturn("aWorkspaceId");
    testling.setPreview(true);

    testling.getCommerceConnectionWithConfiguredStoreContext(site, request);

    CommerceConnection currentConnection = Commerce.getCurrentConnection();
    assertThat(currentConnection).isNotNull();

    StoreContext context = currentConnection.getStoreContext();
    assertThat(context).isNotNull();
    assertThat(context.get(PREVIEW_DATE)).isNotNull();
    assertThat(context.get(WORKSPACE_ID)).isNotNull();
  }

  // --- internal ---------------------------------------------------

  private class NonAbstractTestling extends AbstractCommerceContextInterceptor {

    @Nullable
    @Override
    protected Site getSite(HttpServletRequest request, String normalizedPath) {
      throw new UnsupportedOperationException("This dummy impl is not sufficient for your test.");
    }
  }
}
