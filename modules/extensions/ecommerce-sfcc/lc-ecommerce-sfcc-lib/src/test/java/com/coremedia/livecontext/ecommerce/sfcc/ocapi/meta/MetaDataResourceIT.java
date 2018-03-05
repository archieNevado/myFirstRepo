package com.coremedia.livecontext.ecommerce.sfcc.ocapi.meta;

import com.coremedia.livecontext.ecommerce.sfcc.configuration.SfccConfigurationProperties;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.OCAPITestBase;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.meta.documents.ApiVersionDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base class for all meta API resource tests.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {
        MetaDataTestConfiguration.class,
})
@TestPropertySource(properties = {
        "livecontext.sfcc.ocapi.metaBasePath=/s/-/dw/meta/v1/"
})
public class MetaDataResourceIT extends OCAPITestBase {

  // version needs to be adjusted when API is updated
  private static final String EXPECTED_API_VERSION = SfccConfigurationProperties.SFCC_VENDOR_VERSION;

  @Inject
  private MetaDataResource resource;

  @Test
  public void testGetAvailableApis() {
    if (useBetamaxTapes()) {
      return;
    }

    Map<String, String> apis = resource.getAvailableApis();
    // Make sure, that at least Data and Shop APIs are available
    assertThat(apis).containsKeys("data", "meta", "shop");
  }

  @Test
  public void testGetAvailableDataApiVersions() {
    if (useBetamaxTapes()) {
      return;
    }

    List<ApiVersionDocument> dataApiVersions = resource.getAvailableApiVersions("data");

    Optional<ApiVersionDocument> document = dataApiVersions.stream()
            .filter(apiVersionDocument -> apiVersionDocument.getName().equals(EXPECTED_API_VERSION))
            .findFirst();

    assertThat(document).isNotEmpty()
            .map(ApiVersionDocument::getStatus).isNotEqualTo("obsolete");
  }

  @Test
  public void testGetAvailableShopApiVersions() {
    if (useBetamaxTapes()) {
      return;
    }

    List<ApiVersionDocument> dataApiVersions = resource.getAvailableApiVersions("shop");

    Optional<ApiVersionDocument> document = dataApiVersions.stream()
            .filter(apiVersionDocument -> apiVersionDocument.getName().equals(EXPECTED_API_VERSION))
            .findFirst();

    assertThat(document).isNotEmpty()
            .map(ApiVersionDocument::getStatus).isNotEqualTo("obsolete");
  }
}
