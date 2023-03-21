package com.coremedia.livecontext.pagegrid;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGridPlacement;
import com.coremedia.blueprint.base.pagegrid.ContentBackedStyleGrid;
import com.coremedia.blueprint.base.pagegrid.impl.ContentBackedPageGridServiceImpl;
import com.coremedia.blueprint.base.tree.TreeRelation;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.augmentation.AugmentationService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Map;
import java.util.function.BiFunction;

import static java.lang.invoke.MethodHandles.lookup;

/**
 * PageGridService merges content backed pageGrids along an external category hierarchy.
 */
public class ContentAugmentedPageGridServiceImpl extends ContentBackedPageGridServiceImpl {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  static final String CM_EXTERNAL_CHANNEL = "CMExternalChannel";

  private AugmentationService augmentationService;
  private CommerceConnectionSupplier commerceConnectionSupplier;
  private BiFunction<Content, TreeRelation<Content>, Content> rootCategoryContentSupplier =
          (content, treeRelation) -> this.getRootCategoryContent(content);

  /**
   * Make #getMergedHierarchicalPageGridPlacements available for
   * {@link ContentAugmentedProductPageGridServiceImpl#getMergedPageGridPlacements}
   */
  @NonNull
  @Override
  protected Map<String, ContentBackedPageGridPlacement> getMergedPageGridPlacements(
          @NonNull Content navigation,
          @NonNull String pageGridName,
          @NonNull ContentBackedStyleGrid styleGrid,
          @NonNull TreeRelation<Content> treeRelation) {
    return super.getMergedPageGridPlacements(navigation, pageGridName, styleGrid, treeRelation);
  }

  @Nullable
  @Override
  protected Content getParentOf(@Nullable Content content, TreeRelation<Content> treeRelation) {
    if (content == null || !content.getType().isSubtypeOf(CM_EXTERNAL_CHANNEL)) {
      return null;
    }

    return treeRelation.getParentOf(content);
  }

  @Nullable
  @Override
  public Content getLayout(@NonNull Content content, @NonNull String pageGridName, @NonNull TreeRelation<Content> treeRelation) {
    Content style = styleSettingsDocument(content, pageGridName);
    if (style == null) {
      Content rootCategoryContent = rootCategoryContentSupplier.apply(content, treeRelation);
      if (rootCategoryContent != null) {
        style = styleSettingsDocument(rootCategoryContent, pageGridName);
      }
    }
    return style != null ? style : getDefaultLayout(content);
  }

  @Nullable
  private Content getRootCategoryContent(@NonNull Content content) {
    return commerceConnectionSupplier.findConnection(content)
            .map(connection -> getRootCategoryContent(content, connection))
            .orElse(null);
  }

  @Nullable
  private Content getRootCategoryContent(@NonNull Content content, @NonNull CommerceConnection commerceConnection) {
    try {
      var storeContext = commerceConnection.getInitialStoreContext();
      Category rootCategory = commerceConnection.getCatalogService()
              .findRootCategory(storeContext.getCatalogAlias(), storeContext);
      return augmentationService.getContent(rootCategory);
    } catch (CommerceException e) {
      LOG.warn("Could not retrieve root category for Content {}.", content, e);
      return null;
    }
  }

  @Autowired
  @Qualifier("categoryAugmentationService")
  public void setAugmentationService(AugmentationService augmentationService) {
    this.augmentationService = augmentationService;
  }

  @Autowired
  public void setCommerceConnectionSupplier(CommerceConnectionSupplier commerceConnectionSupplier) {
    this.commerceConnectionSupplier = commerceConnectionSupplier;
  }

  public void setRootCategoryContentSupplier(BiFunction<Content, TreeRelation<Content>, Content> rootCategoryContentSupplier) {
    this.rootCategoryContentSupplier = rootCategoryContentSupplier;
  }
}
