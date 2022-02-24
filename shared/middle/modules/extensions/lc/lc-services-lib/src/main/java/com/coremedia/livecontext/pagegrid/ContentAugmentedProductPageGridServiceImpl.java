package com.coremedia.livecontext.pagegrid;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGrid;
import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGridPlacement;
import com.coremedia.blueprint.base.pagegrid.ContentBackedStyleGrid;
import com.coremedia.blueprint.base.pagegrid.impl.ContentBackedPageGridServiceImpl;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.tree.ExternalChannelContentTreeRelation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * This ContentBackedPageGridService merges content backed pageGrids for augmented products.
 */
public class ContentAugmentedProductPageGridServiceImpl extends ContentBackedPageGridServiceImpl<Content> {

  private static final Logger LOG = LoggerFactory.getLogger(ContentAugmentedProductPageGridServiceImpl.class);

  protected static final String CM_EXTERNAL_PRODUCT = "CMExternalProduct";
  protected static final String CM_EXTERNAL_CHANNEL = "CMExternalChannel";
  private static final String EXTERNAL_ID = "externalId";

  private ContentAugmentedPageGridServiceImpl augmentedCategoryPageGridService;
  private CommerceConnectionSupplier commerceConnectionSupplier;

  @NonNull
  @Override
  public ContentBackedPageGrid getContentBackedPageGrid(@NonNull Content content, @NonNull String pageGridName) {
    return super.getContentBackedPageGrid(content, pageGridName);
  }

  @NonNull
  @Override
  protected Map<String, ContentBackedPageGridPlacement> getMergedPageGridPlacements(
          @NonNull Content content, @NonNull String pageGridName,
          @NonNull ContentBackedStyleGrid styleGrid) {
    if (content.getType().isSubtypeOf(ContentAugmentedPageGridServiceImpl.CM_EXTERNAL_CHANNEL)) {
      return augmentedCategoryPageGridService.getMergedHierarchicalPageGridPlacements(content, pageGridName,
              styleGrid);
    }

    // CMExternalProduct
    Map<String, ContentBackedPageGridPlacement> result = getPlacements(content, pageGridName, styleGrid);

    // parental merge
    Content parentNavigation = getParentOf(content);
    if (parentNavigation != null) {
      Map<String, ContentBackedPageGridPlacement> parentPlacements = augmentedCategoryPageGridService
              .getMergedHierarchicalPageGridPlacements(parentNavigation, "pdpPagegrid", styleGrid);
      result = merge(result, parentPlacements, styleGrid);
    }

    addMissingPlacementsFromLayout(result, styleGrid);
    return result;
  }

  @Nullable
  @Override
  protected Content getParentOf(@Nullable Content content) {
    if (content == null || !content.getType().isSubtypeOf(CM_EXTERNAL_PRODUCT)) {
      return null;
    }

    return getParentExternalChannelContent(content);
  }

  @Nullable
  @Override
  public Content getLayout(@NonNull Content content, @NonNull String pageGridName) {
    Content style = styleSettingsDocument(content, pageGridName);

    if (style == null) {
      Content parentExternalChannelContent = getParentExternalChannelContent(content);
      if (parentExternalChannelContent != null) {
        return augmentedCategoryPageGridService.getLayout(parentExternalChannelContent, pageGridName);
      }
    }

    return style;
  }

  @Nullable
  private Content getParentExternalChannelContent(@NonNull Content content) {
    // return content itself, if already subtype of external channel
    if (content.getType().isSubtypeOf(CM_EXTERNAL_CHANNEL)){
      return content;
    }

    // return null, if the content is not an external product
    if (!content.getType().isSubtypeOf(CM_EXTERNAL_PRODUCT)) {
      return null;
    }

    Site site = getSitesService().getContentSiteAspect(content).getSite();
    if (site == null) {
      LOG.warn("Content '{}' has no site, cannot determine parent content.", content.getPath());
      return null;
    }

    String reference = content.getString(EXTERNAL_ID);
    Optional<CommerceId> commerceIdOptional = CommerceIdParserHelper.parseCommerceId(reference);

    if (commerceIdOptional.isEmpty()) {
      LOG.warn("Content '{}' provides invalid commerce reference '{}', cannot determine parent content.",
              content.getPath(), reference);
      return null;
    }

    CommerceId commerceId = commerceIdOptional.get();

    Optional<CommerceConnection> commerceConnectionOpt = commerceConnectionSupplier.findConnection(site);

    if (commerceConnectionOpt.isEmpty()) {
      LOG.debug("Commerce connection is not available for site '{}'; not looking up parent content.", site.getName());
      return null;
    }

    ExternalChannelContentTreeRelation treeRelation = (ExternalChannelContentTreeRelation) getTreeRelation();

    CommerceConnection commerceConnection = commerceConnectionOpt.get();
    StoreContext storeContext = commerceConnection.getInitialStoreContext();
    CommerceBean commerceBean = commerceConnection.getCommerceBeanFactory().createBeanFor(commerceId, storeContext);
    if (commerceBean instanceof Product) {
      return treeRelation.getNearestContentForCategory(((Product)commerceBean).getCategory(), site);
    }

    LOG.warn("Unexpected commerce type '{}' found in '{}' from site {}.", commerceId, content, site);
    return null;
  }

  @Autowired
  @Qualifier("pdpContentBackedPageGridService")
  public void setAugmentedCategoryPageGridService(ContentAugmentedPageGridServiceImpl categoryAugmentedPageGridService) {
    this.augmentedCategoryPageGridService = categoryAugmentedPageGridService;
  }

  @Autowired
  public void setCommerceConnectionSupplier(CommerceConnectionSupplier commerceConnectionSupplier) {
    this.commerceConnectionSupplier = commerceConnectionSupplier;
  }
}
