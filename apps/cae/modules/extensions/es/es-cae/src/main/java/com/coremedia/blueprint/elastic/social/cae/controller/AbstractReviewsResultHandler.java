package com.coremedia.blueprint.elastic.social.cae.controller;


import com.coremedia.blueprint.base.elastic.social.configuration.ElasticSocialConfiguration;
import com.coremedia.blueprint.cae.web.links.NavigationLinkSupport;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.elastic.social.cae.tags.ElasticSocialFunctions;
import com.coremedia.cap.user.User;
import com.coremedia.common.logging.PersonalDataLogger;
import com.coremedia.elastic.social.api.ContributionType;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.reviews.DuplicateReviewException;
import com.coremedia.elastic.social.api.reviews.Review;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.UserVariantHelper;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

import static com.coremedia.common.logging.BaseMarker.UNCLASSIFIED_PERSONAL_DATA;

public abstract class AbstractReviewsResultHandler extends ElasticContentHandler<ReviewsResult> {

  private static final PersonalDataLogger PERSONAL_DATA_LOGGER = new PersonalDataLogger(LOG);

  /* TODO min_length is currently duplicated translations for labels */
  private static final int REVIEW_TEXT_MIN_LENGTH = 5;

  protected abstract ReviewsResult getReviewsResult(Object target, boolean enabled, ContributionType contributionType, ElasticSocialConfiguration elasticSocialConfiguration);

  protected ModelAndView handleCreateReview(String contextId,
                                            String targetId,
                                            String text,
                                            String title,
                                            Integer rating,
                                            HttpServletRequest request) {
    var contributionTarget = getContributionTarget(targetId, request);
    if (contributionTarget == null) {
      return HandlerHelper.notFound();
    }

    var navigation = getNavigation(contextId);
    Object[] beans = getBeansForSettings(contributionTarget, navigation).toArray();
    ElasticSocialConfiguration elasticSocialConfiguration = getElasticSocialConfiguration(beans);

    CommunityUser author = getElasticSocialUserHelper().getCurrentUser();

    var developer = UserVariantHelper.getUser(request);
    HandlerInfo result = new HandlerInfo();
    validateReview(result, author, rating, title, text, navigation, developer, request, beans);

    if (result.isSuccess()) {
      ModerationType moderationType = elasticSocialConfiguration.getReviewModerationType();
      try {
        if (author == null) {
          author = getElasticSocialUserHelper().getAnonymousUser();
        }
        Review newReview = getElasticSocialService().createReview(author, contributionTarget, text, title, rating, moderationType, null, navigation);
        result.setModel(newReview);
        String message;
        if (moderationType.equals(ModerationType.PRE_MODERATION)) {
          message = getMessage(navigation, developer, ContributionMessageKeys.REVIEW_FORM_SUCCESS_PREMODERATION);
        } else {
          message = getMessage(navigation, developer, ContributionMessageKeys.REVIEW_FORM_SUCCESS);
        }
        result.addMessage(SUCCESS_MESSAGE, null, message);
      } catch (DuplicateReviewException e) {  // NOSONAR no need to log a stacktrace for this
        String authorId = e.getAuthor() != null ? e.getAuthor().toIdString() : "";
        PERSONAL_DATA_LOGGER.info("Could not write a review, the author {} has already written a review for the target {}", authorId, e.getTarget());
        addErrorMessage(result, null, navigation, developer, ContributionMessageKeys.REVIEW_FORM_ALREADY_REVIEWED);
      } catch (Exception e) {
        PERSONAL_DATA_LOGGER.error(UNCLASSIFIED_PERSONAL_DATA, "Could not write a review", e);
        addErrorMessage(result, null, navigation, developer, ContributionMessageKeys.REVIEW_FORM_ERROR);
      }
    }
    return HandlerHelper.createModel(result);
  }

  protected ModelAndView handleGetReviews(String contextId, String targetId, String view, HttpServletRequest request) {
    Object contributionTarget = getContributionTarget(targetId, request);
    if (contributionTarget == null) {
      return HandlerHelper.notFound();
    }

    var navigation = getNavigation(contextId);

    Object[] beans = getBeansForSettings(contributionTarget, navigation).toArray();

    // if elastic social plugin is disabled, go no further
    ElasticSocialConfiguration elasticSocialConfiguration = getElasticSocialConfiguration(beans);
    if (!elasticSocialConfiguration.isFeedbackEnabled()) {
      return null;
    }

    final ReviewsResult reviewsResult = getReviewsResult(contributionTarget, elasticSocialConfiguration.isFeedbackEnabled(), elasticSocialConfiguration.getReviewType(), elasticSocialConfiguration);

    ModelAndView modelWithView = HandlerHelper.createModelWithView(reviewsResult, view);
    NavigationLinkSupport.setNavigation(modelWithView, navigation);

    return modelWithView;
  }

  /**
   * @param user The community user that this is all about
   * @param developer A Blueprint developer whose work in progress may be considered by particular features
   */
  private void validateReview(HandlerInfo handlerInfo, CommunityUser user, Integer rating, String title, String text, Navigation navigation, @Nullable User developer, HttpServletRequest request, Object... beans) {
    // user == null was not allowed in previous versions, removed because user filter handling not fix
    ElasticSocialConfiguration elasticSocialConfiguration = getElasticSocialConfiguration(beans);
    // user == null was not allowed in previous versions, removed because user filter handling not fix
    if (!elasticSocialConfiguration.isWritingReviewsEnabled()) {
      addErrorMessage(handlerInfo, null, navigation, developer, ContributionMessageKeys.REVIEW_FORM_ERROR_NOT_ENABLED);
    } else if ((user == null || user.isAnonymous()) && !elasticSocialConfiguration.isAnonymousReviewingEnabled()) {
      addErrorMessage(handlerInfo, null, navigation, developer, ContributionMessageKeys.REVIEW_FORM_NOT_LOGGED_IN);
    }
    // validate Recaptcha
    if (elasticSocialConfiguration.isRecaptchaForReviewRequired() && ElasticSocialFunctions.isAnonymousUser() && !elasticSocialConfiguration.validateCaptcha(request)) {
      addErrorMessage(handlerInfo, "recaptcha", navigation, developer, ContributionMessageKeys.INVALID_CAPTCHA);
    }

    if (rating == null) {
      addErrorMessage(handlerInfo, "rating", navigation, developer, ContributionMessageKeys.REVIEW_FORM_ERROR_RATING_BLANK);
    }

    if (StringUtils.isBlank(title)) {
      addErrorMessage(handlerInfo, "title", navigation, developer, ContributionMessageKeys.REVIEW_FORM_ERROR_TITLE_BLANK);
    }

    if (StringUtils.isBlank(text)) {
      addErrorMessage(handlerInfo, "text", navigation, developer, ContributionMessageKeys.REVIEW_FORM_ERROR_TEXT_BLANK);
    } else if (text.length() < REVIEW_TEXT_MIN_LENGTH) {
      addErrorMessage(handlerInfo, "text", navigation, developer, ContributionMessageKeys.REVIEW_FORM_ERROR_TEXT_TOO_SHORT);
    }
  }

}
