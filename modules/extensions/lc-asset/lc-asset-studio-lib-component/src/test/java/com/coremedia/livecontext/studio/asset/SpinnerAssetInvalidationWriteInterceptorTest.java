package com.coremedia.livecontext.studio.asset;

import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.asset.util.AssetReadSettingsHelper;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import com.google.common.collect.ImmutableList;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.coremedia.livecontext.studio.asset.SpinnerAssetInvalidationWriteInterceptor.SEQUENCE_SPINNER_PROPERTY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class SpinnerAssetInvalidationWriteInterceptorTest {

  private SpinnerAssetInvalidationWriteInterceptor testling;

  @Mock
  private Map<String, Object> oldLocalSettings;
  @Mock
  private Map<String, Object> newLocalSettings;
  @Mock
  private AssetReadSettingsHelper assetReadSettingsHelper;
  @Mock
  private ContentWriteRequest contentWriteRequest;
  @Mock
  private AssetInvalidationWritePostProcessor postProcessor;
  @Mock
  private Struct emptyStruct;

  @Before
  public void setup() {
    ContentRepository contentRepository = mock(ContentRepository.class, RETURNS_DEEP_STUBS);
    testling = new SpinnerAssetInvalidationWriteInterceptor();
    initMocks(this);
    testling.setAssetReadSettingsHelper(assetReadSettingsHelper);
    testling.setPostProcessor(postProcessor);
    testling.setContentRepository(contentRepository);

    when(contentRepository.getConnection().getStructService().createStructBuilder().build()).thenReturn(emptyStruct);
  }

  @Test
  public void testAssignedProductsChangedNewSettingsNoCommerceStruct() throws Exception {
    when(assetReadSettingsHelper.hasCommerceStruct(newLocalSettings)).thenReturn(false);

    assertFalse(testling.assignedCommerceReferencesChanged(oldLocalSettings, newLocalSettings));
  }

  @Test
  public void testAssignedProductsChangedOldSettingsNoCommerceStruct() throws Exception {
    when(assetReadSettingsHelper.hasCommerceStruct(newLocalSettings)).thenReturn(true);
    when(assetReadSettingsHelper.hasCommerceStruct(oldLocalSettings)).thenReturn(false);

    assertTrue(testling.assignedCommerceReferencesChanged(oldLocalSettings, newLocalSettings));
  }

  @Test
  public void testAssignedProductsChangedNewSettingsHaveNoProducts() throws Exception {
    when(assetReadSettingsHelper.hasCommerceStruct(newLocalSettings)).thenReturn(true);
    when(assetReadSettingsHelper.hasCommerceStruct(oldLocalSettings)).thenReturn(true);
    when(assetReadSettingsHelper.hasReferencesList(newLocalSettings)).thenReturn(false);

    assertFalse(testling.assignedCommerceReferencesChanged(oldLocalSettings, newLocalSettings));
  }

  @Test
  public void testAssignedProductsChangedEqualProductList() throws Exception {
    List<String> newProducts = ImmutableList.of("1", "2");
    List<String> oldProducts = ImmutableList.of("1", "2");
    when(assetReadSettingsHelper.hasCommerceStruct(newLocalSettings)).thenReturn(true);
    when(assetReadSettingsHelper.hasCommerceStruct(oldLocalSettings)).thenReturn(true);
    when(assetReadSettingsHelper.hasReferencesList(newLocalSettings)).thenReturn(true);
    when(assetReadSettingsHelper.getCommerceReferences(newLocalSettings)).thenReturn(newProducts);
    when(assetReadSettingsHelper.getCommerceReferences(oldLocalSettings)).thenReturn(oldProducts);

    assertFalse(testling.assignedCommerceReferencesChanged(oldLocalSettings, newLocalSettings));
  }

  @Test
  public void testAssignedProductsChangedNotEqualProductList() throws Exception {
    List<String> newProducts = ImmutableList.of("1", "2");
    List<String> oldProducts = ImmutableList.of("1", "3");
    when(assetReadSettingsHelper.hasCommerceStruct(newLocalSettings)).thenReturn(true);
    when(assetReadSettingsHelper.hasCommerceStruct(oldLocalSettings)).thenReturn(true);
    when(assetReadSettingsHelper.hasReferencesList(newLocalSettings)).thenReturn(true);
    when(assetReadSettingsHelper.getCommerceReferences(newLocalSettings)).thenReturn(newProducts);
    when(assetReadSettingsHelper.getCommerceReferences(oldLocalSettings)).thenReturn(oldProducts);

    assertTrue(testling.assignedCommerceReferencesChanged(oldLocalSettings, newLocalSettings));
  }

  @Test
  public void testSequencePropertyChanged() throws Exception {
    Map<String, Object> newProperties = new HashMap<>();
    newProperties.put(SEQUENCE_SPINNER_PROPERTY, mock(Object.class));
    when(contentWriteRequest.getProperties()).thenReturn(newProperties);

    assertTrue(testling.sequencePropertyChanged(contentWriteRequest));
  }

  @Test
  public void testSequencePropertyNotChanged() throws Exception {
    Map<String, Object> newProperties = new HashMap<>();
    when(contentWriteRequest.getProperties()).thenReturn(newProperties);

    assertFalse(testling.sequencePropertyChanged(contentWriteRequest));
  }

  @Test (expected = NullPointerException.class)
  public void resolveAllSpinnerPictures() throws Exception {
    //noinspection ConstantConditions
    testling.resolveAllSpinnerPictures(null, null);
  }

  @Test
  public void resolveAllSpinnerPicturesNoPictures() throws Exception {
    Map<String, Object> properties = allSpinnerPicturesTestNewProperties();

    Set<Content> contents = testling.resolveAllSpinnerPictures(oldLocalSettings, properties);
    assertEquals(0, contents.size());
  }

  @Test
  public void resolveAllSpinnerPicturesFromNewProperties() {
    Content newPicture1 = mock(Content.class);
    Content newPicture2 = mock(Content.class);
    Map<String, Object> properties = allSpinnerPicturesTestNewProperties(newPicture1, newPicture2);

    Set<Content> contents = testling.resolveAllSpinnerPictures(oldLocalSettings, properties);
    assertEquals(2, contents.size());
    assertTrue("New Picture 1 must be part of the result but is not", contents.contains(newPicture1));
    assertTrue("New Picture 2 must be part of the result but is not", contents.contains(newPicture2));
  }

  @Test
  public void resolveAllSpinnerPicturesFromContent() throws Exception {
    Content oldPicture1 = mock(Content.class);
    Content oldPicture2 = mock(Content.class);
    Map<String, Object> properties = allSpinnerPicturesTestNewProperties();
    Map<String, Object> oldProperties = allSpinnerPicturesTestNewProperties(oldPicture1, oldPicture2);

    Set<Content> contents = testling.resolveAllSpinnerPictures(oldProperties, properties);

    assertEquals(2, contents.size());
    assertTrue("Old Picture 1 must be part of the result but is not", contents.contains(oldPicture1));
    assertTrue("Old Picture 2 must be part of the result but is not", contents.contains(oldPicture2));
  }

  @Test
  public void resolveAllSpinnerPicturesFromNewPropertiesEvenIfOldExist() throws Exception {
    Content oldPicture1 = mock(Content.class);
    Content newPicture1 = mock(Content.class);
    Map<String, Object> properties = allSpinnerPicturesTestNewProperties(newPicture1);
    Map<String, Object> oldProperties = allSpinnerPicturesTestNewProperties(oldPicture1);

    Set<Content> contents = testling.resolveAllSpinnerPictures(oldProperties, properties);

    assertEquals(1, contents.size());
    assertTrue("New Picture 1 must be part of the result but is not", contents.contains(newPicture1));
  }

  @Test
  public void testCheckPreconditions() throws Exception {
    when(contentWriteRequest.getEntity()).thenReturn(mock(Content.class));
    when(contentWriteRequest.getProperties()).thenReturn(new HashMap<String, Object>());
    boolean actual = testling.checkPreconditions(contentWriteRequest);

    assertTrue(actual);
  }

  @Test
  public void testCheckPreconditionsNoContent() throws Exception {
    when(contentWriteRequest.getEntity()).thenReturn(null);
    when(contentWriteRequest.getProperties()).thenReturn(new HashMap<String, Object>());
    boolean actual = testling.checkPreconditions(contentWriteRequest);

    assertFalse(actual);
  }

  @Test
  public void testCheckPreconditionsNoNewProperties() throws Exception {
    when(contentWriteRequest.getEntity()).thenReturn(mock(Content.class));
    when(contentWriteRequest.getProperties()).thenReturn(null);
    boolean actual = testling.checkPreconditions(contentWriteRequest);

    assertFalse(actual);
  }

  @Test
  public void testInheritedChangedNoCommerceStruct() throws Exception {
    when(assetReadSettingsHelper.hasCommerceStruct(newLocalSettings)).thenReturn(Boolean.FALSE);

    boolean inheritedChanged = testling.inheritedChanged(oldLocalSettings, newLocalSettings);

    assertFalse(inheritedChanged);
  }

  @Test
  public void testInheritedChangedNullInputs() throws Exception {
    when(assetReadSettingsHelper.hasCommerceStruct(null)).thenReturn(Boolean.FALSE);

    boolean inheritedChanged = testling.inheritedChanged(null, null);

    assertFalse(inheritedChanged);
  }

  @Test
  public void testInheritedChangedNoOldCommerceStruct() throws Exception {
    when(assetReadSettingsHelper.hasCommerceStruct(newLocalSettings)).thenReturn(Boolean.TRUE);
    when(assetReadSettingsHelper.hasCommerceStruct(oldLocalSettings)).thenReturn(Boolean.FALSE);

    boolean inheritedChanged = testling.inheritedChanged(oldLocalSettings, newLocalSettings);

    assertTrue(inheritedChanged);
  }

  @Test
  public void testInheritedChangedInheritStatesAreEqual() throws Exception {
    when(assetReadSettingsHelper.hasCommerceStruct(newLocalSettings)).thenReturn(Boolean.TRUE);
    when(assetReadSettingsHelper.hasCommerceStruct(oldLocalSettings)).thenReturn(Boolean.TRUE);

    when(assetReadSettingsHelper.readInheritedField(newLocalSettings)).thenReturn(Boolean.TRUE);
    when(assetReadSettingsHelper.readInheritedField(oldLocalSettings)).thenReturn(Boolean.TRUE);

    boolean inheritedChanged = testling.inheritedChanged(oldLocalSettings, newLocalSettings);

    assertFalse(inheritedChanged);
  }

  @Test
  public void testInheritedChangedInheritStatesAreNotEqual() throws Exception {
    when(assetReadSettingsHelper.hasCommerceStruct(newLocalSettings)).thenReturn(Boolean.TRUE);
    when(assetReadSettingsHelper.hasCommerceStruct(oldLocalSettings)).thenReturn(Boolean.TRUE);

    when(assetReadSettingsHelper.readInheritedField(newLocalSettings)).thenReturn(Boolean.FALSE);
    when(assetReadSettingsHelper.readInheritedField(oldLocalSettings)).thenReturn(Boolean.TRUE);

    boolean inheritedChanged = testling.inheritedChanged(oldLocalSettings, newLocalSettings);

    assertTrue(inheritedChanged);
  }

  @Test
  public void testInvalidateExternalReferences() throws Exception {
    testling.invalidateExternalReferences(null, null);
    verify(postProcessor, times(1)).addInvalidations(new ArrayList<String>());
  }

  @Test
  public void testInvalidateExternalReferencesNoNewSettings() throws Exception {
    List<String> oldSettingsInvalidation = ImmutableList.of("1ProductReference", "2ProductReference", "3ProductReference");
    when(assetReadSettingsHelper.getCommerceReferences(oldLocalSettings)).thenReturn(oldSettingsInvalidation);
    when(assetReadSettingsHelper.hasReferencesList(oldLocalSettings)).thenReturn(Boolean.TRUE);
    testling.invalidateExternalReferences(oldLocalSettings, null);
    verify(postProcessor, times(1)).addInvalidations(argThat(new CollectionContainsMatcher(oldSettingsInvalidation)));
  }

  @Test
  public void testInvalidateExternalReferencesNoOldSettings() throws Exception {
    List<String> newSettingsInvalidation = ImmutableList.of("1ProductReference", "2ProductReference", "3ProductReference");
    when(assetReadSettingsHelper.getCommerceReferences(newLocalSettings)).thenReturn(newSettingsInvalidation);
    when(assetReadSettingsHelper.hasReferencesList(newLocalSettings)).thenReturn(Boolean.TRUE);
    testling.invalidateExternalReferences(null, newLocalSettings);
    verify(postProcessor, times(1)).addInvalidations(argThat(new CollectionContainsMatcher(newSettingsInvalidation)));
  }

  @Test
  public void testInvalidateExternalReferencesBothSettings() throws Exception {
    List<String> oldSettingsInvalidation = ImmutableList.of("1OldProductReference", "2OldProductReference");
    when(assetReadSettingsHelper.getCommerceReferences(oldLocalSettings)).thenReturn(oldSettingsInvalidation);
    when(assetReadSettingsHelper.hasReferencesList(oldLocalSettings)).thenReturn(Boolean.TRUE);

    List<String> newSettingsInvalidation = ImmutableList.of("1ProductReference", "2ProductReference");
    when(assetReadSettingsHelper.getCommerceReferences(newLocalSettings)).thenReturn(newSettingsInvalidation);
    when(assetReadSettingsHelper.hasReferencesList(newLocalSettings)).thenReturn(Boolean.TRUE);

    ImmutableList<String> expected = ImmutableList.of("1OldProductReference", "2OldProductReference", "1ProductReference", "2ProductReference");

    testling.invalidateExternalReferences(oldLocalSettings, newLocalSettings);
    verify(postProcessor, times(1)).addInvalidations(argThat(new CollectionContainsMatcher(expected)));
  }

  @Test
  public void evaluateNewSettingsFoundationNoSettings() {
    Struct newStruct = testling.evaluateNewSettingsFoundation(oldLocalSettings, newLocalSettings);

    assertSame(emptyStruct, newStruct);
  }

  @Test
  public void evaluateNewSettingsSettingsFoundationFromContent() {
    Struct expectedSettings = mock(Struct.class);
    Map<String, Object> ownOldProperties = new HashMap<>();
    ownOldProperties.put("localSettings", expectedSettings);
    Struct newStruct = testling.evaluateNewSettingsFoundation(ownOldProperties, newLocalSettings);

    assertSame(expectedSettings, newStruct);
  }

  @Test
  public void evaluateNewSettingsSettingsFoundationFromProperties() {
    Struct expectedSettings = mock(Struct.class);
    Map<String, Object> ownNewProperties = new HashMap<>();
    ownNewProperties.put("localSettings", expectedSettings);
    Struct newStruct = testling.evaluateNewSettingsFoundation(oldLocalSettings, ownNewProperties);

    assertSame(expectedSettings, newStruct);
  }

  @Test
  public void evaluateNewSettingsSettingsFoundationFromPropertiesIfBothGiven() {
    Struct expectedSettings = mock(Struct.class);
    Struct notExpectedSettings = mock(Struct.class);

    Map<String, Object> ownNewProperties = new HashMap<>();
    ownNewProperties.put("localSettings", expectedSettings);

    Map<String, Object> ownOldProperties = new HashMap<>();
    ownOldProperties.put("localSettings", notExpectedSettings);

    Struct newStruct = testling.evaluateNewSettingsFoundation(ownOldProperties, ownNewProperties);

    assertSame(expectedSettings, newStruct);
  }

  private Map<String, Object> allSpinnerPicturesTestNewProperties(Content... pictures) {
    HashMap<String, Object> stringObjectHashMap = new HashMap<>();
    if(pictures != null && pictures.length > 0) {
      List<Content> newPictures = ImmutableList.copyOf(pictures);
      stringObjectHashMap.put(SEQUENCE_SPINNER_PROPERTY, newPictures);
    }
    return stringObjectHashMap;
  }

  private Content mockContentSequenceProperty(Content... pictures) {
    Content content = mock(Content.class);
    if(pictures != null) {
      when(content.get(SEQUENCE_SPINNER_PROPERTY)).thenReturn(ImmutableList.copyOf(pictures));
    }
    return content;
  }

  private class CollectionContainsMatcher extends BaseMatcher<Collection<String>> {

    private Iterable<String> items;

    public CollectionContainsMatcher(Iterable<String> items) {
      this.items = items;
    }

    @Override
    public boolean matches(Object o) {
      if (o == null) {
        return false;
      }
      if (!(o instanceof Collection)) {
        return false;
      }
      Collection collection = (Collection) o;
      for (String item : items) {
        if(!collection.contains(item)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public void describeTo(Description description) {

    }
  }
}