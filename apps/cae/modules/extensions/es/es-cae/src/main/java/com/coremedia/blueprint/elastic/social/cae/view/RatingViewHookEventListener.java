package com.coremedia.blueprint.elastic.social.cae.view;

import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.elastic.social.cae.controller.RatingResult;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import javax.inject.Named;
import java.util.List;

/**
 * A {@link com.coremedia.objectserver.view.events.ViewHookEventListener} that
 * is responsible for adding the comments widget to rendered views.
 */
@Named
public class RatingViewHookEventListener extends AbstractESViewHookEventListener {

  @Override
  protected boolean isEnabled(@NonNull ElasticSocialConfiguration elasticSocialConfiguration) {
    return elasticSocialConfiguration.isRatingEnabled();
  }

  @Nullable
  @Override
  protected List<String> getWhitelistTypes(@NonNull ElasticSocialConfiguration elasticSocialConfiguration) {
    return elasticSocialConfiguration.getRatingDocumentTypes();
  }

  @Override
  protected RatingResult getContribution(@NonNull Object target) {
    return new RatingResult(target);
  }

  @Override
  public int getOrder() {
    return DEFAULT_ORDER;
  }
}
