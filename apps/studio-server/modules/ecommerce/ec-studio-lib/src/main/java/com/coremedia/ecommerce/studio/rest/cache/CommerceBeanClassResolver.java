package com.coremedia.ecommerce.studio.rest.cache;

import com.coremedia.livecontext.ecommerce.catalog.Catalog;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.p13n.MarketingSpot;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATALOG;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.MARKETING_SPOT;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.SEGMENT;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.SKU;

/**
 * Helper class to resolve commerce bean classes from commerce bean types for Studio REST resource linking.
 * <p>
 * Can be replaced with custom bean that provides additional resolution mappings or overwrites defaults mappings.
 */
@DefaultAnnotation(NonNull.class)
public class CommerceBeanClassResolver {

  private static final Map<CommerceBeanType, Class<? extends CommerceBean>> DEFAULT_MAPPINGS = Map.of(
          CATALOG, Catalog.class,
          CATEGORY, Category.class,
          MARKETING_SPOT, MarketingSpot.class,
          PRODUCT, Product.class,
          SKU, ProductVariant.class,
          SEGMENT, Segment.class
  );

  private final Map<CommerceBeanType, Class<? extends CommerceBean>> mappings = new HashMap<>();

  public CommerceBeanClassResolver() {
    this(Map.of());
  }

  public CommerceBeanClassResolver(Map<CommerceBeanType, Class<? extends CommerceBean>> customMappings) {
    mappings.putAll(DEFAULT_MAPPINGS);
    mappings.putAll(customMappings);
  }

  public Optional<Class<? extends CommerceBean>> findByType(String commerceBeanTypeName) {
    return findByType(CommerceBeanType.of(commerceBeanTypeName));
  }

  public Optional<Class<? extends CommerceBean>> findByType(CommerceBeanType commerceBeanType) {
    return Optional.ofNullable(mappings.get(commerceBeanType));
  }
}
