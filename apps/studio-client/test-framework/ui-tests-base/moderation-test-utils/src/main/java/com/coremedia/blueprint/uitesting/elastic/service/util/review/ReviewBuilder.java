package com.coremedia.blueprint.uitesting.elastic.service.util.review;

import com.coremedia.cap.multisite.Site;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.reviews.Review;
import com.coremedia.elastic.social.api.users.CommunityUser;

import java.util.List;

/**
 * <p>
 * Interface for a fluent review builder.
 * </p>
 *
 * @since 2014-06-30
 */
public interface ReviewBuilder {
  ModerationType DEFAULT_MODERATION_TYPE = ModerationType.PRE_MODERATION;

  /**
   * <p>
   * Set the rating of a review.
   * </p>
   *
   * @param rating rating of the review;
   * @return self-reference
   */
  ReviewBuilder rating(Integer rating);

  /**
   * <p>
   * Set the title of a review. The title will be added unparsed and might contain
   * BB-Code and alike.
   * </p>
   *
   * @param title title of the review; {@code null} for empty title which is also the default
   * @return self-reference
   */
  ReviewBuilder title(String title);

  /**
   * <p>
   * Set the text of a review. The text will be added unparsed and might contain
   * BB-Code and alike.
   * </p>
   *
   * @param text text of the review; {@code null} for empty text which is also the default
   * @return self-reference
   */
  ReviewBuilder text(String text);

  /**
   * <p>
   *   The site where the target will be reviewed
   * </p>
   *
   * @param site the site which must be used for the target.
   * @return self-reference
   */
  ReviewBuilder forSite(Site site);

    /**
     * <p>
     * Set the target of the review. Typically you add a Content-instance or a
     * ContentBean.
     * </p>
     *
     * @param target review target; {@code null} for none which is also the default
     * @return self-reference
     */
  ReviewBuilder target(Object target);

  /**
   * <p>
   * Set the moderation type of the review. Defaults to {@link #DEFAULT_MODERATION_TYPE}.
   * </p>
   *
   * @param moderationType moderation type; {@code null} for no moderation type (ModerationType.NONE),
   *                       which will actually just create an approved review
   * @return self-reference
   */
  ReviewBuilder moderationType(ModerationType moderationType);

  /**
   * <p>
   * Set the user for the review.
   * </p>
   *
   * @param communityUser user to set; {@code null} for an anonymous, unnamed user which is also the default
   * @return self-reference
   */
  ReviewBuilder user(CommunityUser communityUser);

  /**
   * <p>
   * Set the review this review is a reply to. (not used but technically possible)
   * </p>
   *
   * @param review review to reply to; {@code null} for none (default)
   * @return self-reference
   */
  ReviewBuilder replyTo(Review review);

  /**
   * <p>
   * Set the categories of the review as a list of Strings
   * </p>
   *
   * @param categories the categories; {@code null} for none which is also the default
   * @return self-reference
   */
  ReviewBuilder categories(List<String> categories);

  /**
   * <p>
   * Actually create and save the review. Implementations should register created reviews for later clean-up.
   * </p>
   *
   * @return created review
   */
  Review build();
}
