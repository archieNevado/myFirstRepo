package com.coremedia.blueprint.caas.augmentation;

import com.coremedia.blueprint.base.caas.model.adapter.SearchResult;
import com.coremedia.blueprint.base.caas.model.util.SearchHelper;
import com.coremedia.caas.search.solr.SearchQueryHelper;
import com.coremedia.caas.search.solr.SolrQueryBuilder;
import com.coremedia.caas.search.solr.SolrSearchResultFactory;
import com.coremedia.cap.common.CapType;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.multisite.Site;
import com.coremedia.id.IdScheme;
import com.coremedia.livecontext.asset.AssetSearchService;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

@DefaultAnnotation(NonNull.class)
public class CaasAssetSearchService implements AssetSearchService {

  private static final String COMMERCE_ITEMS_FIELD = "commerceitems";

  private final CaasAssetSearchServiceConfigProperties caasAssetSearchServiceConfigProperties;
  private final ContentRepository contentRepository;
  private final List<IdScheme> idSchemes;
  private final SolrSearchResultFactory searchResultFactory;

  private SolrQueryBuilder solrQueryBuilder;

  CaasAssetSearchService(CaasAssetSearchServiceConfigProperties caasAssetSearchServiceConfigProperties, SolrSearchResultFactory searchResultFactory, ContentRepository contentRepository, List<IdScheme> idSchemes, SolrQueryBuilder solrQueryBuilder) {
    this.caasAssetSearchServiceConfigProperties = caasAssetSearchServiceConfigProperties;
    this.searchResultFactory = searchResultFactory;
    this.contentRepository = contentRepository;
    this.idSchemes = idSchemes;
    this.solrQueryBuilder = solrQueryBuilder;
  }

  @NonNull
  @Override
  public List<Content> searchAssets(@NonNull String contentType, @NonNull String externalId, @NonNull Site site) {

    // Content type filter
    List<String> filterQueries = new ArrayList<>();
    docTypeFilterQuery(contentType).ifPresent(filterQueries::add);

    String query = SearchQueryHelper.exactQuery(COMMERCE_ITEMS_FIELD, '"' + externalId + '"');

    // create solr query
    SolrQuery solrQuery = solrQueryBuilder.createSearchQuery(query, site.getSiteRootDocument(), caasAssetSearchServiceConfigProperties.getLimit(), 0, filterQueries, emptyMap(), true);

    // search
    QueryResponse rawSearchResult = searchResultFactory.createSearchResult(solrQuery);

    // transform result
    SearchResult searchServiceResult = SearchHelper.getSearchServiceResult(rawSearchResult, caasAssetSearchServiceConfigProperties.getLimit(), solrQueryBuilder, idSchemes);
    return searchServiceResult.getResult();
  }

  private Optional<String> docTypeFilterQuery(String docType) {
    ContentType contentType = contentRepository.getContentType(docType);
    if (contentType == null) {
      return Optional.empty();
    }
    Set<ContentType> children = contentType.getChildren();
    List<String> docTypes = children.stream().map(CapType::getName).collect(Collectors.toList());
    docTypes.add(docType);
    return SearchHelper.contentTypesFilterQuery(
            docTypes,
            false,
            emptyList(),
            solrQueryBuilder.getDocumentTypeFieldName(),
            contentRepository);
  }
}
