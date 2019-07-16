package com.coremedia.livecontext.ecommerce.sfcc.ocapi.data.documents;

import com.coremedia.livecontext.ecommerce.sfcc.ocapi.AbstractOCDocument;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Document representing a code version.
 */
public class CodeVersionDocument extends AbstractOCDocument {

  /**
   * The code version activation time.
   */
  @JsonProperty("activation_time")
  private String/*DateTime*/ activationTime;

  /**
   * Use this method to determine, if this code version is currently active.
   */
  @JsonProperty("active")
  private boolean active;

  /**
   * A list containing the names of all cartridges participating in this code version.
   */
  @JsonProperty("cartridges")
  private List<String> cartridges;

  /**
   * The code version compatibility mode.
   */
  @JsonProperty("compatibility_mode")
  private String compatibilityMode;

  /**
   * The code version creation time.
   */
  @JsonProperty("creation_time")
  private String/*DateTime*/ creationTime;

  /**
   * The last time, when the code version was changed.
   */
  @JsonProperty("last_modification_time")
  private String/*DateTime*/ lastModificationTime;

  /**
   * Use this method to determine, if this code version is the current rollback version.
   */
  @JsonProperty("rollback")
  private boolean rollback;

  /**
   * Returns the total size of the file system content of this code version in bytes.
   */
  @JsonProperty("total_size")
  private long totalSize;

  /**
   * Returns the HTTPS based WebDAV URL that can be used to access the code version resources.
   */
  @JsonProperty("web_dav_url")
  private String webDavUrl;

  public String getActivationTime() {
    return activationTime;
  }

  public void setActivationTime(String activationTime) {
    this.activationTime = activationTime;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public List<String> getCartridges() {
    return cartridges;
  }

  public void setCartridges(List<String> cartridges) {
    this.cartridges = cartridges;
  }

  public String getCompatibilityMode() {
    return compatibilityMode;
  }

  public void setCompatibilityMode(String compatibilityMode) {
    this.compatibilityMode = compatibilityMode;
  }

  public String getCreationTime() {
    return creationTime;
  }

  public void setCreationTime(String creationTime) {
    this.creationTime = creationTime;
  }

  public String getLastModificationTime() {
    return lastModificationTime;
  }

  public void setLastModificationTime(String lastModificationTime) {
    this.lastModificationTime = lastModificationTime;
  }

  public boolean isRollback() {
    return rollback;
  }

  public void setRollback(boolean rollback) {
    this.rollback = rollback;
  }

  public long getTotalSize() {
    return totalSize;
  }

  public void setTotalSize(long totalSize) {
    this.totalSize = totalSize;
  }

  public String getWebDavUrl() {
    return webDavUrl;
  }

  public void setWebDavUrl(String webDavUrl) {
    this.webDavUrl = webDavUrl;
  }
}
