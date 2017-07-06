package com.coremedia.livecontext.ecommerce.hybris.rest;

import com.coremedia.livecontext.ecommerce.hybris.HybrisITBase;
import com.coremedia.livecontext.ecommerce.hybris.SystemProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("oauthconnector")
public class OAuthConnectorIT extends HybrisITBase {

  @Inject
  OAuthConnector testling;

  @Test
  public void testGetOrRequestAccessToken() throws Exception {
    if (!"*".equals(SystemProperties.getBetamaxIgnoreHosts())) {
      return;
    }

    AccessToken accessToken = testling.getOrRequestAccessToken();
    assertThat(accessToken).isNotNull();

    assertThat(testling.getOrRequestAccessToken()).isEqualTo(accessToken);
  }
}
