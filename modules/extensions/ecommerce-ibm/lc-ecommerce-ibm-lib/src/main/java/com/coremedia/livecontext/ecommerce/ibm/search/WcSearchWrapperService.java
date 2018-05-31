package com.coremedia.livecontext.ecommerce.ibm.search;

import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWcWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestServiceMethod;
import org.springframework.http.HttpMethod;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getStoreId;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Wrapper query and result format of the IBM rest search service.
 */
public class WcSearchWrapperService extends AbstractWcWrapperService {

  private static final WcRestServiceMethod<WcSuggestionViews, Void>
          GET_KEYWORD_SUGGESTIONS = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/sitecontent/keywordSuggestionsByTerm/{term}", false, false, true, WcSuggestionViews.class);

  public List<WcSuggestion> getKeywordSuggestionsByTerm(String term, @Nonnull StoreContext storeContext) {
    try {
      String storeId = getStoreId(storeContext);

      List<String> variableValues = asList(storeId, term);

      Map<String, String[]> parametersMap = buildParameterMap()
              .withCurrency(storeContext)
              .withLanguageId(storeContext)
              .build();

      String catalogId = storeContext.getCatalogId();
      if (isNotBlank(catalogId)) {
        parametersMap.put("catalogId", new String[]{catalogId});
      }

      WcSuggestionViews suggestionViews = getRestConnector().callService(GET_KEYWORD_SUGGESTIONS, variableValues,
              parametersMap, null, storeContext, null);

      if (suggestionViews == null) {
        return emptyList();
      }

      return suggestionViews.getSuggestionView().stream()
              .findFirst()
              .map(WcSuggestionView::getEntry)
              .orElseGet(Collections::emptyList);
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }
}
