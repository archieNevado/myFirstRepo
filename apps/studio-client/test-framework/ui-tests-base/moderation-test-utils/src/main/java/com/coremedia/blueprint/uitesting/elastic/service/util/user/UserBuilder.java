package com.coremedia.blueprint.uitesting.elastic.service.util.user;

import com.coremedia.elastic.core.api.blobs.Blob;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.users.CommunityUser;

import java.util.Locale;
import java.util.TimeZone;

/**
 * <p>
 * Interface for a fluent user builder.
 * </p>
 *
 * @since 2013-02-19
 */
public interface UserBuilder {
  ModerationType DEFAULT_MODERATION_TYPE = ModerationType.PRE_MODERATION;

  /**
   * <p>
   * Set the username.
   * </p>
   *
   * @param value name of the user
   * @return self-reference
   */
  UserBuilder username(String value);

  /**
   * <p>
   * Set the username.
   * </p>
   *
   * @param value name of the user
   * @param disableXssToken if to disable the XSS token for the username or not
   * @return self-reference
   */
  UserBuilder username(String value, boolean disableXssToken);

  /**
   * <p>
   * Set the password. If unset defaults to the username.
   * </p>
   *
   * @param value password of the user
   * @return self-reference
   */
  UserBuilder password(String value);

  /**
   * <p>
   * Set the given name.
   * </p>
   *
   * @param value given name of the user
   * @return self-reference
   */
  UserBuilder givenName(final String value);

  /**
   * <p>
   * Set the surname.
   * </p>
   *
   * @param value surname of the user
   * @return self-reference
   */
  UserBuilder surName(final String value);

  /**
   * <p>
   * Set the user's locale.
   * </p>
   *
   * @param value locale of the user
   * @return self-reference
   */
  UserBuilder locale(Locale value);

  /**
   * <p>
   * Set the user's timezone.
   * </p>
   *
   * @param value timezone of the user
   * @return self-reference
   */
  UserBuilder timezone(TimeZone value);

  /**
   * <p>
   * Set user properties. Any property keys defined before will be overwritten.
   * </p>
   */
  UserBuilder property(String key, Object value);

  /**
   * <p>
   * Set the moderation type of the user. Defaults to {@link #DEFAULT_MODERATION_TYPE}.
   * </p>
   *
   * @param value moderation type; {@code null} for no moderation type (ModerationType.NONE),
   *              which will actually just create an approved comment
   * @return self-reference
   */
  UserBuilder moderationType(ModerationType value);

  /**
   * <p>
   * Sets a profile image.
   * </p>
   *
   * @param value profile image as blob
   * @return self-reference
   */
  UserBuilder profileImage(Blob value);

  /**
   * <p>
   * Sets a random profile image.
   * </p>
   *
   * @return self-reference
   */
  UserBuilder profileImageByRandom();

  /**
   * Control if to replace already existing users (by username). If set to {@code true} a user already existing
   * with this name will be removed prior to creating a new user. If set to {@code false} (which is the default)
   * the user builder will fail when trying to create a duplicate user.
   *
   * @param replaceExisting {@code true} to replace a possibly already existing user; {@code false} otherwise
   * @return self-reference
   */
  UserBuilder replaceExisting(boolean replaceExisting);

  /**
   * <p>
   * Actually create and save the user. Implementations should register created users for later clean-up.
   * </p>
   *
   * @return created user
   */
  CommunityUser build();

  /**
   * <p>
   * Actually create and save the user. Implementations should register created users for later clean-up.
   * </p>
   *
   * @param activated defines if user is activated after registration
   * @return created user
   */
  CommunityUser build(boolean activated);
}
