package com.coremedia.ecommerce.studio.rest;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdParserHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cms.middle.defaultpicture.ContentDefaultPictureResolver;
import com.coremedia.cms.middle.defaultpicture.DefaultPicture;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Optional;

public class CommerceContentTypesPictureResolver implements ContentDefaultPictureResolver {

  public static final String EXTERNAL_ID = "externalId";

  public static final String CM_EXTERNAL_CHANNEL = "CMExternalChannel";
  public static final String CM_EXTERNAL_PRODUCT = "CMExternalProduct";
  public static final String CM_PRODUCT_TEASER = "CMProductTeaser";

  private final CommerceConnectionSupplier commerceConnectionSupplier;
  private final ContentRepository repository;
  private final String contentType;

  public CommerceContentTypesPictureResolver(@NonNull CommerceConnectionSupplier commerceConnectionSupplier,
                                             @NonNull ContentRepository repository,
                                             @NonNull String contentType) {
    this.commerceConnectionSupplier = commerceConnectionSupplier;
    this.repository = repository;
    this.contentType = contentType;
  }

  @Override
  public ContentType getContentType() {
    return repository.getContentType(contentType);
  }

  @NonNull
  @Override
  public Optional<DefaultPicture> resolve(@NonNull Content content) {
    return commerceConnectionSupplier
            .findConnection(content)
            .flatMap(commerceConnection -> resolveWithCommerceConnection(content, commerceConnection));
  }

  private Optional<DefaultPicture> resolveWithCommerceConnection(Content content, CommerceConnection commerceConnection) {
    String externalId = content.getString(CommerceContentTypesPictureResolver.EXTERNAL_ID);
    StoreContext storeContext = commerceConnection.getInitialStoreContext();
    CommerceId commerceId = CommerceIdParserHelper.parseCommerceIdOrThrow(externalId);

    //the actual asset URL does not change, so we always add a timestamp hash
    if (content.getType().isSubtypeOf(CM_EXTERNAL_PRODUCT) || content.getType().isSubtypeOf(CM_PRODUCT_TEASER)) {
      Product product = commerceConnection.getCatalogService().findProductById(commerceId, storeContext);
      if (product != null) {
        DefaultPicture defaultPicture = new DefaultPicture();
        defaultPicture.setUrl(product.getThumbnailUrl() + "#" + System.currentTimeMillis());
        return Optional.of(defaultPicture);
      }
    } else if (content.getType().isSubtypeOf(CM_EXTERNAL_CHANNEL)) {
      Category category = commerceConnection.getCatalogService().findCategoryById(commerceId, storeContext);
      if (category != null) {
        DefaultPicture defaultPicture = new DefaultPicture();
        defaultPicture.setUrl(category.getThumbnailUrl() + "#" + System.currentTimeMillis());
        return Optional.of(defaultPicture);
      }
    }

    return Optional.empty();
  }

  @Override
  public int getOrder() {
    return ContentDefaultPictureResolver.DEFAULT_ORDER - 100;
  }
}
