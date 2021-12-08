package com.coremedia.livecontext.studio.asset;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceConnectionSupplier;
import com.coremedia.blueprint.base.livecontext.util.CommerceReferenceHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.cap.struct.Struct;
import com.coremedia.ecommerce.studio.rest.cache.CommerceCacheInvalidationSource;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static com.coremedia.livecontext.asset.util.AssetReadSettingsHelper.NAME_LOCAL_SETTINGS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class AssetInvalidationWriteInterceptorTest {

  @Mock
  private CommerceConnectionSupplier commerceConnectionSupplier;

  @Mock
  private CommerceConnection commerceConnection;

  @Mock
  private CommerceCacheInvalidationSource invalidationSource;

  @Mock
  private ContentType cmPictureType;

  @Mock
  private ContentWriteRequest contentWriteRequest;

  @Mock
  private Content content;

  @Mock
  private ContentRepository repository;

  @Mock
  private Struct oldLocalSettings, newLocalSettings;

  private AssetInvalidationWriteInterceptor testling;

  @Before
  public void setUp() throws Exception {
    testling = new AssetInvalidationWriteInterceptor(commerceConnectionSupplier, invalidationSource);

    testling.setType(cmPictureType);
    testling.afterPropertiesSet();

    when(commerceConnectionSupplier.findConnection(any(Content.class))).thenReturn(Optional.of(commerceConnection));

    when(contentWriteRequest.getEntity()).thenReturn(content);
    Map<String, Object> properties = new HashMap<>();
    properties.put(NAME_LOCAL_SETTINGS, newLocalSettings);
    when(contentWriteRequest.getProperties()).thenReturn(properties);
  }

  @Test
  public void testReferencesChange() {
    try (var mocked = mockStatic(CommerceReferenceHelper.class)) {
      //the old references
      mocked.when(() -> CommerceReferenceHelper.getExternalReferences(content)).thenReturn(Arrays.asList("a", "b", "c"));
      //the new references
      mocked.when(() -> CommerceReferenceHelper.getExternalReferences(newLocalSettings)).thenReturn(Arrays.asList("c", "d", "e"));
      testling.intercept(contentWriteRequest);
    }

    Set<String> expected = Set.of("d", "e", "b", "a");
    verify(invalidationSource, times(1)).invalidateReferences(argThat(expected::containsAll), any());
  }

  @Test
  public void testLocalSettingsChange() {
    // the local settings are changed
    when(content.getStruct(AssetInvalidationWriteInterceptor.STRUCT_PROPERTY_NAME)).thenReturn(oldLocalSettings);

    try (var mocked = mockStatic(CommerceReferenceHelper.class)) {
      // the references are not changed
      mocked.when(() -> CommerceReferenceHelper.getExternalReferences(content)).thenReturn(Arrays.asList("a", "b", "c"));
      mocked.when(() -> CommerceReferenceHelper.getExternalReferences(newLocalSettings)).thenReturn(Arrays.asList("a", "b", "c"));
      testling.intercept(contentWriteRequest);
    }

    Set<String> expected = Set.of("a", "b", "c");
    verify(invalidationSource, times(1)).invalidateReferences(argThat(expected::containsAll), any());
  }
}
