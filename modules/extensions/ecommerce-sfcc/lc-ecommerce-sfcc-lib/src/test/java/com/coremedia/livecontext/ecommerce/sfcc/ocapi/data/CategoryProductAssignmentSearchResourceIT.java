package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.CategoryProductAssignmentSearchResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static org.assertj.core.api.Assertions.assertThat;

public class CategoryProductAssignmentSearchResourceIT extends DataApiResourceTestBase {

  @Autowired
  private CategoryProductAssignmentSearchResource resource;

  @Test
  public void testProductsByCategory() {
    if (useBetamaxTapes()) {
      return;
    }

    List<ProductDocument> products = resource.getProductsByCategory("mens-accessories-ties", getCurrentStoreContext());
    assertThat(products).hasSize(4);

    for (ProductDocument product : products) {
      assertProduct(product, false);  // no images in current API
    }
  }
}
