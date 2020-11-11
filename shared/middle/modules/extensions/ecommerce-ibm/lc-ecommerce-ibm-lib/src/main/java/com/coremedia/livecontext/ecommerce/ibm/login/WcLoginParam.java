package com.coremedia.livecontext.ecommerce.ibm.login;

/**
 * Parameter model for REST-Login
 * @see LoginServiceImpl
 *
 * @deprecated This class is part of the legacy Blueprint commerce integration and has been deprecated
 * in favour of the Commerce Hub integration.
 */
@Deprecated
public class WcLoginParam {
  private String logonId;
  private String logonPassword;

  public WcLoginParam(String logonId, String logonPassword) {
    this.logonId = logonId;
    this.logonPassword = logonPassword;
  }

  public String getLogonId() {
    return logonId;
  }

  public void setLogonId(String logonId) {
    this.logonId = logonId;
  }

  public String getLogonPassword() {
    return logonPassword;
  }

  public void setLogonPassword(String logonPassword) {
    this.logonPassword = logonPassword;
  }
}
