package com.coremedia.livecontext.ecommerce.hybris;

public class SystemProperties {

  private SystemProperties() {
  }

  public static Object getBetamaxIgnoreHosts() {
    return System.getProperties().get("betamax.ignoreHosts");
  }
}
