package com.coremedia.livecontext.ecommerce.hybris;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.asset.AssetService;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.common.CommerceId;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.mock;

public class AssetServiceMock implements AssetService {

  @NonNull
  @Override
  public CatalogPicture getCatalogPicture(@NonNull String url, @NonNull CommerceId commerceId) {
    return mock(CatalogPicture.class);
  }

  @NonNull
  @Override
  public List<Content> findPictures(@NonNull CommerceId id) {
    Content content = mock(Content.class);
    return singletonList(content);
  }

  @NonNull
  @Override
  public List<Content> findPictures(@NonNull CommerceId id, boolean withDefault) {
    Content content = mock(Content.class);
    return singletonList(content);
  }

  @NonNull
  @Override
  public List<Content> findVisuals(@NonNull CommerceId id, boolean withDefault) {
    return emptyList();
  }

  @NonNull
  @Override
  public List<Content> findVisuals(@NonNull CommerceId id) {
    return emptyList();
  }

  @NonNull
  @Override
  public List<Content> findDownloads(@Nullable CommerceId id) {
    return emptyList();
  }

  @Nullable
  @Override
  public Content getDefaultPicture(@NonNull Site site) {
    return null;
  }

}
