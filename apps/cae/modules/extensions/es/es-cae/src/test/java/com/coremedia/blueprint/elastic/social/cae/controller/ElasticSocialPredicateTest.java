package com.coremedia.blueprint.elastic.social.cae.controller;

import com.coremedia.objectserver.view.RenderNode;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class ElasticSocialPredicateTest {

  private final Predicate<RenderNode> predicate = new ElasticSocialPredicate();

  @Test
  void withReviewsResult() {
    ReviewsResult reviewsResult = mock(ReviewsResult.class);
    RenderNode renderNode = new RenderNode(reviewsResult, null);

    assertThat(predicate.test(renderNode)).isTrue();
  }

  @Test
  void withCommentsResult() {
    CommentsResult commentsResult = mock(CommentsResult.class);
    RenderNode renderNode = new RenderNode(commentsResult, null);

    assertThat(predicate.test(renderNode)).isTrue();
  }

  @Test
  void withLikeResult() {
    LikeResult likeResult = mock(LikeResult.class);
    RenderNode renderNode = new RenderNode(likeResult, null);

    assertThat(predicate.test(renderNode)).isTrue();
  }

  @Test
  void withComplaintResult() {
    ComplaintResult complaintResult = mock(ComplaintResult.class);
    RenderNode renderNode = new RenderNode(complaintResult, null);

    assertThat(predicate.test(renderNode)).isTrue();
  }

  @Test
  void withoutReviewsResult() {
    Object target = new Object();
    RenderNode renderNode = new RenderNode(target, null);

    assertThat(predicate.test(renderNode)).isFalse();
  }
}
