package com.coremedia.livecontext.handler;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.multisite.SiteResolver;
import com.coremedia.blueprint.common.datevalidation.ValidityPeriodValidator;
import com.coremedia.blueprint.ecommerce.cae.AbstractCommerceContextInterceptor;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.contract.Contract;
import com.coremedia.livecontext.ecommerce.contract.ContractService;
import com.coremedia.livecontext.ecommerce.user.UserContext;
import com.coremedia.livecontext.ecommerce.workspace.WorkspaceId;
import com.coremedia.livecontext.fragment.FragmentContextProvider;
import com.coremedia.livecontext.fragment.FragmentParameters;
import com.coremedia.livecontext.fragment.links.context.Context;
import com.coremedia.livecontext.fragment.links.context.LiveContextContextHelper;
import com.coremedia.livecontext.fragment.links.context.accessors.LiveContextContextAccessor;
import com.coremedia.livecontext.handler.util.LiveContextSiteResolver;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Splitter;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.Optional;
import java.util.TimeZone;

import static com.coremedia.blueprint.base.links.UriConstants.Links.ABSOLUTE_URI_KEY;
import static com.coremedia.livecontext.handler.util.CaeStoreContextUtil.updateStoreContextWithFragmentParameters;
import static java.util.stream.Collectors.toList;

/**
 * Suitable for URLs whose second segment denotes the store, e.g. /fragment/10001/...
 */
public class FragmentCommerceContextInterceptor extends AbstractCommerceContextInterceptor {

  private static final Logger LOG = LoggerFactory.getLogger(FragmentCommerceContextInterceptor.class);

  private LiveContextContextAccessor fragmentContextAccessor;
  private LiveContextSiteResolver liveContextSiteResolver;
  private boolean contractsProcessingEnabled = true;

  // Names of context entries
  private String contextNameMemberGroup = "wc.preview.memberGroups";
  private String contextNameTimestamp = "wc.preview.timestamp";
  private String contextNamePreviewUserGroup = "wc.preview.usergroup";
  private String contextNameTimezone = "wc.preview.timezone";
  private String contextNameWorkspaceId = "wc.preview.workspaceId";
  private String contextNameUserId = "wc.user.id";
  private String contextNameUserName = "wc.user.loginid";
  private String contextNameContractIds = "wc.preview.contractIds";
  private String contextNameUserGroupIds = "wc.user.membergroupids";

  private CatalogAliasTranslationService catalogAliasTranslationService;

  // --- AbstractCommerceContextInterceptor -------------------------

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

    if (connection.isPresent()) {
      StoreContext storeContext = connection.get().getStoreContext();
      FragmentParameters fragmentParameters = FragmentContextProvider.getFragmentContext(request).getParameters();
      updateStoreContextWithFragmentParameters(catalogAliasTranslationService, storeContext, fragmentParameters, site);

      if (isPreview()) {
        LiveContextContextHelper.findContext(request)
                .ifPresent(fragmentContext -> initStoreContextPreview(fragmentContext, storeContext, request));
      }
    }

    return connection;
  }

  @Override
  protected void initUserContext(@NonNull CommerceConnection commerceConnection, @NonNull HttpServletRequest request) {
    super.initUserContext(commerceConnection, request);

    UserContext userContext = commerceConnection.getUserContext();
    Context fragmentContext = LiveContextContextHelper.findContext(request).orElse(null);
    if (userContext == null || fragmentContext == null) {
      return;
    }

    StoreContext storeContext = commerceConnection.getStoreContext();

    UserContext.Builder userContextBuilder = UserContext.buildCopyOf(userContext);
    findStringValue(fragmentContext, contextNameUserId).ifPresent(userContextBuilder::withUserId);
    findStringValue(fragmentContext, contextNameUserName).ifPresent(userContextBuilder::withUserName);
    userContext = userContextBuilder.build();
    commerceConnection.setUserContext(userContext);

    findStringValue(fragmentContext, contextNameUserGroupIds).ifPresent(userSegments -> {
      StoreContext clonedStoreContext = commerceConnection.getStoreContextProvider()
              .buildContext(storeContext)
              .withUserSegments(userSegments)
              .build();
      commerceConnection.setStoreContext(clonedStoreContext);
    });

    if (contractsProcessingEnabled) {
      ContractService contractService = commerceConnection.getContractService();
      initUserContextContractsProcessing(fragmentContext, storeContext, userContext, contractService);
    }
  }

  @Override
  @Nullable
  protected Site getSite(@NonNull HttpServletRequest request, String normalizedPath) {
    FragmentParameters parameters = FragmentContextProvider.getFragmentContext(request).getParameters();
    return parameters != null ? liveContextSiteResolver.findSiteFor(parameters) : null;
  }

  @Override
  public SiteResolver getSiteResolver() {
    return liveContextSiteResolver;
  }

  // --- features (expected to be useful) ---------------------------

  protected void setFragmentContext(@NonNull HttpServletRequest request) {
    // apply the absolute URL flag for fragment requests
    request.setAttribute(ABSOLUTE_URI_KEY, true);
    fragmentContextAccessor.openAccessToContext(request);
  }

  // --- customization hooks (expected to be overridden) ------------

  // --- configure --------------------------------------------------

  @Required
  public void setFragmentContextAccessor(LiveContextContextAccessor fragmentContextAccessor) {
    this.fragmentContextAccessor = fragmentContextAccessor;
  }

  @Required
  public void setLiveContextSiteResolver(LiveContextSiteResolver liveContextSiteResolver) {
    this.liveContextSiteResolver = liveContextSiteResolver;
  }

  public void setContractsProcessingEnabled(boolean contractsProcessingEnabled) {
    this.contractsProcessingEnabled = contractsProcessingEnabled;
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

  public void setContextNameWorkspaceId(String contextNameWorkspaceId) {
    this.contextNameWorkspaceId = contextNameWorkspaceId;
  }

  public void setContextNameUserId(String contextNameUserId) {
    this.contextNameUserId = contextNameUserId;
  }

  public void setContextNameUserName(String contextNameUserName) {
    this.contextNameUserName = contextNameUserName;
  }

  public void setContextNameContractIds(String contextNameContractIds) {
    this.contextNameContractIds = contextNameContractIds;
  }

  // --- internal ---------------------------------------------------

  private void initStoreContextPreview(@NonNull Context fragmentContext, @NonNull StoreContext storeContext,
                                       @NonNull HttpServletRequest request) {
    initStoreContextUserSegments(fragmentContext, storeContext);
    initStoreContextPreviewMode(fragmentContext, storeContext, request);
    initStoreContextWorkspaceId(fragmentContext, storeContext);
  }

  private void initStoreContextUserSegments(@NonNull Context fragmentContext, @NonNull StoreContext storeContext) {
    findStringValue(fragmentContext, contextNameMemberGroup)
            .ifPresent(storeContext::setUserSegments);
  }

  private void initStoreContextWorkspaceId(@NonNull Context fragmentContext, @NonNull StoreContext storeContext) {
    findStringValue(fragmentContext, contextNameWorkspaceId)
            .map(WorkspaceId::of)
            .ifPresent(storeContext::setWorkspaceId);
  }

  private void initStoreContextPreviewMode(@NonNull Context fragmentContext, @NonNull StoreContext storeContext,
                                           @NonNull HttpServletRequest request) {
    if (!isStudioPreviewRequest(request)) {
      return;
    }
    initStoreContextPreviewDate(fragmentContext, storeContext, request);
  }

  @VisibleForTesting
  boolean isStudioPreviewRequest(@NonNull HttpServletRequest request) {
    return LiveContextPageHandlerBase.isStudioPreviewRequest(request);
  }

  private void initStoreContextPreviewDate(@NonNull Context fragmentContext, @NonNull StoreContext storeContext,
                                           @NonNull HttpServletRequest request) {
    ZonedDateTime previewDate = createPreviewDate(fragmentContext).orElse(null);
    if (previewDate != null) {
      Calendar calendar = GregorianCalendar.from(previewDate);
      storeContext.setPreviewDate(previewDate);
      request.setAttribute(ValidityPeriodValidator.REQUEST_ATTRIBUTE_PREVIEW_DATE, calendar);
    }

    findStringValue(fragmentContext, contextNamePreviewUserGroup)
            .ifPresent(storeContext::setUserSegments);
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

  private void initUserContextContractsProcessing(@NonNull Context fragmentContext, @NonNull StoreContext storeContext,
                                                  @NonNull UserContext userContext,
                                                  @Nullable ContractService contractService) {
    Collection<String> contractIdsFromContext = contractIds(fragmentContext);
    if (contractIdsFromContext.isEmpty()) {
      return;
    }

    // check if user is allowed to execute a call for the passed contracts
    if (contractService != null) {
      String organizationId = findStringValue(fragmentContext, "user.organization.id").orElse(null);

      Collection<String> contractIdsForUser = contractIds(contractService, storeContext, userContext, organizationId);
      Collection intersection = CollectionUtils.intersection(contractIdsForUser, contractIdsFromContext);
      if (!intersection.isEmpty()) {
        //noinspection unchecked
        storeContext.setContractIds(new ArrayList<String>(intersection));
      }
    }
  }

  @NonNull
  private Collection<String> contractIds(@NonNull Context fragmentContext) {
    Optional<String> contractIdsStr = findStringValue(fragmentContext, contextNameContractIds);

    return contractIdsStr
            .filter(str -> !str.isEmpty())
            .map(Splitter.on(' ')::splitToList)
            .orElseGet(Collections::emptyList);
  }

  @NonNull
  private static Collection<String> contractIds(@NonNull ContractService contractService,
                                                @NonNull StoreContext storeContext, @NonNull UserContext userContext,
                                                @Nullable String organizationId) {
    Collection<Contract> contractsForUser = contractService.findContractIdsForUser(userContext, storeContext,
            organizationId);

    return contractsForUser.stream()
            .map(CommerceBean::getExternalTechId)
            .collect(toList());
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
}
