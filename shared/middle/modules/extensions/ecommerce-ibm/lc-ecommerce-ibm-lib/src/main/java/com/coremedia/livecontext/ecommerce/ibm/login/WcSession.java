package com.coremedia.livecontext.ecommerce.ibm.login;

/**
 * The user credentials for the WCS.
 *
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 */
@Deprecated
public class WcSession {
  public final static String HTTP_SESSION_VARIABLE_NAME = "wcsSession";

    private String WCToken;
    private String WCTrustedToken;
    private String personalizationID;
    private String userId;

    public String getWCToken() {
      return WCToken;
    }

    public void setWCToken(String WCToken) {
      this.WCToken = WCToken;
    }

    public String getWCTrustedToken() {
      return WCTrustedToken;
    }

    public void setWCTrustedToken(String WCTrustedToken) {
      this.WCTrustedToken = WCTrustedToken;
    }

    public String getPersonalizationID() {
      return personalizationID;
    }

    public void setPersonalizationID(String personalizationID) {
      this.personalizationID = personalizationID;
    }

  public String getUserId() {
    return userId;
  }

  public void setUserId(String userId) {
    this.userId = userId;
  }
}
