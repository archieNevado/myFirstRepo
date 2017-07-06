package com.coremedia.livecontext.ecommerce.hybris.rest;

import co.freeside.betamax.Betamax;
import co.freeside.betamax.MatchRule;
import com.coremedia.livecontext.ecommerce.hybris.HybrisITBase;
import com.coremedia.livecontext.ecommerce.hybris.rest.documents.CategoryDocument;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfiguration.class)
public class HybrisRestConnectorIT extends HybrisITBase {

  @Betamax(tape = "hy_testListCatalogs", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testListCatalogs() {
    String responseValue = performGetWithStoreContext("/catalogs", String.class);

    assertThat(responseValue).isNotNull();
  }

  @Betamax(tape = "hy_testMainCatalog", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testMainCatalog() {
    String responseValue = performGetWithStoreContext("/catalogs/electronicsProductCatalog", String.class);

    assertThat(responseValue).isNotNull();
  }

  @Betamax(tape = "hy_testMainCatalogStagedVersion", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testMainCatalogStagedVersion() {
    String responseValue = performGetWithStoreContext("/catalogs/electronicsProductCatalog/catalogversions/Staged",
            String.class);

    assertThat(responseValue).isNotNull();
  }

  @Betamax(tape = "hy_testRootCategory", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testRootCategory() {
    String responseValue = performGetWithStoreContext(
            "/catalogs/electronicsProductCatalog/catalogversions/Staged/categories/brands", String.class);

    assertThat(responseValue).isNotNull();
  }

  @Betamax(tape = "hy_testSubCategory", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testSubCategory() {
    CategoryDocument responseValue = performGetWithStoreContext(
            "/catalogs/electronicsProductCatalog/catalogversions/Staged/categories/575", CategoryDocument.class);

    assertThat(responseValue).isNotNull();
  }

  @Betamax(tape = "hy_testProduct", match = {MatchRule.path, MatchRule.query})
  @Test
  public void testProduct() {
    String responseValue = performGetWithStoreContext(
            "/catalogs/electronicsProductCatalog/catalogversions/Staged/products/492274", String.class);

    assertThat(responseValue).isNotNull();
  }
}
