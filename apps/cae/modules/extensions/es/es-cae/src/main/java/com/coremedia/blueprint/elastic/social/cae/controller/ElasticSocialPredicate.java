package com.coremedia.blueprint.elastic.social.cae.controller;

import com.coremedia.objectserver.view.RenderNode;
import com.coremedia.objectserver.view.dynamic.DynamicIncludePredicate;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import javax.inject.Named;

@Named
@DefaultAnnotation(NonNull.class)
public class ElasticSocialPredicate implements DynamicIncludePredicate {
  @Override
  public boolean test(RenderNode input) {
    Object bean = input.getBean();
    return bean instanceof CommentsResult
            || bean instanceof ReviewsResult
            || bean instanceof ComplaintResult
            || bean instanceof RatingResult
            || bean instanceof ShareResult
            || bean instanceof LikeResult;
  }
}
