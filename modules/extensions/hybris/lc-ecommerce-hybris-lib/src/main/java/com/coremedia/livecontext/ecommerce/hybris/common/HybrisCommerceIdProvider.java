package com.coremedia.livecontext.ecommerce.hybris.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdBuilder;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;

import javax.annotation.Nonnull;

public class HybrisCommerceIdProvider extends BaseCommerceIdProvider {

  public static final String HYBRIS_VENDOR_PREFIX = "hybris";

  public HybrisCommerceIdProvider() {
    super(HYBRIS_VENDOR_PREFIX);
  }

  @Nonnull
  public static CommerceIdBuilder commerceId(@Nonnull CommerceBeanType beanType) {
    return BaseCommerceIdProvider.commerceId(HYBRIS_VENDOR_PREFIX, beanType);
  }
}
