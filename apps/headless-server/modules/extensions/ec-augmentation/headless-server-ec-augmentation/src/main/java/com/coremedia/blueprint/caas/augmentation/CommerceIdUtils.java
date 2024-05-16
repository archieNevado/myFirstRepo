package com.coremedia.blueprint.caas.augmentation;

import com.coremedia.blueprint.caas.augmentation.model.CommerceRef;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.Vendor;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider.commerceId;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static java.lang.invoke.MethodHandles.lookup;

@DefaultAnnotation(NonNull.class)
public class CommerceIdUtils {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  private CommerceIdUtils() {
  }

  /**
   * Convert the given breadcrumb (external IDs) to commerce IDs and add the commerceRef's external ID
   * as last element to the breadcrumb if it is a category.
   *
   * @param breadcrumb list of external ids
   * @param vendor     commerce vendor for the current site
   * @return the extended breadcrumb of commerce IDs
   */
  public static List<CommerceId> extendBreadcrumb(List<String> breadcrumb, Vendor vendor, CommerceRef commerceRef) {
    if (breadcrumb.isEmpty()) {
      return List.of();
    }
    String categoryLeafId = null;
    if (BaseCommerceBeanType.CATEGORY.equals(commerceRef.getType())) {
      categoryLeafId = commerceRef.getExternalId();
    }
    List<String> extendedBreadcrumb = new ArrayList<>(breadcrumb);
    //add categoryLeafId to breadcrumb, if not already part of the breadcrumb
    if (categoryLeafId != null && !breadcrumb.get(breadcrumb.size() - 1).equals(categoryLeafId)) {
      extendedBreadcrumb.add(categoryLeafId);
      LOG.debug("Automatically extended breadcrumb parameter with {}.", categoryLeafId);
    }

    var catalogAlias = commerceRef.getCatalogAlias();
    return extendedBreadcrumb.stream()
            .map(id -> buildCommerceId(id, CATEGORY, vendor, catalogAlias))
            .collect(Collectors.toList());
  }

  public static CommerceId buildCommerceId(String string, CommerceBeanType type, Vendor vendor, CatalogAlias alias) {
    return commerceId(vendor, type).withExternalId(string).withCatalogAlias(alias).build();
  }
}
