package com.coremedia.blueprint.common.teaserOverlay;

import com.coremedia.cap.content.Content;

/**
 * Settings for the teaser overlay feature.
 *
 * @cm.template.api
 */
public interface TeaserOverlaySettings {

  /**
   * @cm.template.api
   */
  boolean isEnabled();

  Content getStyle();

  /**
   * @cm.template.api
   */
  int getPositionX();

  /**
   * @cm.template.api
   */
  int getPositionY();

  /**
   * @cm.template.api
   */
  int getWidth();
}
