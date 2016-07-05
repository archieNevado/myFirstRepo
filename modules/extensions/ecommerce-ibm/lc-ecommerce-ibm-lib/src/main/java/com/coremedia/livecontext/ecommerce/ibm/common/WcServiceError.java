package com.coremedia.livecontext.ecommerce.ibm.common;

import com.coremedia.livecontext.ecommerce.common.CommerceRemoteError;

/**
 * Pojo generated from the json response from erroneous REST calls.
 */
@SuppressWarnings("unused") // values are injected by gson
class WcServiceError implements CommerceRemoteError {

  private String errorCode;
  private String errorKey;
  private String errorMessage;
  private String errorParameters;

  @Override
  public String getErrorCode() {
    return errorCode;
  }

  @Override
  public String getErrorKey() {
    return errorKey;
  }

  @Override
  public String getErrorMessage() {
    return errorMessage;
  }

  @Override
  public String getErrorParameters() {
    return errorParameters;
  }

}
