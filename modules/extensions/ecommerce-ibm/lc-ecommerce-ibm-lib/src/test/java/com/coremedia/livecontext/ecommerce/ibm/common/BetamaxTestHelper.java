package com.coremedia.livecontext.ecommerce.ibm.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class BetamaxTestHelper {

  public static Properties updateSystemPropertiesWithBetamaxConfig() {
    Properties properties = new Properties();

    //read from betamax.properties
    InputStream input = AbstractServiceTest.class.getResourceAsStream("/betamax.properties");
    if (input != null) {
      try {
        properties.load(input);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    //merge properties
    for (Map.Entry<Object, Object> entry : properties.entrySet()) {
      System.setProperty((String) entry.getKey(), (String) entry.getValue());
    }
    return System.getProperties();
  }
}
