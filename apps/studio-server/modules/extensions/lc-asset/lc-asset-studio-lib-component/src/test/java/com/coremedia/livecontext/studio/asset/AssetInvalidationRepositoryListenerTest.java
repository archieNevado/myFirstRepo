package com.coremedia.livecontext.studio.asset;

import com.coremedia.blueprint.base.livecontext.util.CommerceReferenceHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.content.events.ContentEvent;
import com.coremedia.ecommerce.studio.rest.cache.CommerceCacheInvalidationSource;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.HashSet;
import java.util.List;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssetInvalidationRepositoryListenerTest {

  @Mock
  private CommerceCacheInvalidationSource invalidationSource;

  @Mock
  private ContentType cmPictureType;

  @Mock
  private Content content;

  @Mock
  private ContentEvent event;

  @Mock
  private ContentRepository repository;

  private AssetInvalidationRepositoryListener testling;

  @Before
  public void setUp() {
    testling = new AssetInvalidationRepositoryListener(invalidationSource, repository);

    testling.start();

    when(event.getType()).thenReturn(ContentEvent.CONTENT_CREATED);
    when(event.getContent()).thenReturn(content);
    when(content.getType()).thenReturn(cmPictureType);
    when(cmPictureType.isSubtypeOf(AssetInvalidationRepositoryListener.CMPICTURE)).thenReturn(true);
  }

  @Test
  public void testHandleContentEvent() {
    // content has any external references
    List<String> externalReferences = List.of("vendor:///catalog/product/what", "vendor:///catalog/product/ever");

    try (var mocked = mockStatic(CommerceReferenceHelper.class)) {
      mocked.when(() -> CommerceReferenceHelper.getExternalReferences(content)).thenReturn(externalReferences);
      testling.handleContentEvent(event);
    }

    // then all products and product variants should be invalidated.
    verify(invalidationSource).invalidateReferences(new HashSet<>(externalReferences), null);
  }
}
