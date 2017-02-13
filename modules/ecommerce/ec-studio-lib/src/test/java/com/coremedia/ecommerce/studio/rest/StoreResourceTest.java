package com.coremedia.ecommerce.studio.rest;


import org.assertj.core.api.Assertions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StoreResourceTest {

  @InjectMocks
  private StoreResource storeResource;

  @Mock
  private PbeShopUrlTargetResolver pbeShopUrlTargetResolver;

  @Before
  public void setup() {
    storeResource.setPbeShopUrlTargetResolvers(Collections.singletonList(pbeShopUrlTargetResolver));
    storeResource.initialize();
  }

  @Test
  public void handlePostNoData() {
    Assertions.assertThat(storeResource.handlePost(Collections.<String, Object>emptyMap())).isNull();
  }


  @Test
  public void handlePost() {
    Object o = new Object();
    String url = "http://test.net";
    when(pbeShopUrlTargetResolver.resolveUrl(url, null)).thenReturn(o);
    Assertions.assertThat(storeResource.handlePost(Collections.<String, Object>singletonMap("shopUrl", url))).isSameAs(o);
  }

}