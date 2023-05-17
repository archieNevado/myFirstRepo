package com.coremedia.ecommerce.studio.rest;

import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cms.middle.defaultpicture.DefaultPicture;
import com.coremedia.cms.middle.defaultpicture.DefaultPictureResolver;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Optional;

public class CommerceBeanDefaultPictureResolver implements DefaultPictureResolver<CommerceBean> {

  private final ContentRepository contentRepository;

  public CommerceBeanDefaultPictureResolver(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }

  @NonNull
  @Override
  public Optional<DefaultPicture> resolve(@NonNull CommerceBean entity) {
    if (entity instanceof Product) {
      DefaultPicture defaultPicture = new DefaultPicture();
      defaultPicture.setUrl(RepresentationHelper.modifyAssetImageUrl(((Product) entity).getThumbnailUrl(), contentRepository));
      return Optional.of(defaultPicture);
    }

    if (entity instanceof Category) {
      DefaultPicture defaultPicture = new DefaultPicture();
      defaultPicture.setUrl(RepresentationHelper.modifyAssetImageUrl(((Category) entity).getThumbnailUrl(), contentRepository));
      return Optional.of(defaultPicture);
    }

    return Optional.empty();
  }
}
