package com.coremedia.livecontext.asset.util;

import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.struct.StructBuilder;
import com.coremedia.springframework.beans.RequiredPropertyNotSetException;
import edu.umd.cs.findbugs.annotations.NonNull;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

import static com.coremedia.livecontext.asset.util.AssetReadSettingsHelper.NAME_COMMERCE;
import static com.coremedia.livecontext.asset.util.AssetReadSettingsHelper.NAME_LOCAL_SETTINGS;
import static com.coremedia.livecontext.asset.util.AssetReadSettingsHelper.NAME_ORIGIN_REFERENCES;
import static com.coremedia.livecontext.asset.util.AssetReadSettingsHelper.NAME_REFERENCES;

public class AssetWriteSettingsHelper {

  private ContentRepository contentRepository;
  private AssetReadSettingsHelper assetReadSettingsHelper;

  @NonNull
  public Struct createNewSettingsStructWithReferences(@NonNull Map<String, Object> localSettings,
                                                      List<String> commerceReferences, boolean inheritedActive) {
    StructBuilder structBuilder = ((Struct) localSettings.get(NAME_LOCAL_SETTINGS)).builder();
    ensureCommerceStructIsAvailable(structBuilder, localSettings);
    rewriteReferencesField(structBuilder, commerceReferences, inheritedActive);
    rewriteOriginalReferencesField(structBuilder, commerceReferences);
    return structBuilder.build();
  }

  private void ensureCommerceStructIsAvailable(@NonNull StructBuilder settingsBuilder,
                                               @NonNull Map<String, Object> localSettings) {
    if (!assetReadSettingsHelper.hasCommerceStruct(localSettings)) {
      Struct emptyStruct = contentRepository.getConnection().getStructService().emptyStruct();
      settingsBuilder.declareStruct(NAME_COMMERCE, emptyStruct);
    }
  }

  private void rewriteReferencesField(@NonNull StructBuilder structBuilder, List<String> commerceReferences,
                                      boolean inheritedActive) {
    if (inheritedActive) {
      rewriteCommerceStructField(structBuilder, commerceReferences, NAME_REFERENCES);
    }
  }

  private void rewriteOriginalReferencesField(@NonNull StructBuilder structBuilder, List<String> commerceReferences) {
    rewriteCommerceStructField(structBuilder, commerceReferences, NAME_ORIGIN_REFERENCES);
  }

  private void rewriteCommerceStructField(@NonNull StructBuilder structBuilder, List<String> commerceReferences,
                                          String property) {
    structBuilder.enter(NAME_COMMERCE);
    if (structBuilder.currentStruct().get(property) != null) {
      structBuilder.remove(property);
    }
    structBuilder.declareStrings(property, Integer.MAX_VALUE, commerceReferences);
    structBuilder.up();
  }

  public void setAssetReadSettingsHelper(AssetReadSettingsHelper assetReadSettingsHelper) {
    this.assetReadSettingsHelper = assetReadSettingsHelper;
  }

  public void setContentRepository(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }

  @PostConstruct
  void initialize() {
    RequiredPropertyNotSetException.ifNull("contentRepository", contentRepository);
    RequiredPropertyNotSetException.ifNull("assetReadSettingsHelper", assetReadSettingsHelper);
  }
}
