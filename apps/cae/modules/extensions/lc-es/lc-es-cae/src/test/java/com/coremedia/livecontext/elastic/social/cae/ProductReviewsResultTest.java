package com.coremedia.livecontext.elastic.social.cae;


import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.elastic.social.cae.ElasticSocialService;
import com.coremedia.elastic.social.api.ContributionType;
import com.coremedia.elastic.social.api.reviews.Review;
import com.coremedia.elastic.social.api.users.CommunityUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.from;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductReviewsResultTest {

  @Mock
  private CommunityUser user;

  @Mock
  private ElasticSocialService elasticSocialService;

  @Mock
  private ElasticSocialConfiguration elasticSocialConfiguration;

  @Mock
  private Object target;

  @Test
  void testProductReviewsResult() {
    List<Review> resultList = Collections.emptyList();
    when(elasticSocialService.getReviews(target, user)).thenReturn(resultList);

    ProductReviewsResult result = new ProductReviewsResult(target, user, elasticSocialService, true, ContributionType.ANONYMOUS, elasticSocialConfiguration);

    assertThat(result)
            .returns(target, from(ProductReviewsResult::getTarget))
            .returns(user, from(ProductReviewsResult::getUser))
            .returns(resultList, from(ProductReviewsResult::getReviews));
    verify(elasticSocialService).getReviews(target, user);
  }
}
