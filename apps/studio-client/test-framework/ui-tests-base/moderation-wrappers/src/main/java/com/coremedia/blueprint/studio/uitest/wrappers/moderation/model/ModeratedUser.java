package com.coremedia.blueprint.studio.uitest.wrappers.moderation.model;

import com.coremedia.elastic.core.api.users.User;
import com.coremedia.elastic.core.api.users.UserService;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * <p>
 * A {@link ModeratedItem} which is a user. Retrieve via {@link ModeratedItemFactory}.
 * </p>
 *
 * @since 2013-02-18
 */
@Named
@Scope("prototype")
public class ModeratedUser extends ModeratedItem<User> {
  @Inject
  private UserService userService;

  @Override
  protected User getBeanById(final String id) {
    return userService.getUserById(id);
  }
}
