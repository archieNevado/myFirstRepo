package com.coremedia.blueprint.uitesting.elastic.service.util.comment;

import com.coremedia.cap.multisite.Site;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.comments.Comment;
import com.coremedia.elastic.social.api.users.CommunityUser;

import java.util.List;

/**
 * <p>
 * Interface for a fluent comment builder.
 * </p>
 *
 * @since 2013-02-06
 */
public interface CommentBuilder {
  ModerationType DEFAULT_MODERATION_TYPE = ModerationType.PRE_MODERATION;

  /**
   * <p>
   * Set the text of a comment. The text will be added unparsed and might contain
   * BB-Code and alike.
   * </p>
   *
   * @param text text of the comment; {@code null} for empty text which is also the default
   * @return self-reference
   */
  CommentBuilder text(String text);

  /**
   * <p>
   *   The site where the target will be commented
   * </p>
   *
   * @param site the site which must be used for the target.
   * @return self-reference
   */
  CommentBuilder forSite(Site site);

  /**
   * <p>
   * Set the target of the comment. Typically you add a Content-instance or a
   * ContentBean.
   * </p>
   *
   * @param target comment target; {@code null} for none which is also the default
   * @return self-reference
   */
  CommentBuilder target(Object target);

  /**
   * <p>
   * Set the moderation type of the comment. Defaults to {@link #DEFAULT_MODERATION_TYPE}.
   * </p>
   *
   * @param moderationType moderation type; {@code null} for no moderation type (ModerationType.NONE),
   *                       which will actually just create an approved comment
   * @return self-reference
   */
  CommentBuilder moderationType(ModerationType moderationType);

  /**
   * <p>
   * Set the user for the comment.
   * </p>
   *
   * @param communityUser user to set; {@code null} for an anonymous, unnamed user which is also the default
   * @return self-reference
   */
  CommentBuilder user(CommunityUser communityUser);

  /**
   * <p>
   * Set the comment this comment is a reply to.
   * </p>
   *
   * @param comment comment to reply to; {@code null} for none (default)
   * @return self-reference
   */
  CommentBuilder replyTo(Comment comment);

  /**
   * <p>
   * Set the categories of the comment as a list of Strings
   * </p>
   *
   * @param categories the categories; {@code null} for none which is also the default
   * @return self-reference
   */
  CommentBuilder categories(List<String> categories);

  /**
   * <p>
   * Actually create and save the comment. Implementations should register created comments for later clean-up.
   * </p>
   *
   * @return created comment
   */
  Comment build();
}
