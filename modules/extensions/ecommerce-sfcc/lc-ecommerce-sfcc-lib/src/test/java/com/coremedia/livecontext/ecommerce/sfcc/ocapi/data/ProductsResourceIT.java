package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.MarkupTextDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.VariantDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.ProductsResource;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static org.assertj.core.api.Assertions.assertThat;

public class ProductsResourceIT extends DataApiResourceTestBase {

  @Autowired
  private ProductsResource resource;

  @Test
  public void testGetProductById() {
    if (useBetamaxTapes()) {
      return;
    }

    ProductDocument product = resource.getProductById("25050736", getCurrentStoreContext())
            .orElse(null);

    assertProduct(product);
    assertThat(product.getCreationDate().getTime()).matches((Predicate<Long>) aLong -> aLong > 0);

    MarkupTextDocument defaultValue = product.getShortDescription().getDefaultValue();

    assertThat(defaultValue.getMarkup())
            .isEqualTo("Love our easy care solid shirting? Then give our stripes a try! This subtle blue stripe is a nice option to have in your closet for workplace wear and it even has a bit of stretch for extra comfort.");

    assertThat(defaultValue.getSource())
            .isEqualTo("Love our easy care solid shirting? Then give our stripes a try! This subtle blue stripe is a nice option to have in your closet for workplace wear and it even has a bit of stretch for extra comfort.");
  }

  @Test
  public void testGetProductVariantById() {
    if (useBetamaxTapes()) {
      return;
    }

    ProductDocument product = resource.getProductById("008884303996", getCurrentStoreContext())
            .orElse(null);

    assertProduct(product);
    assertThat(product.getType().isVariant()).isTrue();
  }

  /**
   * Does not work unfortunately. Multi get is not implemented.
   */
  @Test
  @Ignore("Fetching multiple products by id is not yet supported by OCAPI.")
  public void testGetProductVariantsById() {
    ProductDocument product = resource.getProductById("25050736", getCurrentStoreContext())
            .orElse(null);

    assertThat(product).isNotNull();

    List<VariantDocument> variants = product.getVariants();
    List<String> productIds = new ArrayList<>();
    for (VariantDocument variant : variants) {
      productIds.add(variant.getProductId());
    }
  }
}
