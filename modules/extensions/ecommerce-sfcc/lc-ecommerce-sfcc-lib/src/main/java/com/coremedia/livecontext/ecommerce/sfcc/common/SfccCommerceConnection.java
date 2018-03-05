package com.coremedia.livecontext.ecommerce.sfcc.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceConnection;
import com.coremedia.livecontext.ecommerce.sfcc.configuration.SfccConfigurationProperties;

import javax.annotation.Nonnull;

/**
 * An Salesforce Commerce Cloud specific connection class.
 * Manages the Salesforce Commerce Cloud vendor specific properties.
 */
public class SfccCommerceConnection extends BaseCommerceConnection {

  private static final String SFCC_VENDOR_NAME = "Salesforce";
  private static final String SFCC_VENDOR_URL = "http://www.demandware.com/";

  public SfccCommerceConnection(@Nonnull SfccConfigurationProperties sfccConfigurationProperties) {
    setVendor(SfccCommerceIdProvider.SFCC);
    setVendorName(SFCC_VENDOR_NAME);
    setVendorUrl(SFCC_VENDOR_URL);
    setVendorVersion(sfccConfigurationProperties.getVendorVersion());
  }
}
