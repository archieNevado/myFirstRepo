package com.coremedia.blueprint.uitesting.elastic.service.util.user;

import com.coremedia.cms.integration.test.util.ByteArrayImageBuilder;
import com.coremedia.elastic.core.api.blobs.Blob;
import com.coremedia.elastic.core.api.blobs.BlobService;
import com.coremedia.elastic.social.api.ModerationType;
import com.coremedia.elastic.social.api.registration.RegistrationService;
import com.coremedia.elastic.social.api.users.CommunityUser;
import com.coremedia.elastic.social.api.users.CommunityUserService;
import com.coremedia.uitesting.elastic.helper.model.ElasticCleanupRegistry;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.ByteArrayInputStream;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import static com.coremedia.cms.integration.test.util.image.Painters.gradient;
import static com.coremedia.uitesting.uapi.helper.XssAssert.getAsXssMessage;
import static com.google.common.collect.Maps.newHashMap;
import static java.lang.String.format;
import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;

/**
 * <p>
 * Default implementation of the {@link UserBuilder}. All users created via this builder
 * will be registered for automatic deletion afterwards.
 * </p>
 *
 * @since 2013-02-19
 */
@Named
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class DefaultUserBuilder implements UserBuilder {
  private static final String IMAGE_CONTENT_TYPE = "image/png";
  private String username;
  private boolean disableXssToken;
  private String password;
  private String givenName;
  private String surName;
  private Locale locale;
  private TimeZone timezone;
  private final Map<String, Object> properties = newHashMap();
  private ModerationType moderationType;
  private Blob profileImage;

  @Inject
  private BlobService blobService;
  @Inject
  private ByteArrayImageBuilder imageBuilder;
  @Inject
  private RegistrationService registrationService;
  @Inject
  private ElasticCleanupRegistry modelRegistry;
  @Inject
  private CommunityUserService communityUserService;
  private boolean replaceExisting;

  @Override
  public UserBuilder username(final String value) {
    return username(value, false);
  }

  @Override
  public UserBuilder username(String value, boolean disableXssToken) {
    username = value;
    this.disableXssToken = disableXssToken;
    return this;
  }

  @Override
  public UserBuilder password(final String value) {
    password = value;
    return this;
  }

  @Override
  public UserBuilder givenName(final String value) {
    givenName = value;
    return this;
  }

  @Override
  public UserBuilder surName(final String value) {
    surName = value;
    return this;
  }

  @Override
  public UserBuilder locale(final Locale value) {
    locale = value;
    return this;
  }

  @Override
  public UserBuilder timezone(final TimeZone value) {
    timezone = value;
    return this;
  }

  @Override
  public UserBuilder property(final String key, final Object value) {
    properties.put(key, value);
    return this;
  }

  @Override
  public UserBuilder moderationType(final ModerationType value) {
    moderationType = value;
    return this;
  }

  @Override
  public UserBuilder profileImage(final Blob value) {
    profileImage = value;
    return this;
  }

  @Override
  public UserBuilder replaceExisting(boolean replaceExisting) {
    this.replaceExisting = replaceExisting;
    return this;
  }

  private UserBuilder profileImage(final byte[] imageByteArray, final String imageContentType) {
    final Blob blob = blobService.put(new ByteArrayInputStream(imageByteArray),
            imageContentType,
            format("%s.png", randomAlphabetic(10)));
    return profileImage(blob);
  }

  @Override
  public UserBuilder profileImageByRandom() {
    final byte[] imageByteArray = imageBuilder.mimeType(IMAGE_CONTENT_TYPE).painter(gradient()).build();
    return profileImage(imageByteArray, IMAGE_CONTENT_TYPE);
  }

  @Override
  public CommunityUser build(boolean activated) {
    // ES-258: Username must not be null
    String nonNullUsername = ofNullable(username).orElse(randomAlphabetic(10));
    String concreteUsername =
        nonNullUsername + (disableXssToken ? "" : " " + getAsXssMessage("username"));
    String concretePassword = password == null ? concreteUsername : password;
    if (StringUtils.isNotBlank(givenName)) {
      properties.put("givenName", givenName);
    }
    if (StringUtils.isNotBlank(surName)) {
      properties.put("surName", surName);
    }
    final String email = nonNullUsername + '_' + randomAlphabetic(10) + "@example.com";
    if (locale == null) {
      locale = Locale.US;
    }
    if (timezone == null) {
      timezone = TimeZone.getTimeZone("UTC");
    }

    if (replaceExisting) {
      CommunityUser possiblyExistingUser = communityUserService.getUserByName(concreteUsername);
      if (possiblyExistingUser != null) {
        communityUserService.anonymize(possiblyExistingUser);
      }
    }

    final CommunityUser user = registrationService.register(
            concreteUsername,
            concretePassword,
            email,
            locale,
            timezone,
            properties);

    modelRegistry.register(user);

    if (profileImage != null) {
      user.setImage(profileImage);
      user.save();
    }

    if (activated) {
      registrationService.activateRegistration(
              user.getProperty("token", String.class),
          ofNullable(moderationType).orElse(DEFAULT_MODERATION_TYPE));
    }
    return user;
  }

  @Override
  public CommunityUser build() {
    return build(true);
  }
}
