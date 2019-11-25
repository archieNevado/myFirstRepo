package com.coremedia.livecontext.ecommerce.hybris.rest;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.HybrisITBase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.inject.Inject;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
@ActiveProfiles("oauthconnector")
public class OAuthConnectorIT extends HybrisITBase {

  @Inject
  OAuthConnector testling;

  @Test
  public void testGetOrRequestAccessToken() throws Exception {
    if (useBetamaxTapes()) {
      return;
    }

    StoreContext storeContext = getStoreContext();
    AccessToken requestedAccessToken = testling.getOrRequestAccessToken(storeContext);
    assertThat(requestedAccessToken).isNotNull();

    //Check if the identity of the newly obtained accessToken is the same as the first one
    assertThat(testling.getOrRequestAccessToken(storeContext)).isEqualTo(requestedAccessToken);
  }
}
