package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents.CategoryDocument;
import com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.resources.CategoriesResource;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;
import java.util.function.Predicate;

import static com.coremedia.blueprint.lc.test.BetamaxTestHelper.useBetamaxTapes;
import static org.assertj.core.api.Assertions.assertThat;

public class CategoriesResourceIT extends DataApiResourceTestBase {

  @Autowired
  private CategoriesResource resource;

  @Test
  public void testGetRootCategory() {
    if (useBetamaxTapes()) {
      return;
    }

    Optional<CategoryDocument> category = resource.getCategoryById("root", getCurrentStoreContext());

    assertThat(category).isPresent()
            .hasValueSatisfying(c -> {
              assertThat(c.getId()).isEqualTo("root");
              assertThat(c.getCreationDate().getTime()).matches((Predicate<Long>) aLong -> aLong > 0);
            });
  }

  @Test
  public void testGetCategoryById() {
    if (useBetamaxTapes()) {
      return;
    }

    Optional<CategoryDocument> category = resource.getCategoryById("mens-clothing-suits", getCurrentStoreContext());

    assertThat(category).isPresent();
    assertThat(category).hasValueSatisfying(c -> assertThat(c.getId()).isEqualTo("mens-clothing-suits"));
  }
}
