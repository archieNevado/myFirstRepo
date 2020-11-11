package com.coremedia.livecontext.ecommerce.ibm.common;

/**
 * Pojo generated from the json response from erroneous REST calls.
 *
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 */
@SuppressWarnings("unused") // values are injected by gson
@Deprecated
class WcServiceError {

  private String errorCode;
  private String errorKey;
  private String errorMessage;
  private String errorParameters;

  public String getErrorCode() {
    return errorCode;
  }

  public String getErrorKey() {
    return errorKey;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public String getErrorParameters() {
    return errorParameters;
  }

}
