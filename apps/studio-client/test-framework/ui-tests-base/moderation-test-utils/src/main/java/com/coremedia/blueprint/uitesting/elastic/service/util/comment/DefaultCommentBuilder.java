package com.coremedia.blueprint.uitesting.elastic.service.util.comment;

import com.coremedia.blueprint.base.elastic.social.common.ContributionTargetHelper;
import com.coremedia.cap.multisite.Site;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.comments.Comment;
import com.coremedia.elastic.social.api.comments.CommentService;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.uitesting.cap.builder.content.CobContentBuilder;
import com.coremedia.uitesting.elastic.helper.model.ElasticCleanupRegistry;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * <p>
 * Default implementation of the {@link CommentBuilder}. All comments created via this builder
 * will be registered for automatic deletion afterwards.
 * </p>
 *
 * @since 2013-02-06
 */
@Named
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
final class DefaultCommentBuilder implements CommentBuilder {

  /**
   * The user who creates the comment. If unset (thus {@code null}) the author will be <em>anonymous</em>.
   */
  private CommunityUser user;
  private String text;
  private Object target;
  private Collection<String> categories = Collections.emptyList();
  private Comment replyTo;
  private ModerationType moderationType = DEFAULT_MODERATION_TYPE;
  private Site site;

  @Inject
  private CommentService commentService;
  @Inject
  private CommunityUserService userService;
  @Inject
  private ElasticCleanupRegistry modelRegistry;
  @Inject
  private Provider<CobContentBuilder> contentBuilderProvider;
  @Inject
  private ContributionTargetHelper contributionTargetHelper;

  @Override
  public CommentBuilder text(final String text) {
    this.text = text;
    return this;
  }

  @Override
  public CommentBuilder forSite(Site site) {
    this.site = site;
    return this;
  }

  @Override
  public CommentBuilder user(final CommunityUser communityUser) {
    this.user = communityUser;
    return this;
  }

  @Override
  public CommentBuilder replyTo(final Comment comment) {
    this.replyTo = comment;
    return this;
  }

  @Override
  public CommentBuilder categories(List<String> categories) {
    this.categories = categories;
    return this;
  }

  @Override
  public CommentBuilder moderationType(final ModerationType moderationType) {
    this.moderationType = moderationType;
    return this;
  }

  @Override
  public CommentBuilder target(final Object target) {
    this.target = target;
    return this;
  }

  @NonNull
  private Object getTarget() {
    if(target != null) {
      return contributionTargetHelper.getTarget(target);
    }

    if (site == null) {
      throw new IllegalStateException("No site has been set to create a comment for.");
    }

    target = contentBuilderProvider.get()
                                   .named("ContentWithSite_")
                                   .parent(site.getSiteRootFolder())
                                   .contentType("CMArticle")
                                   .build();
    return contributionTargetHelper.getTarget(target);
  }

  @Override
  public Comment build() {
    if (user == null) {
      user = userService.createAnonymousUser();
      persist(user);
    }
    final Comment comment = commentService.createComment(user, text, getTarget(), categories, replyTo);
    commentService.save(comment, moderationType);

    modelRegistry.register(comment);
    return comment;
  }

  private void persist(CommunityUser user) {
    // persist anonymous user so that he can be found later on, e.g., by a CAE rendering a comment
    user.save();
    modelRegistry.register(user);
  }

}
