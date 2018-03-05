package com.coremedia.livecontext.ecommerce.sfcc.ocapi;

import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Answers.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.withSettings;

class AbstractOCAPIConnectorRequestUrlBuildingTest {

  private AbstractOCAPIConnector connector;

  @BeforeEach
  void setUp() throws Exception {
    connector = getConnector();
  }

  @Test
  void withoutCustomBasePath() {
    Map<String, String> pathParams = ImmutableMap.of("catalogId", "winter-2017", "categoryId", "accessories");
    ListMultimap<String, String> queryParams = ImmutableListMultimap.of();

    String actual = connector.buildRequestUrl("/catalogs", pathParams, queryParams);

    assertThat(actual).isEqualTo("https://shop-ref.demandware.net/base-path/catalogs");
  }

  @Test
  void withoutPathParameters() {
    Map<String, String> pathParams = ImmutableMap.of("catalogId", "winter-2017", "categoryId", "accessories");
    ListMultimap<String, String> queryParams = ImmutableListMultimap.of();

    String actual = connector.buildRequestUrl("/catalogs", pathParams, queryParams);

    assertThat(actual).isEqualTo("https://shop-ref.demandware.net/base-path/catalogs");
  }

  @Test
  void withPathParameters() {
    Map<String, String> pathParams = ImmutableMap.of("catalogId", "winter-2017", "categoryId", "accessories");
    ListMultimap<String, String> queryParams = ImmutableListMultimap.of();

    String actual = connector.buildRequestUrl("/catalogs/{catalogId}/categories/{categoryId}", pathParams, queryParams);

    assertThat(actual).isEqualTo("https://shop-ref.demandware.net/base-path/catalogs/winter-2017/categories/accessories");
  }

  @Test
  void withEncoding() {
    Map<String, String> pathParams = ImmutableMap.of("catalogId", "winter-2017", "categoryId", "foo#bar");

    ListMultimap<String, String> queryParams = ImmutableListMultimap.<String, String>builder()
            .put("one", "foo&bar")
            .put("two", "bar?foo")
            .build();

    String actual = connector.buildRequestUrl("/catalogs/{catalogId}/categories/{categoryId}", pathParams, queryParams
    );

    assertThat(actual).isEqualTo(
            "https://shop-ref.demandware.net/base-path/catalogs/winter-2017/categories/foo%23bar?one=foo%26bar&two=bar%3Ffoo");
  }

  @Test
  void withQueryParameters() {
    Map<String, String> pathParams = ImmutableMap.of();

    ListMultimap<String, String> queryParams = ImmutableListMultimap.<String, String>builder()
            .put("one", "eins")
            .put("two", "zwei")
            .build();

    String actual = connector.buildRequestUrl("/catalogs", pathParams, queryParams);

    assertThat(actual).isEqualTo("https://shop-ref.demandware.net/base-path/catalogs?one=eins&two=zwei");
  }

  private static AbstractOCAPIConnector getConnector() throws Exception {
    SfccOcapiConfigurationProperties props = new SfccOcapiConfigurationProperties();
    props.setHost("shop-ref.demandware.net");

    return mock(AbstractOCAPIConnector.class,
            withSettings().useConstructor(props, "base-path", null).defaultAnswer(CALLS_REAL_METHODS));
  }
}
