package com.coremedia.blueprint.studio.uitest.wrappers.moderation.model;

import com.coremedia.elastic.core.api.models.Model;
import com.coremedia.elastic.core.api.tenant.TenantService;
import com.coremedia.elastic.core.api.users.User;
import com.coremedia.elastic.social.api.comments.Comment;
import com.coremedia.uitesting.ui.data.RemoteBeanFactory;
import com.google.common.base.Preconditions;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

/**
 * A factory for creating {@link ModeratedItem} objects. Currently supported: Users and Comments.
 */
@Named
@Singleton
public class ModeratedItemFactory {
  private static final String PATH_PATTERN = "%s/elastic/social/moderation/%s/%s";

  @SuppressWarnings("SpringJavaAutowiringInspection")
  @Inject
  private RemoteBeanFactory remoteBeanFactory;

  @Inject
  private TenantService tenantService;

  /**
   * <p>
   * Get the moderated item for a given model.
   * </p>
   *
   * @param model the model to get the remote bean representation for
   * @param <T>   the type of the model
   * @return bean representation
   */
  public <T extends Model> ModeratedItem<T> get(final T model) {
    Preconditions.checkNotNull(model, "Model must not be null.");
    final Class<? extends ModeratedItem<?>> itemClass;
    if (model instanceof Comment) {
      itemClass = ModeratedComment.class;
    } else if (model instanceof User) {
      itemClass = ModeratedUser.class;
    } else {
      throw new IllegalArgumentException(String.format("Unsupported model type %s", model.getClass()));
    }
    //noinspection unchecked
    return (ModeratedItem<T>) remoteBeanFactory.getRemoteBean(getPath(model), itemClass);
  }

  private String getPath(final Model model) {
    Preconditions.checkNotNull(model, "Model must not be null.");
    final String type = getTypeString(model);
    return String.format(PATH_PATTERN, tenantService.getCurrent(), type, model.getId());
  }

  private String getTypeString(final Model model) {
    final String type;
    if (model instanceof Comment) {
      type = "comment";
    } else if (model instanceof User) {
      type = "user";
    } else {
      throw new IllegalArgumentException(String.format("Unsupported model type %s", model.getClass()));
    }
    return type;
  }

}
