package com.coremedia.livecontext.fragment.links.transformers;

import com.coremedia.blueprint.base.links.UriConstants;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
import com.coremedia.blueprint.cae.layout.ContentBeanBackedPageGridPlacement;
import com.coremedia.blueprint.cae.web.taglib.FindNavigationContext;
import com.coremedia.blueprint.common.contentbeans.CMContext;
import com.coremedia.blueprint.common.contentbeans.CMDynamicList;
import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.layout.DynamizableContainer;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.blueprint.common.services.context.CurrentContextService;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SiteHelper;
import com.coremedia.livecontext.ecommerce.order.Cart;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.LiveContextLinkResolver;
import com.coremedia.objectserver.request.RequestUtils;
import com.coremedia.objectserver.web.links.LinkTransformer;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;

import static com.coremedia.livecontext.fragment.links.CommerceLinkUtils.isFragmentRequest;
import static com.coremedia.livecontext.fragment.links.transformers.LiveContextLinkTransformerOrderChecker.validateOrder;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.left;
import static org.apache.commons.lang3.StringUtils.remove;

/**
 * LiveContextLinkTransformer that creates Commerce-Links with cm-Param containing the backend-url.
 * in addition:
 * - removing jsessionid
 * - passing ShopController-Redirect Requests
 */
public class LiveContextLinkTransformer implements LinkTransformer, ApplicationListener<ContextRefreshedEvent> {

  // dummy URL meant to be replaced by the commerce link resolver later on
  public static final String DUMMY_URI_STRING = "http://lc-generic-live.vm";

  protected static final Logger LOG = LoggerFactory.getLogger(LiveContextLinkTransformer.class);

  private static final String UNSUPPORTED_LINK = "#";

  private List<LiveContextLinkResolver> liveContextLinkResolverList;
  private boolean isRemoveJSession = true;
  private CurrentContextService currentContextService;

  @Override
  public void onApplicationEvent(@NonNull ContextRefreshedEvent contextRefreshedEvent) {
    validateOrder(contextRefreshedEvent);
  }

  @Override
  public String transform(@NonNull String cmsLink, @Nullable Object bean, @Nullable String view,
                          @NonNull HttpServletRequest request,
                          @NonNull HttpServletResponse response,
                          boolean forRedirect) {

    if (!canHandle(cmsLink, bean, view, request)) {
      return cmsLink;
    }

    CMNavigation navigation = getNavigation(bean);
    // do not generate links if the navigation is null, discard the previously generated link
    if (navigation == null) {
      LOG.debug("Cannot transform link. Navigation is null. Link target: {}", bean);
      return UNSUPPORTED_LINK;
    }

    // do not generate links to content of a different site, discard the previously generated link
    CMNavigation targetRootNavigation = navigation.getRootNavigation();
    Navigation currentNavigation = FindNavigationContext.getNavigation(request);
    CMNavigation currentRootNavigation = currentNavigation.getRootNavigation();
    if (!currentRootNavigation.equals(targetRootNavigation)) {
      LOG.warn("Cannot transform link to a different site. Link target {} ({}), current context {} ({}).",
              navigation, targetRootNavigation, currentNavigation, currentRootNavigation);
      return UNSUPPORTED_LINK;
    }

    String modifiableSource = removeBaseUri(cmsLink, request);
    modifiableSource = removeJSession(modifiableSource);

    Object content = getContent(bean);
    return transform(modifiableSource, content, view, navigation, request);
  }

  /**
   * Check prerequisites of live context link transformers
   */
  @SuppressWarnings("OverlyComplexMethod")
  private static boolean canHandle(@NonNull String cmsLink, @Nullable Object bean, @Nullable String view, @NonNull HttpServletRequest request) {
    if (CurrentStoreContext.find(request).isEmpty()) {
      // not a commerce request at all
      return false;
    }

    Site site = SiteHelper.getSiteFromRequest(request);
    if (site == null) {
      return false;
    }

    // Only transform links for Fragment Requests or dynamic Ajax Requests in case of fragment scenario
    if (!isFragmentRequest(request)) {
      return false;
    }

    // transform ajax links in order to rewrite them to the ajax proxy on the commerce side
    if (bean instanceof ContentBeanBackedPageGridPlacement
            || bean instanceof DynamizableContainer
            || bean instanceof Cart
            // Dynamic Includes
            || (bean instanceof CMDynamicList && UriConstants.Views.VIEW_FRAGMENT.equals(view))) {
      return true;
    }

    //if the commerce link schemes rendered a dummy url before, it needs to be handled/replaced
    return cmsLink.startsWith(DUMMY_URI_STRING);
  }

  @NonNull
  private String transform(String source, @Nullable Object content, @Nullable String view, CMNavigation navigation,
                           @NonNull HttpServletRequest request) {
    Optional<String> nonBlankLcUrl = resolveUrl(source, content, view, navigation, request)
            .filter(StringUtils::isNotBlank);
    if (nonBlankLcUrl.isPresent()) {
      return nonBlankLcUrl.get();
    }

    if (isNotBlank(source)) {
      LOG.debug("Cannot resolve url for '{}', using source link '{}'.", content, source);
      return source;
    }

    LOG.warn("Cannot resolve url for '{}', no source link available.", content);
    return UNSUPPORTED_LINK;
  }

  @NonNull
  private Optional<String> resolveUrl(String source, @Nullable Object content, @Nullable String view,
                                      @Nullable CMNavigation navigation, @NonNull HttpServletRequest request) {
    if (source == null) {
      return Optional.empty();
    }

    return liveContextLinkResolverList.stream()
            .filter(resolver -> resolver.isApplicable(content, request))
            .map(resolver -> resolver.resolveUrl(source, content, navigation, view, request))
            .flatMap(Optional::stream)
            .findFirst();
  }

  @Nullable
  private static String removeBaseUri(@Nullable String source, @NonNull HttpServletRequest request) {
    String baseUri = RequestUtils.getBaseUri(request);

    if (source != null && source.startsWith(baseUri)) {
      return remove(source, baseUri);
    }

    return source;
  }

  @Nullable
  private String removeJSession(@Nullable String source) {
    if (isRemoveJSession && source != null && source.contains(";jsessionid")) {
      int jSessionIndex = StringUtils.indexOf(source, ";jsessionid");
      return left(source, jSessionIndex);
    }

    return source;
  }

  @Nullable
  private static Object getContent(@Nullable Object bean) {
    Object content = bean;

    if (content instanceof Page) {
      content = ((Page) content).getContent();
    }

    return content;
  }

  @Nullable
  private CMNavigation getNavigation(@Nullable Object bean) {
    Object context = bean;
    if (context instanceof Page) {
      context = ((Page) context).getNavigation();
    }

    if (context instanceof CMNavigation) {
      return (CMNavigation) context;
    }

    if (context instanceof CMLinkable) {
      List<? extends CMContext> contexts = ((CMLinkable) context).getContexts();
      if (!contexts.isEmpty()) {
        return contexts.get(0);
      }
    }

    if (context instanceof ContentBeanBackedPageGridPlacement) {
      return ((ContentBeanBackedPageGridPlacement) context).getNavigation();
    }

    return currentContextService.getContext();
  }

  public void setLiveContextLinkResolverList(List<LiveContextLinkResolver> liveContextLinkResolverList) {
    this.liveContextLinkResolverList = List.copyOf(liveContextLinkResolverList);
  }

  public void setRemoveJSession(boolean removeJSession) {
    isRemoveJSession = removeJSession;
  }

  public void setCurrentContextService(CurrentContextService currentContextService) {
    this.currentContextService = currentContextService;
  }
}
