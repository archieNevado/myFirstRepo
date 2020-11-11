package com.coremedia.livecontext.ecommerce.ibm.login;

/**
 * Provides information to make authenticated REST API calls.
 *
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 */
@Deprecated
public interface WcCredentials {
  String getStoreId();
  WcSession getSession();
}
