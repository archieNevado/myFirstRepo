package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.OCAPITestBase;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.MediaFileDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.ProductDocument;
import com.google.common.base.Strings;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Base class for all data API resource tests.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DataApiTestConfiguration.class)
public abstract class DataApiResourceTestBase extends OCAPITestBase {

  void assertProduct(ProductDocument product) {
    assertProduct(product, true);
  }

  void assertProduct(ProductDocument product, boolean validateImages) {
    assertThat(product).isNotNull();

    String id = product.getId();
    assertThat(Strings.isNullOrEmpty(id)).as("Product id is not set.").isFalse();

    if (validateImages) {
      MediaFileDocument image = product.getImage();
      assertThat(image).as("Product does not have an image.").isNotNull();
      assertThat(image.getAbsUrl()).as("Product does not have an image link.").isNotNull();
    }
  }
}
