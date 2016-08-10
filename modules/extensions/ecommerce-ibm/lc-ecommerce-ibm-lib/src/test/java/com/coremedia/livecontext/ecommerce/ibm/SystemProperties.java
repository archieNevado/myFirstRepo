package com.coremedia.livecontext.ecommerce.ibm;

public class SystemProperties {

  private SystemProperties() {
  }

  public static Object getBetamaxIgnoreHosts() {
    return System.getProperties().get("betamax.ignoreHosts");
  }
}
