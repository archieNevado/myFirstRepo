package com.coremedia.blueprint.uitesting.elastic.service.util.settings;

import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialSettings;

import java.util.Map;

/**
 * <p>
 * Interface for a fluent elastic social settings builder.
 * </p>
 *
 * @since 2014-06-30
 */
public interface SettingsBuilder {

  /**
   * see {@link ElasticSocialSettings#isEnabled()}
   *
   * @param enabled
   * @return self-reference
   */
  SettingsBuilder enabled(Boolean enabled);

  /**
   * see {@link ElasticSocialSettings#getCommentType()}
   *
   * @param commentType
   * @return self-reference
   */
  SettingsBuilder commentType(String commentType);

  /**
   * see {@link ElasticSocialSettings#getReviewType()}
   *
   * @param reviewType
   * @return self-reference
   */
  SettingsBuilder reviewType(String reviewType);

  /**
   * <p>
   * Actually create the elastic social settings.
   * </p>
   *
   * @return created settings
   */
  Map<String, Object> build();
}
