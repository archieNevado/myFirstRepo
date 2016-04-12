package com.coremedia.blueprint.assets.contentbeans;

import com.coremedia.cap.common.CapBlobRef;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents a single rendition defined in an {@link AMAsset} bean.
 */
public interface AMAssetRendition {

  /**
   * The asset the rendition is stored in.
   *
   * @return the asset the rendition is stored in.
   */
  @Nonnull
  AMAsset getAsset();

  /**
   * The name of the rendition which is also the name of the document type property which
   * holds the rendition's blob.
   * @return  name of the rendition which is also the name of the document type property
   */
  @Nonnull
  String getName();

  /**
   * Returns the size of the rendition's blob in bytes
   * @return the size of the rendition's blob in bytes
   */
  int getSize();

  /**
   * Returns the type (Mime Subtype) of the rendition's blob or null
   * @return the type (Mime Subtype) of the rendition's blob or null
   */
  @Nullable
  String getMimeType();

  /**
   * Returns the rendition's blob or null
   * @return the rendition's blob or null
   */
  @Nullable
  CapBlobRef getBlob();

  /**
   * Check if the rendition is published i.e. the rendition should be displayed in the download portal
   * @return <code>true</code> if the rendition is published otherwise <code>false</code>
   */
  boolean isPublished();
}
