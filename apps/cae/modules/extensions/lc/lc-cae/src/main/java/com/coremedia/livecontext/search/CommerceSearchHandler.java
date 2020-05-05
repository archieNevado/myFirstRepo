package com.coremedia.livecontext.search;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentStoreContext;
import com.coremedia.blueprint.cae.handlers.PageHandlerBase;
import com.coremedia.blueprint.cae.searchsuggestion.Suggestion;
import com.coremedia.blueprint.cae.searchsuggestion.Suggestions;
import com.coremedia.blueprint.common.contentbeans.CMAction;
import com.coremedia.blueprint.common.contentbeans.CMObject;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.link.StorefrontRef;
import com.coremedia.livecontext.ecommerce.search.SearchService;
import com.coremedia.livecontext.ecommerce.search.SuggestionResult;
import com.coremedia.objectserver.view.substitution.Substitution;
import com.coremedia.objectserver.web.HandlerHelper;
import com.coremedia.objectserver.web.links.Link;
import com.coremedia.objectserver.web.links.LinkFormatter;
import com.coremedia.objectserver.web.links.LinkTransformer;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.coremedia.blueprint.base.links.UriConstants.ContentTypes.CONTENT_TYPE_JSON;
import static com.coremedia.blueprint.base.links.UriConstants.Segments.PREFIX_DYNAMIC;
import static com.coremedia.livecontext.fragment.links.CommerceLinkTemplateTypes.SEARCH_REDIRECT;

/**
 * Handler gets search suggestions from shop search service.
 */
@RequestMapping
@Link
public class CommerceSearchHandler extends PageHandlerBase {

  private static final String ACTION_NAME = "shopsearch";
  private static final String ACTION_ID = "shopsearch";

  private static final String SEGMENT_ROOT = "root";
  private static final String PARAMETER_QUERY = "query";
  private static final String PARAMETER_TYPE = "type";

  /**
   * e.g.: /dynamic/shopName/shopsearch?type=suggest&query=dre
   * e.g.: /dynamic/shopName/shopsearch?query=dress
   */
  private static final String   URI_PATTERN =
                  '/' + PREFIX_DYNAMIC +
                  "/{" + SEGMENT_ROOT + "}" +
                  '/' + ACTION_NAME;

  public static final String SEARCH_TERM_KEY = "searchTerm";

  private LinkFormatter linkFormatter;
  private SitesService sitesService;
  private CommerceConnectionSupplier commerceConnectionSupplier;

  @Substitution(ACTION_ID)
  @SuppressWarnings("unused")
  public CommerceSearchActionState createActionState(CMAction representative, HttpServletRequest request) {
    return new CommerceSearchActionState(representative);
  }

  /**
   * Performs shop suggestion search and provides a JSON object containing the suggestions.
   *
   * @param context a path segment to find resolve navigation context
   * @param term the search term to search in commerce
   */
  @ResponseBody
  @GetMapping(value = URI_PATTERN, params = {PARAMETER_TYPE, PARAMETER_QUERY}, produces = CONTENT_TYPE_JSON)
  public List<Suggestion> handleAjaxKeywordSuggestion(
          @PathVariable(SEGMENT_ROOT) String context,
          @RequestParam(value = PARAMETER_QUERY) String term) {

    Navigation navigation = getNavigation(context);
    if (!(navigation instanceof CMObject)) {
      throw new IllegalArgumentException("Could not get suggestions from shop search.");
    }

    CommerceConnection commerceConnection = findCommerceConnection((CMObject) navigation)
            .orElseThrow(() -> new IllegalArgumentException("Could not get suggestions from shop search."));

    SearchService searchService = commerceConnection.getSearchService()
            .orElseThrow(() -> new IllegalArgumentException("Could not get suggestions from shop search."));

    StoreContext storeContext = commerceConnection.getStoreContext();

    List<SuggestionResult> commerceSuggestions = searchService.getAutocompleteSuggestions(term, storeContext);
    Suggestions suggestions = new Suggestions();
    List<Suggestion> suggestionList = new ArrayList<>();
    for (SuggestionResult commerceSuggestion : commerceSuggestions) {
      suggestionList.add(new Suggestion(commerceSuggestion.getSuggestTerm(), term, (long) commerceSuggestion.getResultCount()));
    }
    // sort suggestions by count and enforce limit
    Collections.sort(suggestionList);
    suggestions.addAll(suggestionList);

    return suggestions.delegate();
  }

  /**
   * Redirects to shop search result page.
   *
   * @param context a path segment to find resolve navigation context
   * @param term the search term to search in commerce
   */
  @PostMapping(value = URI_PATTERN, params = {PARAMETER_QUERY}, produces = CONTENT_TYPE_JSON)
  public Object handleSearchRequest(
          @PathVariable(SEGMENT_ROOT) String context,
          @RequestParam(value = PARAMETER_QUERY) String term,
          HttpServletRequest request,
          HttpServletResponse response) {

    // if no context available: return "not found"
    Navigation navigation = getNavigation(context);
    if (!(navigation instanceof CMObject)) {
      return HandlerHelper.notFound();
    }

    return CurrentStoreContext.find()
            .flatMap(storeContext -> getSearchRedirectStorefrontRef(storeContext, term))
            .map(StorefrontRef::toLink)
            .map(UriComponentsBuilder::fromUriString)
            .map(ucb -> getRedirectUrl(request, response, ucb))
            .map(url -> (Object) new RedirectView(url))
            .orElseGet(HandlerHelper::notFound);

  }

  @NonNull
  Optional<StorefrontRef> getSearchRedirectStorefrontRef(@NonNull StoreContext storeContext, @NonNull String term) {
    return storeContext.getConnection().getLinkService()
            .flatMap(linkService -> linkService.getStorefrontRef(SEARCH_REDIRECT, storeContext))
            .map(storefrontRef -> storefrontRef.replace(Map.of(SEARCH_TERM_KEY, term)));
  }

  private Optional<CommerceConnection> findCommerceConnection(CMObject navigation) {
    Content content = navigation.getContent();
    return commerceConnectionSupplier.findConnection(content);
  }

  @NonNull
  @VisibleForTesting
  private String getRedirectUrl(@NonNull HttpServletRequest request, HttpServletResponse response, @NonNull UriComponentsBuilder uriComponentsBuilder) {
    UriComponents redirectUrl = uriComponentsBuilder.scheme(request.getScheme()).build();
    String urlStr = redirectUrl.toString();
    List<LinkTransformer> transformers = linkFormatter.getTransformers();
    for (LinkTransformer transformer : transformers) {
      urlStr = transformer.transform(urlStr, null, null, request, response, true);
    }
    return UriComponentsBuilder.fromHttpUrl(urlStr).build().toString();
  }

  @SuppressWarnings("UnusedDeclaration") // NOSONAR
  @Link(type = CommerceSearchActionState.class, uri = URI_PATTERN)
  public UriComponents buildSearchActionLink(CommerceSearchActionState state, UriTemplate uriTemplate, Map<String, Object> linkParameters, HttpServletRequest request) {
    UriComponentsBuilder uri = UriComponentsBuilder.fromPath(uriTemplate.toString());
    UriComponentsBuilder builder = addLinkParametersAsQueryParameters(uri, linkParameters);

    Content content = state.getAction().getContent();
    Optional<Site> site = sitesService.getContentSiteAspect(content).findSite();
    if (!site.isPresent()) {
      return null;
    }

    Navigation context = getContextHelper().currentSiteContext();
    String firstPathSegment = getPathSegments(context).get(0);

    return builder.buildAndExpand(ImmutableMap.of(SEGMENT_ROOT, firstPathSegment));
  }

  @Override
  @Required
  public void setSitesService(SitesService sitesService) {
    this.sitesService = sitesService;
  }

  @Required
  public void setCommerceConnectionSupplier(CommerceConnectionSupplier commerceConnectionSupplier) {
    this.commerceConnectionSupplier = commerceConnectionSupplier;
  }

  @Required
  public void setLinkFormatter(LinkFormatter linkFormatter) {
    this.linkFormatter = linkFormatter;
  }
}
