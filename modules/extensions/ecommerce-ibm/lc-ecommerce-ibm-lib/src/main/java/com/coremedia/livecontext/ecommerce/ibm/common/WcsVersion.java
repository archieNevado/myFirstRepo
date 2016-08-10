package com.coremedia.livecontext.ecommerce.ibm.common;

import com.google.common.base.Optional;

import javax.annotation.Nonnull;

/**
 * Enumeration type for WCS versions.
 */
public enum WcsVersion {

  WCS_VERSION_UNKNOWN(0.0F),

  WCS_VERSION_7_6(7.6F),

  WCS_VERSION_7_7(7.7F),

  WCS_VERSION_7_8(7.8F),

  WCS_VERSION_8_0(8.0F);

  private final float version;

  WcsVersion(float version) {
    this.version = version;
  }

  /**
   * Lookup the enum instance for the given version String.
   *
   * @param wcsVersion a version String, e.g., 7.6
   * @return the optional wcversion enum instance
   */
  public static Optional<WcsVersion> fromVersionString(@Nonnull String wcsVersion) {
    try {
      Float value = Float.parseFloat(wcsVersion);
      for (WcsVersion version : WcsVersion.values()) {
        if (value.compareTo(version.version) == 0) {
          return Optional.of(version);
        }
      }
    } catch (NumberFormatException ignored) {
    }
    return Optional.absent();
  }

  @Nonnull
  public String toVersionString() {
    return Float.toString(version);
  }

  /**
   * Test whether or not this WCS version is less than the given WCS version
   *
   * @param wcsVersion another WCS version
   * @return true if this WCS version is less than the given WCS version
   */
  public boolean lessThan(@Nonnull WcsVersion wcsVersion) {
    return ordinal() < wcsVersion.ordinal();
  }
}
