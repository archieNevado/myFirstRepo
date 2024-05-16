package com.coremedia.blueprint.elastic.social.cae.configuration;

import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@DefaultAnnotation(NonNull.class)
@ConfigurationProperties(prefix = "elastic.social")
public class ElasticSocialCaeConfigurationProperties {

  /**
   * Set to true to keep https after a shop user logged out.
   */
  @Value("${keep.https.after.logout:false}")
  private boolean keepHttpsAfterLogout = false;

  public boolean isKeepHttpsAfterLogout() {
    return keepHttpsAfterLogout;
  }

  public void setKeepHttpsAfterLogout(boolean keepHttpsAfterLogout) {
    this.keepHttpsAfterLogout = keepHttpsAfterLogout;
  }
}
