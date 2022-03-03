package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CatalogAliasTranslationService;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.ecommerce.studio.rest.configuration.ECommerceStudioConfigurationProperties;
import com.coremedia.ecommerce.studio.rest.configuration.PreloadChildCategories;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.rest.cap.content.ContentRepositoryResource;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryResourceTest {

  @Mock
  private CatalogAliasTranslationService catalogAliasTranslationService;

  @Mock(answer = RETURNS_DEEP_STUBS)
  private Category category;

  @Mock
  private ContentRepositoryResource contentRepositoryResource;

  @Mock
  private Category parent;

  @Mock
  private Category child;

  @ParameterizedTest
  @MethodSource
  void fillRepresentation(PreloadChildCategories preload, int timesOfLoadCalls, boolean isRoot) {
    var properties = new ECommerceStudioConfigurationProperties();
    properties.setPreloadChildCategories(preload);
    var resourceImpl = new CategoryResource(catalogAliasTranslationService, properties);
    var categoryResource = spy(resourceImpl);

    when(category.getId()).thenReturn(CommerceIdParserHelper.parseCommerceIdOrThrow("test:///catalog/category/aValue"));
    when(category.getContext().getConnection().getAssetService()).thenReturn(Optional.empty());
    when(category.getChildren()).thenReturn(List.of(child));
    when(category.getProducts()).thenReturn(List.of());
    lenient().when(category.isRoot()).thenReturn(isRoot);
    doReturn(parent).when(category).getParent();
    doReturn(category).when(categoryResource).getEntity(Map.of());
    when(categoryResource.getContentRepositoryResource()).thenReturn(contentRepositoryResource);
    when(contentRepositoryResource.getPreviewControllerUrlPattern()).thenReturn("preview?id={0}");

    categoryResource.fillRepresentation(Map.of(), new CategoryRepresentation());

    // both getParent and load will load the child category
    Mockito.verify(child, Mockito.times(timesOfLoadCalls)).getParent();
    Mockito.verify(child, Mockito.times(timesOfLoadCalls)).load();
  }

  static Stream<Arguments> fillRepresentation() {
    return Stream.of(
            Arguments.of(PreloadChildCategories.ALL, 1, false),
            Arguments.of(PreloadChildCategories.ALL_EXCEPT_TOP_LEVEL, 1, false),
            Arguments.of(PreloadChildCategories.NONE, 0, false),
            Arguments.of(PreloadChildCategories.ALL, 1, true),
            Arguments.of(PreloadChildCategories.ALL_EXCEPT_TOP_LEVEL, 0, true),
            Arguments.of(PreloadChildCategories.NONE, 0, true)
    );
  }

}
