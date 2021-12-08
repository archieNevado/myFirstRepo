package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentUserContext;
import com.coremedia.blueprint.base.multisite.cae.SiteResolver;
import com.coremedia.blueprint.common.datevalidation.ValidityPeriodValidator;
import com.coremedia.blueprint.ecommerce.cae.AbstractCommerceContextInterceptor;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextBuilder;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.fragment.FragmentContextProvider;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.livecontext.fragment.links.context.Context;
import com.coremedia.livecontext.fragment.links.context.LiveContextContextHelper;
import com.coremedia.livecontext.fragment.links.context.accessors.LiveContextContextAccessor;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.TimeZone;

import static com.coremedia.blueprint.base.links.UriConstants.Links.ABSOLUTE_URI_KEY;

/**
 * Suitable for URLs whose second segment denotes the store, e.g. /fragment/10001/...
 */
public class FragmentCommerceContextInterceptor extends AbstractCommerceContextInterceptor {

  private static final Logger LOG = LoggerFactory.getLogger(FragmentCommerceContextInterceptor.class);

  private static final String REQUEST_PARAM_TIMESTAMP = "timestamp";
  private static final String REQUEST_PARAM_TIMEZONE = "timezone";

  private CatalogAliasTranslationService catalogAliasTranslationService;
  private LiveContextContextAccessor fragmentContextAccessor;
  private LiveContextSiteResolver liveContextSiteResolver;

  // Names of context entries
  private String contextNameMemberGroup = "wc.preview.memberGroups";
  private String contextNameTimestamp = "wc.preview.timestamp";
  private String contextNamePreviewUserGroup = "wc.preview.usergroup";
  private String contextNameTimezone = "wc.preview.timezone";
  private String contextNameUserId = "wc.user.id";
  private String contextNameUserName = "wc.user.loginid";
  private String contextNameUserGroupIds = "wc.user.membergroupids";

  @Override
  public boolean preHandle(@NonNull HttpServletRequest request, HttpServletResponse response, Object handler) {
    setFragmentContext(request);
    return super.preHandle(request, response, handler);
  }

  @Override
  @NonNull
  protected Optional<CommerceConnection> getCommerceConnectionWithConfiguredStoreContext(
          @NonNull Site site, @NonNull HttpServletRequest request) {
    Optional<CommerceConnection> connection = super.getCommerceConnectionWithConfiguredStoreContext(site, request);

    connection.ifPresent(commerceConnection -> updateStoreContext(commerceConnection, request, site.getId()));

    return connection;
  }

  private void updateStoreContext(@NonNull CommerceConnection connection, @NonNull HttpServletRequest request,
                                  @NonNull String siteId) {
    FragmentParameters fragmentParameters = FragmentContextProvider.getFragmentContext(request).getParameters();

    fragmentParameters.getCatalogId().ifPresent(catalogId -> {
      StoreContext originalStoreContext = CurrentStoreContext.find(request).orElseGet(connection::getInitialStoreContext);

      Optional<CatalogAlias> catalogAlias = catalogAliasTranslationService
              .getCatalogAliasForId(catalogId, originalStoreContext);

      StoreContext updatedStoreContext = connection
              .getStoreContextProvider()
              .buildContext(originalStoreContext)
              .withCatalogId(catalogId)
              .withCatalogAlias(catalogAlias.orElse(null))
              .build();

      CurrentStoreContext.set(updatedStoreContext, request);
    });

    if (isPreview()) {
      Optional<Context> fragmentContext = LiveContextContextHelper.findContext(request);
      if (fragmentContext.isPresent()) {
        StoreContextProvider storeContextProvider = connection.getStoreContextProvider();
        StoreContext storeContext = CurrentStoreContext.find(request).orElseGet(connection::getInitialStoreContext);
        StoreContext updatedStoreContext = updateStoreContextForPreview(fragmentContext.get(),
                storeContext, storeContextProvider, request);
        CurrentStoreContext.set(updatedStoreContext, request);
      }
    }
  }

  @Override
  protected void initUserContext(@NonNull CommerceConnection commerceConnection, @NonNull HttpServletRequest request) {
    super.initUserContext(commerceConnection, request);

    UserContext userContext = CurrentUserContext.find(request).orElse(null);
    Context fragmentContext = LiveContextContextHelper.findContext(request).orElse(null);
    if (userContext == null || fragmentContext == null) {
      return;
    }

    userContext = adjustUserContext(userContext, fragmentContext);
    CurrentUserContext.set(userContext, request);

    StoreContext storeContext = CurrentStoreContext.find(request).orElseGet(commerceConnection::getInitialStoreContext);
    StoreContextBuilder storeContextBuilder = commerceConnection.getStoreContextProvider().buildContext(storeContext);

    // Set user segments.
    findStringValue(fragmentContext, contextNameUserGroupIds)
            .ifPresent(storeContextBuilder::withUserSegments);

    StoreContext clonedStoreContext = storeContextBuilder.build();
    CurrentStoreContext.set(clonedStoreContext, request);
  }

  @NonNull
  private UserContext adjustUserContext(@NonNull UserContext userContext, @NonNull Context fragmentContext) {
    UserContext.Builder userContextBuilder = UserContext.buildCopyOf(userContext);

    findStringValue(fragmentContext, contextNameUserId).ifPresent(userContextBuilder::withUserId);
    findStringValue(fragmentContext, contextNameUserName).ifPresent(userContextBuilder::withUserName);

    return userContextBuilder.build();
  }

  @NonNull
  @Override
  protected Optional<Site> findSite(@NonNull HttpServletRequest request, String normalizedPath) {
    FragmentParameters parameters = FragmentContextProvider.getFragmentContext(request).getParameters();
    return Optional.ofNullable(parameters).flatMap(liveContextSiteResolver::findSiteFor);
  }

  @Override
  public SiteResolver getSiteResolver() {
    return liveContextSiteResolver;
  }

  protected void setFragmentContext(@NonNull HttpServletRequest request) {
    // apply the absolute URL flag for fragment requests
    request.setAttribute(ABSOLUTE_URI_KEY, true);
    fragmentContextAccessor.openAccessToContext(request);
  }

  @NonNull
  private StoreContext updateStoreContextForPreview(@NonNull Context fragmentContext,
                                                    @NonNull StoreContext storeContext,
                                                    @NonNull StoreContextProvider storeContextProvider,
                                                    @NonNull HttpServletRequest request) {
    String newUserSegments = null;
    ZonedDateTime newPreviewDate = null;

    // member group user segments
    Optional<String> memberGroupUserSegments = findStringValue(fragmentContext, contextNameMemberGroup);
    if (memberGroupUserSegments.isPresent()) {
      newUserSegments = memberGroupUserSegments.get();
    }

    // preview mode
    if (isStudioPreviewRequest(request)) {
      // preview date
      newPreviewDate = createPreviewDate(fragmentContext).orElse(null);

      if (newPreviewDate == null) {
        String timestampText = request.getParameter(REQUEST_PARAM_TIMESTAMP);
        String timezoneText = request.getParameter(REQUEST_PARAM_TIMEZONE);
        if (timestampText != null && timezoneText != null) {
          ZoneId zoneId = parseTimeZone(timestampText);
          newPreviewDate = parsePreviewDate(timestampText, zoneId).orElse(null);
        }
      }

      // preview user group segments
      Optional<String> previewUserGroupSegments = findStringValue(fragmentContext, contextNamePreviewUserGroup);
      if (previewUserGroupSegments.isPresent()) {
        newUserSegments = previewUserGroupSegments.get();
      }
    }

    // Update store context.
    StoreContextBuilder storeContextBuilder = storeContextProvider.buildContext(storeContext);
    if (newUserSegments != null) {
      storeContextBuilder.withUserSegments(newUserSegments);
    }
    if (newPreviewDate != null) {
      storeContextBuilder.withPreviewDate(newPreviewDate);
    }

    // Update request.
    if (newPreviewDate != null) {
      Calendar calendar = GregorianCalendar.from(newPreviewDate);
      request.setAttribute(ValidityPeriodValidator.REQUEST_ATTRIBUTE_PREVIEW_DATE, calendar);
    }

    return storeContextBuilder.build();
  }

  @VisibleForTesting
  boolean isStudioPreviewRequest(@NonNull HttpServletRequest request) {
    return LiveContextPageHandlerBase.isStudioPreviewRequest(request);
  }

  @NonNull
  @VisibleForTesting
  Optional<ZonedDateTime> createPreviewDate(@NonNull Context fragmentContext) {
    Optional<String> timestampText = findStringValue(fragmentContext, contextNameTimestamp);

    ZoneId zoneId = findStringValue(fragmentContext, contextNameTimezone)
            .map(this::parseTimeZone)
            .orElse(null);

    return timestampText.flatMap(text -> parsePreviewDate(text, zoneId));
  }

  /**
   * Obtain a datetime value with a time zone from values in the fragment context.
   *
   * @param timestampText the value of the {@link #setContextNameTimestamp(String)} attribute from the fragment
   *                      context as the timestamp string to be parsed
   * @param zoneId        the value of the {@link #setContextNameTimezone(String)} attribute from the fragment context
   *                      as the time zone string already parsed into a time zone ID
   * @return The time represented by the timestamp and time zone, or nothing if the timestamp cannot be parsed
   */
  @NonNull
  protected Optional<ZonedDateTime> parsePreviewDate(@NonNull String timestampText, @Nullable ZoneId zoneId) {
    ZoneId nonNullZoneId = zoneId != null ? zoneId : ZoneId.systemDefault();

    return parsePreviewTimestamp(timestampText)
            .map(Timestamp::toLocalDateTime)
            .map(localDateTime -> ZonedDateTime.of(localDateTime, nonNullZoneId));
  }

  @NonNull
  private Optional<Timestamp> parsePreviewTimestamp(@NonNull String text) {
    try {
      Timestamp timestamp = Timestamp.valueOf(text);
      return Optional.of(timestamp);
    } catch (IllegalArgumentException e) {
      LOG.warn("Cannot convert timestamp \"{}\", ignore", text, e);
      return Optional.empty();
    }
  }

  @NonNull
  private ZoneId parseTimeZone(@NonNull String text) {
    return TimeZone.getTimeZone(text).toZoneId();
  }

  @NonNull
  private static Optional<String> findStringValue(@NonNull Context fragmentContext, @NonNull String name) {
    String value = (String) fragmentContext.get(name);
    return Optional.ofNullable(value);
  }

  @Required
  public void setCatalogAliasTranslationService(CatalogAliasTranslationService catalogAliasTranslationService) {
    this.catalogAliasTranslationService = catalogAliasTranslationService;
  }

  @Required
  public void setFragmentContextAccessor(LiveContextContextAccessor fragmentContextAccessor) {
    this.fragmentContextAccessor = fragmentContextAccessor;
  }

  @Required
  public void setLiveContextSiteResolver(LiveContextSiteResolver liveContextSiteResolver) {
    this.liveContextSiteResolver = liveContextSiteResolver;
  }

  public void setContextNameMemberGroup(String contextNameMemberGroup) {
    this.contextNameMemberGroup = contextNameMemberGroup;
  }

  public void setContextNameTimestamp(String contextNameTimestamp) {
    this.contextNameTimestamp = contextNameTimestamp;
  }

  public void setContextNameTimezone(String contextNameTimezone) {
    this.contextNameTimezone = contextNameTimezone;
  }

  public void setContextNameUserId(String contextNameUserId) {
    this.contextNameUserId = contextNameUserId;
  }

  public void setContextNameUserName(String contextNameUserName) {
    this.contextNameUserName = contextNameUserName;
  }

  public void setContextNamePreviewUserGroup(String contextNamePreviewUserGroup) {
    this.contextNamePreviewUserGroup = contextNamePreviewUserGroup;
  }

  public void setContextNameUserGroupIds(String contextNameUserGroupIds) {
    this.contextNameUserGroupIds = contextNameUserGroupIds;
  }
}
