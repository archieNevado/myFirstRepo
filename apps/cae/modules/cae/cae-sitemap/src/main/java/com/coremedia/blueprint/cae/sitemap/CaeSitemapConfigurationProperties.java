package com.coremedia.blueprint.cae.sitemap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cae.sitemap")
public class CaeSitemapConfigurationProperties {

  /**
   * Set the root dir where the sitemap controller writes its files.
   */
  @Value("${user.home}/cms/sitemap")
  private String targetRoot = "/sitemap";

  /**
   * Set the delivery cae port. Defaults to 'management.server.port'.
   */
  @Value("${management.server.port:8081}")
  private int caePort = 8081;

  /**
   * Initial time of day to start sitemap generation.
   * <p>
   * Supported formats:
   * "23:45": time of day, 24h, timezone of the host (recommended for production)
   * "+10": minutes after CAE start (useful for testsystems)
   * "-": disable periodic sitemap generation (useful for development)
   * <p>
   * Defaults to "-", i.e. disabled.
   */
  private String starttime = "-";

  /**
   * Define the period in which the sitemap generation job is triggered.
   */
  private long periodMinutes = 1440;

  /**
   * Define the protocol of the sitemap file URL.
   */
  private String protocol = "https";

  public long getPeriodMinutes() {
    return periodMinutes;
  }

  public void setPeriodMinutes(long periodMinutes) {
    this.periodMinutes = periodMinutes;
  }

  public String getStarttime() {
    return starttime;
  }

  public void setStarttime(String starttime) {
    this.starttime = starttime;
  }

  public int getCaePort() {
    return caePort;
  }

  public void setCaePort(int caePort) {
    this.caePort = caePort;
  }

  public String getTargetRoot() {
    return targetRoot;
  }

  public void setTargetRoot(String targetRoot) {
    this.targetRoot = targetRoot;
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }
}
