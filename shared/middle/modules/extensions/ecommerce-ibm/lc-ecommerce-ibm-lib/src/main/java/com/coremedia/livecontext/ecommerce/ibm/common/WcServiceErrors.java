package com.coremedia.livecontext.ecommerce.ibm.common;

import java.util.List;

/**
 * Pojo generated from the json response from erroneous REST calls.
 *
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 */
@SuppressWarnings("unused") // values are injected by gson
@Deprecated
class WcServiceErrors {

  private List<WcServiceError> errors;

  List<WcServiceError> getErrors() {
    return errors;
  }

}
