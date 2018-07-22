package com.coremedia.livecontext.ecommerce.hybris;

import co.freeside.betamax.Recorder;
import com.coremedia.blueprint.lc.test.BetamaxTestHelper;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.hybris.rest.HybrisRestConnector;
import org.junit.Before;
import org.junit.Rule;

import edu.umd.cs.findbugs.annotations.NonNull;
import javax.inject.Inject;
import java.util.Locale;

public class HybrisITBase {

  @Inject
  private HybrisRestConnector connector;

  @Rule
  public Recorder recorder = new Recorder(BetamaxTestHelper.updateSystemPropertiesWithBetamaxConfig());

  private StoreContext storeContext;

  @Before
  public void setup() {
    storeContext = HybrisTestStoreContextBuilder.build("apparel-uk", "Apparel-Catalog",
            CatalogId.of("apparelProductCatalog"), Locale.ENGLISH, "USD", "Staged");
  }

  protected <T> T performGetWithStoreContext(@NonNull String resourcePath, @NonNull Class<T> responseType) {
    return connector.performGet(resourcePath, storeContext, responseType);
  }
}
