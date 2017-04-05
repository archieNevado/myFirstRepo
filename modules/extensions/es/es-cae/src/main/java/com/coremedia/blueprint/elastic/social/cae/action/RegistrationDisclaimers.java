package com.coremedia.blueprint.elastic.social.cae.action;

import com.coremedia.blueprint.common.contentbeans.CMArticle;

/**
 * Serves some documents about legal issues concerning Elastic Social.
 *
 * @cm.template.api
 */
public interface RegistrationDisclaimers {
  /**
   * @cm.template.api
   */
  CMArticle getLinkPrivacyPolicy();

  /**
   * @cm.template.api
   */
  CMArticle getLinkTermsOfUse();

  /**
   * @cm.template.api
   */
  CMArticle getLinkTooYoungPolicy();
}
