package com.coremedia.blueprint.caas.augmentation;

import com.coremedia.blueprint.caas.augmentation.model.CommerceRefFactory;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.common.Vendor;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper.parseCommerceIdOrThrow;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static org.assertj.core.api.Assertions.assertThat;

class CommerceIdUtilsTest {

  @Test
  void testExtendBreadCrumb() {
    var catalogAlias = CatalogAlias.of("aCatalog");
    var commerceId = CommerceIdUtils.buildCommerceId("d", CATEGORY, Vendor.of("vendor"), catalogAlias);
    var commerceRef = CommerceRefFactory.from(commerceId, CatalogId.of("ignored"), "", Locale.US, "", List.of());
    var breadcrumb = CommerceIdUtils.extendBreadcrumb(List.of("a", "b", "c"), Vendor.of("vendor"), commerceRef);
    assertThat(breadcrumb).satisfies(bc -> {
              assertThat(bc).hasSize(4);
              assertThat(bc).first().isEqualTo(parseCommerceIdOrThrow("vendor:///catalog/category/catalog:aCatalog;a"));
              assertThat(bc).last().isEqualTo(parseCommerceIdOrThrow("vendor:///catalog/category/catalog:aCatalog;d"));
            }
    );
  }

}
