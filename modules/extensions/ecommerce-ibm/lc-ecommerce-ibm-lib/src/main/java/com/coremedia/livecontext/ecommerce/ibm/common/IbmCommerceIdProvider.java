package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdBuilder;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;

import javax.annotation.Nonnull;

public class IbmCommerceIdProvider extends BaseCommerceIdProvider {

  public static final String VENDOR = "ibm";

  public IbmCommerceIdProvider() {
    super(VENDOR);
  }

  @Nonnull
  public static CommerceIdBuilder commerceId(@Nonnull CommerceBeanType beanType) {
    return BaseCommerceIdProvider.commerceId(VENDOR, beanType);
  }
}
