package com.coremedia.livecontext.pagegrid;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.blueprint.base.pagegrid.ContentBackedPageGridPlacement;
import com.coremedia.blueprint.base.pagegrid.ContentBackedStyleGrid;
import com.coremedia.blueprint.base.pagegrid.impl.ContentBackedPageGridServiceImpl;
import com.coremedia.blueprint.base.tree.TreeRelation;
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

import java.util.Map;
import java.util.function.BiFunction;

import static java.lang.String.format;
import static java.lang.invoke.MethodHandles.lookup;

/**
 * This ContentBackedPageGridService merges content backed pageGrids for augmented products.
 */
public class ContentAugmentedProductPageGridServiceImpl extends ContentBackedPageGridServiceImpl {

  private static final Logger LOG = LoggerFactory.getLogger(lookup().lookupClass());

  protected static final String CM_EXTERNAL_PRODUCT = "CMExternalProduct";
  protected static final String CM_EXTERNAL_CHANNEL = "CMExternalChannel";
  private static final String EXTERNAL_ID = "externalId";

  private ContentAugmentedPageGridServiceImpl augmentedCategoryPageGridService;
  private CommerceConnectionSupplier commerceConnectionSupplier;
  private  BiFunction<Content, TreeRelation<Content>, Content> nearestCategoryContentSupplier =
          this::getNearestAugmentingContent;

  @NonNull
  @Override
  protected Map<String, ContentBackedPageGridPlacement> getMergedPageGridPlacements(
          @NonNull Content content,
          @NonNull String pageGridName,
          @NonNull ContentBackedStyleGrid styleGrid,
          @NonNull TreeRelation<Content> treeRelation) {
    if (content.getType().isSubtypeOf(ContentAugmentedPageGridServiceImpl.CM_EXTERNAL_CHANNEL)) {
      return augmentedCategoryPageGridService.getMergedPageGridPlacements(content, pageGridName, styleGrid, treeRelation);
    }

    // CMExternalProduct
    Map<String, ContentBackedPageGridPlacement> result = getPlacements(content, pageGridName, styleGrid);

    // parental merge
    Content parentNavigation = getParentOf(content, treeRelation);
    if (parentNavigation != null) {
      Map<String, ContentBackedPageGridPlacement> parentPlacements = augmentedCategoryPageGridService
              .getMergedPageGridPlacements(parentNavigation, "pdpPagegrid", styleGrid, treeRelation);
      result = merge(result, parentPlacements, styleGrid);
    }

    addMissingPlacementsFromLayout(result, styleGrid);
    return result;
  }

  @Nullable
  @Override
  protected Content getParentOf(@Nullable Content content, TreeRelation<Content> treeRelation) {
    if (content == null || !content.getType().isSubtypeOf(CM_EXTERNAL_PRODUCT)) {
      return null;
    }

    return getParentExternalChannelContent(content, treeRelation);
  }

  @Nullable
  @Override
  public Content getLayout(@NonNull Content content, @NonNull String pageGridName, @NonNull TreeRelation<Content> treeRelation) {
    Content style = styleSettingsDocument(content, pageGridName);

    if (style == null) {
      Content parentExternalChannelContent = getParentExternalChannelContent(content, cast(treeRelation));
      if (parentExternalChannelContent != null) {
        return augmentedCategoryPageGridService.getLayout(parentExternalChannelContent, pageGridName, treeRelation);
      }
    }

    return style;
  }

  @Nullable
  private Content getParentExternalChannelContent(@NonNull Content content,
                                                  @NonNull TreeRelation<Content> treeRelation) {
    // return content itself, if already subtype of external channel
    if (content.getType().isSubtypeOf(CM_EXTERNAL_CHANNEL)){
      return content;
    }

    // return null, if the content is not an external product
    if (!content.getType().isSubtypeOf(CM_EXTERNAL_PRODUCT)) {
      return null;
    }

    return this.nearestCategoryContentSupplier.apply(content, treeRelation);
  }

  private Content getNearestAugmentingContent(Content content, TreeRelation<Content> treeRelation) {
    Site site = getSitesService().getContentSiteAspect(content).getSite();
    if (site == null) {
      LOG.warn("Content '{}' has no site, cannot determine parent content.", content.getPath());
      return null;
    }

    String reference = content.getString(EXTERNAL_ID);
    CommerceId commerceId = CommerceIdParserHelper.parseCommerceId(reference).orElse(null);

    if (commerceId == null) {
      LOG.warn("Content '{}' provides invalid commerce reference '{}', cannot determine parent content.",
              content.getPath(), reference);
      return null;
    }

    return commerceConnectionSupplier.findConnection(site).map(commerceConnection ->
                    getNearestContentForCategory(content, site, commerceId, cast(treeRelation), commerceConnection))
            .orElse(null);
  }

  @Nullable
  private static Content getNearestContentForCategory(Content content,
                                                      Site site,
                                                      CommerceId commerceId,
                                                      ExternalChannelContentTreeRelation treeRelation,
                                                      CommerceConnection commerceConnection) {
    StoreContext storeContext = commerceConnection.getInitialStoreContext();
    CommerceBean commerceBean = commerceConnection.getCommerceBeanFactory().createBeanFor(commerceId, storeContext);
    if (commerceBean instanceof Product) {
      return treeRelation.getNearestContentForCategory(((Product)commerceBean).getCategory(), site);
    }

    LOG.warn("Unexpected commerce type '{}' found in '{}' from site {}.", commerceId, content, site);
    return null;
  }

  @NonNull
  private static ExternalChannelContentTreeRelation cast(@NonNull TreeRelation<Content> treeRelation) {
    supports(treeRelation);
    return (ExternalChannelContentTreeRelation) treeRelation;
  }

  private static void supports(TreeRelation<Content> treeRelation) {
    if (!(treeRelation instanceof ExternalChannelContentTreeRelation)) {
      throw new IllegalStateException(format("%s only supports tree relations of type %s but got %s",
              lookup().lookupClass(), ExternalChannelContentTreeRelation.class, treeRelation.getClass()));
    }
  }

  @Override
  protected void initialize() {
    super.initialize();
    // provoke exception if the tree relation has wrong type
    var treeRelation = getTreeRelation();
    if (treeRelation != null) {
      supports(treeRelation);
    }
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

  public void setNearestCategoryContentSupplier(BiFunction<Content, TreeRelation<Content>, Content> nearestCategoryContentSupplier) {
    this.nearestCategoryContentSupplier = nearestCategoryContentSupplier;
  }
}
