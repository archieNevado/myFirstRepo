package com.coremedia.livecontext.asset.util;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.struct.StructBuilder;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.coremedia.cap.struct.StructBuilderMode.LOOSE;
import static com.coremedia.livecontext.asset.util.AssetReadSettingsHelper.COMMERCE;
import static com.coremedia.livecontext.asset.util.AssetReadSettingsHelper.INHERIT;
import static com.coremedia.livecontext.asset.util.AssetReadSettingsHelper.LOCAL_SETTINGS;
import static com.coremedia.livecontext.asset.util.AssetReadSettingsHelper.ORIGIN_REFERENCES;
import static com.coremedia.livecontext.asset.util.AssetReadSettingsHelper.REFERENCES;

/**
 * Helper for common livecontext asset operations.
 */
public class AssetHelper {

  public static final String CONFIG_KEY_DEFAULT_PICTURE = "livecontext.assets.default.picture";

  private ContentRepository contentRepository;
  private AssetReadSettingsHelper assetReadSettingsHelper;

  /**
   * Update the picture document with a new list of catalog object ids. That means the picture will be assigned
   * to each catalog object as catalog object picture. It handles all kinds of conflicts and corner cases when an update is
   * coming in. The following cases will be handled:
   * <pre>
   *           OLD STATE                                    NEW XMP DATA   RESULT STATE
   *
   * case  5:  null / []                                    null / []      No commerce struct
   * case  6:  null / []                                    [A, B]         inherit:TRUE, ori: [A, B], new: [A, B]
   * case  7:  inherit:TRUE, ori: [A, B], new: [A, B]       [A, C, D]      inherit:TRUE, ori: [A, C, D], new: [A, C, D]
   * case  8:  inherit:TRUE, ori: [A, B], new: [A, B]       null / []      No commerce struct
   * case  9:  inherit:FALSE, ori: [A, B], new: [A, C, D]   [E, F]         inherit:FALSE, ori: [E, F], new: [A, C, D]
   * case 10:  inherit:FALSE, ori: [A, B], new: [A, C, D]   null / []      inherit:FALSE, ori: [], new: [A, C, D]
   * case 11:  inherit:FALSE, ori: [A, B], new: []          [E, F]         inherit:TRUE, ori: [E, F], new: [E, F]
   * case 12:  inherit:FALSE, ori: [], new: [A, C, D]       [E, F]         inherit:FALSE, ori: [E, F], new: [A, C, D]
   * case 13:  new: [A, C, D]                               [E, F]         inherit:FALSE, ori: [E, F], new: [A, C, D]
   * case 14:  inherit:FALSE, ori: [A, B], new: []          []             inherit:FALSE, ori: [], new: []
   * case 15:  inherit:FALSE, ori: [], new: []              [A, B]         inherit:FALSE, ori: [A, B], new: [A, B]
   *</pre>
   *
   * Case 15 is same as case 6 but with an empty commerce struct.
   *
   * @param content the picture document
   * @param newCommerceReferences the list of catalog object ids that are to be assigned
   * @return the struct property that contains the updated commerce struct
   */
  public Struct updateCMPictureForExternalIds(@Nullable Content content, @Nonnull List<String> newCommerceReferences) {
    // load/create localSettins struct
    Struct struct = content == null ? null : content.getStruct(LOCAL_SETTINGS);

    if (struct == null && newCommerceReferences.size() > 0) {
      // case 5 (struct empty and externalIds empty)
      return updateStruct(getEmptyStruct(), true, newCommerceReferences, newCommerceReferences);
    } else if (struct == null) {
      // do nothing --> return empty struct
      return getEmptyStruct();
    }

    Struct resultStruct = getEmptyStruct();
    Map<String, Object> contentProperties = content.getProperties();

    if(!assetReadSettingsHelper.hasCommerceStruct(contentProperties)) {
      if (!newCommerceReferences.isEmpty()) {
        // case 4 and 6
        // upload with first time XMP data
        return updateStruct(struct, true, newCommerceReferences, newCommerceReferences);
      }
      return resultStruct;
    }

    // upload with existing struct
    List<String> oldCommerceReferences = assetReadSettingsHelper.getCommerceReferences(contentProperties);
    List<String> oldOriginCommerceReferences = assetReadSettingsHelper.getOriginCommerceReferences(contentProperties);
    boolean inherit = assetReadSettingsHelper.readInheritedField(contentProperties);

    return rewriteCommerceStruct(struct, inherit, oldCommerceReferences, oldOriginCommerceReferences, newCommerceReferences);
  }

  private Struct rewriteCommerceStruct(Struct struct, boolean inherit, List<String> oldCommerceReferences, List<String> oldOriginCommerceReferences, @Nonnull List<String> newCommerceReferences) {
    Struct resultStruct;// case 7-8 --> inherit = TRUE
    if (inherit) {
      if (newCommerceReferences.isEmpty()) {
        // case 8
        resultStruct = removeCommerceSubstruct(struct);
      } else {
        // case 7
        resultStruct = updateStruct(struct, true, newCommerceReferences, newCommerceReferences);
      }
      return resultStruct;
    }

    // inherit=FALSE && originReferences = []
    if (oldOriginCommerceReferences.isEmpty()) {

      if (oldCommerceReferences.isEmpty()) {
        // case 15
        resultStruct = updateStruct(struct, true, newCommerceReferences, newCommerceReferences);
      } else {
        // case 13
        resultStruct = updateStruct(struct, false, newCommerceReferences, oldCommerceReferences);
      }
      return resultStruct;
    }

    if (oldCommerceReferences.isEmpty()) {
      if (newCommerceReferences.isEmpty()) {
        // case 14
        resultStruct = updateStruct(struct, false, newCommerceReferences, newCommerceReferences);
      } else {
        // case 11
        resultStruct = updateStruct(struct, true, newCommerceReferences, newCommerceReferences);
      }
      return resultStruct;
    }
    // case 9-10,12
    return updateStruct(struct, false, newCommerceReferences, oldCommerceReferences);
  }

  private Struct updateStruct(Struct struct, Boolean inherit, List<String> newOriginReferences, List<String> newReferences) {
    StructBuilder builder = struct.builder().mode(LOOSE);
    builder.set(COMMERCE, getEmptyStruct());
    builder.enter(COMMERCE);

    // check what if catalogObjectIds = null
    builder.declareBoolean(INHERIT, inherit);
    builder.declareStrings(ORIGIN_REFERENCES, Integer.MAX_VALUE, newOriginReferences);
    builder.declareStrings(REFERENCES, Integer.MAX_VALUE, newReferences);

    return builder.build();
  }

  /**
   * Removes the commerce struct from the given @param#struct
   * @param struct the local settings struct
   * @return A struct with no commerce substruct
   */
  public static Struct removeCommerceSubstruct(Struct struct) {
    StructBuilder structBuilder = struct.builder().remove(COMMERCE);
    return structBuilder.build();
  }

  /**
   * Removes the catalog object data from the picture struct
   * @param content the image document
   * @return The updated struct
   */
  public Struct updateCMPictureOnBlobDelete(Content content) {
    if (content == null) {
      return null;
    }

    Struct struct = content.getStruct(LOCAL_SETTINGS);
    if (struct == null) {
      return null;
    }

    if(assetReadSettingsHelper.hasCommerceStruct(content.getProperties())) {
      if (assetReadSettingsHelper.readInheritedField(content.getProperties())) {
        struct = updateStruct(struct, false, Collections.<String>emptyList(), Collections.<String>emptyList());
      }
    }
    return struct;
  }

  private Struct getEmptyStruct() {
    return contentRepository.getConnection().getStructService().createStructBuilder().build();
  }

  public static Content getDefaultPicture(@Nonnull Site site, SettingsService settingsService) {
    return settingsService.setting(CONFIG_KEY_DEFAULT_PICTURE, Content.class, site.getSiteRootDocument());
  }

  @Required
  public void setContentRepository(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }

  @Required
  public void setAssetReadSettingsHelper(AssetReadSettingsHelper assetReadSettingsHelper) {
    this.assetReadSettingsHelper = assetReadSettingsHelper;
  }
}
