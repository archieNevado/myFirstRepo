package com.coremedia.blueprint.studio.uitest.wrappers.moderation.model;

import com.coremedia.elastic.social.api.comments.Comment;
import com.coremedia.elastic.social.api.comments.CommentService;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * <p>
 * A {@link ModeratedItem} which is a comment. Retrieve via {@link ModeratedItemFactory}.
 * </p>
 *
 * @since 2013-02-18
 */
@Named
@Scope("prototype")
public class ModeratedComment extends ModeratedItem<Comment> {
  @Inject
  private CommentService commentService;

  @Override
  protected Comment getBeanById(final String id) {
    return commentService.getComment(id);
  }
}
