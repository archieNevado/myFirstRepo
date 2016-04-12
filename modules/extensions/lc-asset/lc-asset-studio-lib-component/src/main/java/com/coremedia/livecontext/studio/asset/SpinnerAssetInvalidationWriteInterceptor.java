package com.coremedia.livecontext.studio.asset;

import com.coremedia.blueprint.base.livecontext.util.CommerceReferenceHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.asset.util.AssetReadSettingsHelper;
import com.coremedia.livecontext.asset.util.AssetWriteSettingsHelper;
import com.coremedia.rest.cap.intercept.ContentWriteInterceptorBase;
import com.coremedia.rest.cap.intercept.ContentWriteRequest;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.coremedia.livecontext.asset.util.AssetReadSettingsHelper.LOCAL_SETTINGS;

public class SpinnerAssetInvalidationWriteInterceptor extends ContentWriteInterceptorBase {
  public static final String SEQUENCE_SPINNER_PROPERTY = "sequence";

  private AssetInvalidationWritePostProcessor postProcessor;
  private AssetReadSettingsHelper assetReadSettingsHelper;
  private AssetWriteSettingsHelper assetWriteSettingsHelper;
  private ContentRepository contentRepository;

  @Override
  public void intercept(ContentWriteRequest request) {
    if(!checkPreconditions(request)) {
      return ;
    }

    Map<String, Object> oldProperties = request.getEntity().getProperties();
    Map<String, Object> newProperties = request.getProperties();

    if (sequencePropertyChanged(request)) {
      rewriteOriginalExternalReferences(oldProperties, newProperties);
      invalidateExternalReferences(oldProperties, newProperties);
    }

    if (inheritedChanged(oldProperties, newProperties)) {
      rewriteOriginalExternalReferences(oldProperties, newProperties);
      invalidateExternalReferences(oldProperties, newProperties);
    }

    if (assignedCommerceReferencesChanged(oldProperties, newProperties)) {
      invalidateExternalReferences(oldProperties, newProperties);
    }
  }

  @VisibleForTesting
  boolean assignedCommerceReferencesChanged(@Nonnull Map<String, Object> oldProperties,
                                            @Nonnull Map<String, Object> newProperties) {
    if(!assetReadSettingsHelper.hasCommerceStruct(newProperties)) {
      //if those properties do not exist it means that there are no changes in the struct, so no changes in the inherited field.
      return false;
    }

    if(!assetReadSettingsHelper.hasCommerceStruct(oldProperties)) {
      //this means no commerce struct exists in content, so the state from the request is more actual
      return true;
    }

    if(!assetReadSettingsHelper.hasReferencesList(newProperties)) {
      //If no references exists which must be written to the commerce references list, no new commerce references exist.
      return false;
    }

    List<String> oldCommerceReferences = assetReadSettingsHelper.getCommerceReferences(oldProperties);
    List<String> newCommerceReferences = assetReadSettingsHelper.getCommerceReferences(newProperties);

    return !CollectionUtils.isEqualCollection(oldCommerceReferences, newCommerceReferences);
  }

  @VisibleForTesting
  boolean sequencePropertyChanged(@Nonnull ContentWriteRequest request) {
    Map<String, Object> properties = request.getProperties();
    return properties.containsKey(SEQUENCE_SPINNER_PROPERTY);
  }

  @VisibleForTesting
  boolean checkPreconditions(@Nonnull ContentWriteRequest request) {
    Content content = request.getEntity();
    Map<String, Object> properties = request.getProperties();

    return (content != null && properties != null);
  }

  @VisibleForTesting
  boolean inheritedChanged(@Nullable Map<String, Object> oldProperties,
                           @Nullable Map<String, Object> newProperties) {
    if(!assetReadSettingsHelper.hasCommerceStruct(newProperties)) {
      //if those properties do not exist it means that there are no changes in the struct, so no changes in the inherited field.
      return false;
    }

    if(!assetReadSettingsHelper.hasCommerceStruct(oldProperties)) {
      //this means no commerce struct exists in content, so the state from the request is more actual
      return true;
    }

    boolean oldInherit = assetReadSettingsHelper.readInheritedField(oldProperties);
    boolean newInherit = assetReadSettingsHelper.readInheritedField(newProperties);

    return oldInherit != newInherit;
  }

  @VisibleForTesting
  void invalidateExternalReferences(@Nonnull Map<String, Object> oldProperties,
                                    @Nonnull Map<String, Object> newProperties) {
    //get all External Ids which may have changed in studio due to the change of the sequence proeprty
    Set<String> allReferences = new HashSet<>();
    if(assetReadSettingsHelper.hasReferencesList(newProperties)) {
      allReferences.addAll(assetReadSettingsHelper.getCommerceReferences(newProperties));
    }

    if(assetReadSettingsHelper.hasReferencesList(oldProperties)) {
      allReferences.addAll(assetReadSettingsHelper.getCommerceReferences(oldProperties));
    }
    //we delegate the invaliations to the write post processor
    //as the write interceptor has too old sequence number
    postProcessor.addInvalidations(new ArrayList<>(allReferences));
  }

  @SuppressWarnings("unchecked")
  Set<Content> resolveAllSpinnerPictures(@Nonnull Map<String, Object> oldProperties, @Nonnull Map<String, Object> newProperties) {
    //first show if the sequence property has changed, if it changed use its pictures
    if (newProperties.containsKey(SEQUENCE_SPINNER_PROPERTY)) {
      Object newPictures = newProperties.get(SEQUENCE_SPINNER_PROPERTY);
      return new HashSet<>((Collection) newPictures);
    }

    //If sequence property has not changed, get the existing pictures directly from the oldContent
    Object oldPictures = oldProperties.get(SEQUENCE_SPINNER_PROPERTY);
    if (oldPictures != null && oldPictures instanceof Collection) {
      return new HashSet<>((Collection) oldPictures);
    }

    return new HashSet<>();
  }

  private void rewriteOriginalExternalReferences(Map<String, Object> oldProperties, Map<String, Object> newProperties) {

    Set<Content> allPictures = resolveAllSpinnerPictures(oldProperties, newProperties);
    Set<String> allReferences = buildOriginalCommerceReferenceList(allPictures);

    Struct localSettings = evaluateNewSettingsFoundation(oldProperties, newProperties);
    writeNewStruct(newProperties, allReferences, localSettings);
  }

  Struct evaluateNewSettingsFoundation(Map<String, Object> oldProperties, Map<String, Object> newProperties) {
    Struct localSettings = emptyStruct();
    Object newLocalSettings = newProperties.get(LOCAL_SETTINGS);
    Object oldLocalSettings = oldProperties.get(LOCAL_SETTINGS);
    if(newLocalSettings != null) {
      localSettings = (Struct) newLocalSettings;
    } else if (oldLocalSettings != null){
      localSettings = (Struct) oldLocalSettings;
    }
    return localSettings;
  }

  void writeNewStruct(Map<String, Object> newProperties, Set<String> allReferences, Struct localSettings) {
    boolean isInherited = assetReadSettingsHelper.readInheritedField(localSettingsToMap(localSettings));

    Struct newLocalSettingsStruct = assetWriteSettingsHelper.createNewSettingsStructWithReferences(localSettingsToMap(localSettings),
            new ArrayList<>(allReferences),
            isInherited);
    newProperties.put(LOCAL_SETTINGS, newLocalSettingsStruct);
  }

  private Map<String, Object> localSettingsToMap(Struct localSettings) {
    HashMap<String, Object> stringObjectHashMap = new HashMap<>();
    stringObjectHashMap.put(LOCAL_SETTINGS, localSettings);
    return stringObjectHashMap;
  }
  private Set<String> buildOriginalCommerceReferenceList(Set<Content> allPictures) {
    Set<String> allReferences = new HashSet<>();
    for (Content aPicture : allPictures) {
      allReferences.addAll(CommerceReferenceHelper.getExternalReferences(aPicture));
    }
    return allReferences;
  }

  private Struct emptyStruct() {
    return contentRepository.getConnection().getStructService().createStructBuilder().build();
  }

  @Required
  public void setPostProcessor(AssetInvalidationWritePostProcessor postProcessor) {
    this.postProcessor = postProcessor;
  }

  @Required
  public void setAssetReadSettingsHelper(AssetReadSettingsHelper assetReadSettingsHelper) {
    this.assetReadSettingsHelper = assetReadSettingsHelper;
  }

  @Required
  public void setAssetWriteSettingsHelper(AssetWriteSettingsHelper assetWriteSettingsHelper) {
    this.assetWriteSettingsHelper = assetWriteSettingsHelper;
  }

  @Required
  public void setContentRepository(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }
}
