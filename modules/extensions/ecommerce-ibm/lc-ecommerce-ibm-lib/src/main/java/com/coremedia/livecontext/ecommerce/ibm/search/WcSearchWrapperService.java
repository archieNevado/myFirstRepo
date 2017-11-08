package com.coremedia.livecontext.ecommerce.ibm.search;

import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.ibm.common.AbstractWcWrapperService;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestConnector;
import com.coremedia.livecontext.ecommerce.ibm.common.WcRestServiceMethod;
import org.springframework.http.HttpMethod;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getCurrency;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getLocale;
import static com.coremedia.livecontext.ecommerce.ibm.common.StoreContextHelper.getStoreId;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

/**
 * Wrapper query and result format of the IBM rest search service.
 */
public class WcSearchWrapperService extends AbstractWcWrapperService {

  private static final WcRestServiceMethod<WcSuggestionViews, Void>
          GET_KEYWORD_SUGGESTIONS = WcRestConnector.createSearchServiceMethod(HttpMethod.GET, "store/{storeId}/sitecontent/keywordSuggestionsByTerm/{term}", false, false, true, WcSuggestionViews.class);

  public List<WcSuggestion> getKeywordSuggestionsByTerm(String term, @Nonnull StoreContext storeContext) {
    try {
      Map<String, String[]> parametersMap = createParametersMap(null, getLocale(storeContext), getCurrency(storeContext), storeContext);
      parametersMap.put("catalogId", new String[]{getStoreId(storeContext)});
      WcSuggestionViews suggestionViews = getRestConnector().callService(
              GET_KEYWORD_SUGGESTIONS, asList(getStoreId(storeContext), term), parametersMap, null, storeContext, null);
      if (suggestionViews != null && suggestionViews.getSuggestionView().get(0) != null) {
        return suggestionViews.getSuggestionView().get(0).getEntry();
      }

      return emptyList();
    } catch (CommerceException e) {
      throw e;
    } catch (Exception e) {
      throw new CommerceException(e);
    }
  }
}
