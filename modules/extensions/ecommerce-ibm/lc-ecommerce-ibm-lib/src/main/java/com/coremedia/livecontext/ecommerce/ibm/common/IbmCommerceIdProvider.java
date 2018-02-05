package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdBuilder;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.Vendor;

import javax.annotation.Nonnull;

public class IbmCommerceIdProvider extends BaseCommerceIdProvider {

  static final Vendor IBM = Vendor.of("ibm");

  public IbmCommerceIdProvider() {
    super(IBM);
  }

  @Nonnull
  public static CommerceIdBuilder commerceId(@Nonnull CommerceBeanType beanType) {
    return BaseCommerceIdProvider.commerceId(IBM, beanType);
  }
}
