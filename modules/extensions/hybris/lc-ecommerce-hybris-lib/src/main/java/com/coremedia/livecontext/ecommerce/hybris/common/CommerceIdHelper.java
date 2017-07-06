package com.coremedia.livecontext.ecommerce.hybris.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdHelper;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CommerceIdHelper {

  private static final String HYBRIS_VENDOR_PREFIX = "hybris";
  private static final String CATALOG_PREFIX = HYBRIS_VENDOR_PREFIX + ":///catalog/";
  private static final String PRODUCT_TYPE = "product";
  private static final String PRODUCT_VARIANT_TYPE = "sku";
  private static final String CATEGORY_TYPE = "category";
  private static final String PRODUCT_ID_PREFIX = CATALOG_PREFIX + PRODUCT_TYPE + "/";
  private static final String PRODUCT_VARIANT_ID_PREFIX = CATALOG_PREFIX + PRODUCT_VARIANT_TYPE + "/";
  private static final String CATEGORY_ID_PREFIX = CATALOG_PREFIX + CATEGORY_TYPE + "/";
  public static final String SEGMENT_TYPE = "segment";
  private static final String SEGMENT_ID_PREFIX = CATALOG_PREFIX + SEGMENT_TYPE + "/";

  public final static String ROOT_CATEGORY_ID = "ROOT";

  private static BaseCommerceIdProvider INSTANCE = new BaseCommerceIdProvider();

  static {
    INSTANCE.setVendor(HYBRIS_VENDOR_PREFIX);
  }

  private CommerceIdHelper() {
  }

  @Nullable
  public static String formatSegmentId(@Nullable String uniqueId) {
    if (uniqueId != null && !uniqueId.startsWith(SEGMENT_ID_PREFIX)) {
      return SEGMENT_ID_PREFIX + uniqueId;
    }

    return uniqueId;
  }

  public static String formatCategoryId(String externalId) {
    return convertToInternalId(externalId, Category.class);
  }

  public static String formatProductId(String externalId) {
    return convertToInternalId(externalId, Product.class);
  }

  public static String formatProductVariantId(String externalId) {
    return convertToInternalId(externalId, ProductVariant.class);
  }

  public static String convertToInternalId(String externalId, @Nonnull Class typeOf) {
    if (ProductVariant.class.isAssignableFrom(typeOf)) {
      return INSTANCE.formatProductVariantId(externalId);
    }

    if (Product.class.isAssignableFrom(typeOf)) {
      return INSTANCE.formatProductId(externalId);
    }

    if (Category.class.isAssignableFrom(typeOf)) {
      return INSTANCE.formatCategoryId(externalId);
    }

    if (Segment.class.isAssignableFrom(typeOf)) {
      return INSTANCE.formatSegmentId(externalId);
    }

    throw new InvalidIdException("Unsupported bean type");
  }

  @Nullable
  public static String convertToExternalId(@Nullable String internalId) {
    if (internalId == null) {
      return null;
    }

    String parsedId = isInternalId(internalId) ? INSTANCE.parseExternalIdFromId(internalId) : internalId;

    // common id handling generates an ID starting with "techId:" . Has to be cut off for Hybris.
    if (StringUtils.startsWith(parsedId, BaseCommerceIdHelper.TECH_ID_PREFIX)) {
      int prefixLength = BaseCommerceIdHelper.TECH_ID_PREFIX.length();
      parsedId = parsedId.substring(prefixLength);
    }

    return parsedId;
  }

  public static boolean isSkuId(@Nullable String id) {
    return id != null && id.startsWith(PRODUCT_VARIANT_ID_PREFIX);
  }

  public static boolean isProductId(@Nullable String id) {
    return id != null && id.startsWith(PRODUCT_ID_PREFIX);
  }

  public static boolean isProductVariantId(@Nullable String id) {
    return id != null && id.startsWith(PRODUCT_VARIANT_ID_PREFIX);
  }

  public static boolean isInternalId(@Nullable String id) {
    return id != null && id.startsWith(HYBRIS_VENDOR_PREFIX);
  }

  public static boolean isCategoryId(@Nullable String id) {
    return id != null && id.startsWith(CATEGORY_ID_PREFIX);
  }

  public static boolean isSegmentId(@Nullable String objectId) {
    return objectId != null && objectId.startsWith(SEGMENT_ID_PREFIX);
  }

  public static boolean isRootCategoryId(@Nullable String id) {
    return id != null && id.endsWith("/" + ROOT_CATEGORY_ID);
  }
}
