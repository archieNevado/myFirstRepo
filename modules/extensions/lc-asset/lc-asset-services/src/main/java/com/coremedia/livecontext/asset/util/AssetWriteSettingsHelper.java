package com.coremedia.livecontext.asset.util;

import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.struct.StructBuilder;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;
import java.util.Map;

import static com.coremedia.livecontext.asset.util.AssetReadSettingsHelper.COMMERCE;
import static com.coremedia.livecontext.asset.util.AssetReadSettingsHelper.LOCAL_SETTINGS;
import static com.coremedia.livecontext.asset.util.AssetReadSettingsHelper.ORIGIN_REFERENCES;
import static com.coremedia.livecontext.asset.util.AssetReadSettingsHelper.REFERENCES;

public class AssetWriteSettingsHelper {

  private ContentRepository contentRepository;
  private AssetReadSettingsHelper assetReadSettingsHelper;

  public Struct createNewSettingsStructWithReferences(Map<String, Object> localSettings,
                                                      List<String> commerceReferences,
                                                      boolean inheritedActive) {
    StructBuilder structBuilder = ((Struct) localSettings.get(LOCAL_SETTINGS)).builder();
    ensureCommerceStructIsAvailable(structBuilder, localSettings);
    rewriteReferencesField(structBuilder, commerceReferences, inheritedActive);
    rewriteOriginalReferencesField(structBuilder, commerceReferences);
    return structBuilder.build();
  }

  private void ensureCommerceStructIsAvailable(StructBuilder settingsBuilder, Map<String, Object> localSettings) {
    if(!assetReadSettingsHelper.hasCommerceStruct(localSettings)) {
      settingsBuilder.declareStruct(COMMERCE, contentRepository.getConnection().getStructService().emptyStruct());
    }
  }

  private void rewriteReferencesField(StructBuilder structBuilder, List<String> commerceReferences, boolean inheritedActive) {
    if (inheritedActive) {
      rewriteCommerceStructField(structBuilder, commerceReferences, REFERENCES);
    }
  }

  private void rewriteOriginalReferencesField(StructBuilder structBuilder, List<String> commerceReferences) {
    rewriteCommerceStructField(structBuilder, commerceReferences, ORIGIN_REFERENCES);
  }

  private void rewriteCommerceStructField(StructBuilder structBuilder,
                                    List<String> commerceReferences,
                                    String property) {
    structBuilder.enter(COMMERCE);
    if (structBuilder.currentStruct().get(property) != null) {
      structBuilder.remove(property);
    }
    structBuilder.declareStrings(property, Integer.MAX_VALUE, commerceReferences);
    structBuilder.up();
  }

  @Required
  public void setAssetReadSettingsHelper(AssetReadSettingsHelper assetReadSettingsHelper) {
    this.assetReadSettingsHelper = assetReadSettingsHelper;
  }

  @Required
  public void setContentRepository(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }
}
