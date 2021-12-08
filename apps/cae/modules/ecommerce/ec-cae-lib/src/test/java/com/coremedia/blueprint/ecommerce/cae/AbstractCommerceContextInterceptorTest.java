package com.coremedia.blueprint.ecommerce.cae;

import com.coremedia.blueprint.base.links.UriConstants;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextBuilderImpl;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.blueprint.common.datevalidation.ValidityPeriodValidator;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cms.delivery.configuration.DeliveryConfigurationProperties;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AbstractCommerceContextInterceptorTest {

  private DeliveryConfigurationProperties deliveryConfigurationProperties;

  @Mock
  private Site site;

  @Mock
  private CommerceConnectionSupplier commerceConnectionSupplier;

  @Spy
  private AbstractCommerceContextInterceptor testling;

  @Mock
  private StoreContextProvider storeContextProvider;

  // --- setup ------------------------------------------------------

  @Before
  public void setup() {
    deliveryConfigurationProperties = new DeliveryConfigurationProperties();
    deliveryConfigurationProperties.setPreviewMode(false);
    testling.setDeliveryConfigurationProperties(deliveryConfigurationProperties);

    BaseCommerceConnection commerceConnection = new BaseCommerceConnection();
    commerceConnection.setStoreContextProvider(storeContextProvider);
    commerceConnection.setInitialStoreContext(StoreContextBuilderImpl.from(commerceConnection, "any-site-id").build());

    when(commerceConnectionSupplier.findConnection(site)).thenReturn(Optional.of(commerceConnection));

    testling.setCommerceConnectionSupplier(commerceConnectionSupplier);

    when(storeContextProvider.buildContext(any())).thenAnswer(invocationOnMock -> {
      Object argument = invocationOnMock.getArgument(0);
      return StoreContextBuilderImpl.from((StoreContextImpl) argument);
    });
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
    String dynamicPath = "/" + UriConstants.Segments.PREFIX_DYNAMIC + path;

    String normalizedPath = AbstractCommerceContextInterceptor.normalizePath(dynamicPath);

    assertThat(normalizedPath).as("path not normalized").isEqualTo(path);
  }

  @Test
  public void testNormalizePathWithNull() {
    String normalizedPath = AbstractCommerceContextInterceptor.normalizePath(null);

    assertThat(normalizedPath).isNull();
  }

  @Test
  public void testInitStoreContextProvider() {
    // This does not work with the @Mock request.
    MockHttpServletRequest request = new MockHttpServletRequest();

    Optional<CommerceConnection> connection = testling.getCommerceConnectionWithConfiguredStoreContext(site, request);
    assertThat(connection)
            .isNotEmpty()
            .map(CommerceConnection::getInitialStoreContext)
            .isNotEmpty();
  }

  @Test
  public void testInitStoreContextProviderWithPreviewParameters() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addParameter(ValidityPeriodValidator.REQUEST_PARAMETER_PREVIEW_DATE, "12-06-2014 13:00 Europe/Berlin");
    deliveryConfigurationProperties.setPreviewMode(true);

    Optional<CommerceConnection> connection = testling.getCommerceConnectionWithConfiguredStoreContext(site, request);

    assertThat(connection)
            .isNotEmpty()
            .map(CommerceConnection::getInitialStoreContext)
            .hasValueSatisfying(
                    context -> {
                      assertThat(context.getPreviewDate()).isEmpty();
                    }
            );

    Optional<StoreContext> updatedStoreContext = CurrentStoreContext.find(request);
    assertThat(updatedStoreContext)
            .isNotEmpty()
            .hasValueSatisfying(
                    context -> {
                      assertThat(context.getPreviewDate()).isPresent();
                    }
            );
  }
}
