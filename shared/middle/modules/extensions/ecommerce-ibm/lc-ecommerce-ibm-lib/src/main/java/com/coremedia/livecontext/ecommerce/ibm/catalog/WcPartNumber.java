package com.coremedia.livecontext.ecommerce.ibm.catalog;

/**
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 */
@Deprecated
public class WcPartNumber {
  String partNumber;

  public WcPartNumber(String partNumber) {
    this.partNumber = partNumber;
  }

  public String getPartNumber() {
    return partNumber;
  }

  public void setPartNumber(String partNumber) {
    this.partNumber = partNumber;
  }
}
