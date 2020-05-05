package com.coremedia.blueprint.uitesting.elastic.service.util.review;

import com.coremedia.blueprint.base.elastic.social.common.ContributionTargetHelper;
import com.coremedia.cap.multisite.Site;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.reviews.Review;
import com.coremedia.elastic.social.api.reviews.ReviewService;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.uitesting.cap.builder.content.CobContentBuilder;
import com.coremedia.uitesting.doctypes.CMArticle;
import com.coremedia.uitesting.elastic.helper.model.ElasticCleanupRegistry;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import edu.umd.cs.findbugs.annotations.NonNull;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/**
 * <p>
 * Default implementation of the {@link com.coremedia.blueprint.uitesting.elastic.service.util.review.ReviewBuilder}. All reviews created via this builder
 * will be registered for automatic deletion afterwards.
 * </p>
 *
 * @since 2014-06-30
 */
@Named
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
final class DefaultReviewBuilder implements ReviewBuilder {

  /**
   * The user who creates the review. If unset (thus {@code null}) the author will be <em>anonymous</em>.
   */
  private CommunityUser user;
  private Integer rating;
  private String title;
  private String text;
  private Object target;
  private Collection<String> categories = Collections.emptyList();
  private Review replyTo;
  private ModerationType moderationType = DEFAULT_MODERATION_TYPE;
  private Site site;

  @Inject
  private ReviewService reviewService;

  @Inject
  private CommunityUserService userService;

  @Inject
  private ElasticCleanupRegistry modelRegistry;

  @Inject
  private ContributionTargetHelper contributionTargetHelper;

  @Inject
  private Provider<CobContentBuilder> contentBuilderProvider;

  @Override
  public ReviewBuilder rating(final Integer rating) {
    this.rating = rating;
    return this;
  }

  @Override
  public ReviewBuilder title(final String title) {
    this.title = title;
    return this;
  }

  @Override
  public ReviewBuilder text(final String text) {
    this.text = text;
    return this;
  }

  @Override
  public ReviewBuilder forSite(Site site) {
    this.site = site;
    return this;
  }

  @Override
  public ReviewBuilder user(final CommunityUser communityUser) {
    this.user = communityUser;
    return this;
  }

  @Override
  public ReviewBuilder replyTo(final Review review) {
    this.replyTo = review;
    return this;
  }

  @Override
  public ReviewBuilder categories(List<String> categories) {
    this.categories = categories;
    return this;
  }

  @Override
  public ReviewBuilder moderationType(final ModerationType moderationType) {
    this.moderationType = moderationType;
    return this;
  }

  @Override
  public ReviewBuilder target(final Object target) {
    this.target = target;
    return this;
  }

  @NonNull
  private Object getTarget() {
    if (target != null) {
      return contributionTargetHelper.getTarget(target);
    }

    if (site == null) {
      throw new IllegalStateException("No site has been set to create a comment for.");
    }

    final CobContentBuilder contentBuilder = contentBuilderProvider.get();
    target = contentBuilder
            .named("ContentWithSite_")
            .parent(site.getSiteRootFolder())
            .contentType(CMArticle.NAME)
            .build();
    return contributionTargetHelper.getTarget(target);
  }

  @Override
  public Review build() {
    if (user == null) {
      user = userService.createAnonymousUser();
      persist(user);
    }
    final Review review = reviewService.createReview(user, text, getTarget(), categories, title, rating != null ? rating : 1);
    review.setReplyTo(replyTo);
    reviewService.save(review, moderationType);
    modelRegistry.register(review);
    return review;
  }

  private void persist(CommunityUser user) {
    // persist anonymous user so that he can be found later on, e.g., by a CAE rendering a comment
    user.save();
    modelRegistry.register(user);
  }
}
