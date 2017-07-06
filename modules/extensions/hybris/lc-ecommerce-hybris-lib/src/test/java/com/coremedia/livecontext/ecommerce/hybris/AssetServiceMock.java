package com.coremedia.livecontext.ecommerce.hybris;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.common.StoreContext;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;

public class AssetServiceMock implements AssetService {

  @Nonnull
  @Override
  public CatalogPicture getCatalogPicture(String url) {
    return null;
  }

  @Nonnull
  @Override
  public List<Content> findPictures(@Nonnull String id) {
    Content content = mock(Content.class);
    return singletonList(content);
  }

  @Nonnull
  @Override
  public List<Content> findVisuals(@Nonnull String id, boolean withDefault) {
    return emptyList();
  }

  @Nonnull
  @Override
  public List<Content> findVisuals(@Nonnull String id) {
    return emptyList();
  }

  @Nonnull
  @Override
  public List<Content> findDownloads(@Nullable String id) {
    return emptyList();
  }

  @Nullable
  @Override
  public Content getDefaultPicture(@Nonnull Site site) {
    return null;
  }

  @Nonnull
  @Override
  public AssetService withStoreContext(StoreContext storeContext) {
    return this;
  }
}
