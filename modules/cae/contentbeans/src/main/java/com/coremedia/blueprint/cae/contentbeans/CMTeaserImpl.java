package com.coremedia.blueprint.cae.contentbeans;

import com.coremedia.blueprint.common.contentbeans.CMLinkable;
import com.coremedia.blueprint.common.contentbeans.CMMedia;
import com.coremedia.blueprint.common.contentbeans.CMPicture;
import com.coremedia.blueprint.common.contentbeans.CMTeasable;
import com.coremedia.blueprint.common.cta.CallToActionButtonSettings;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cap.struct.StructBuilder;
import com.coremedia.cap.struct.StructBuilderMode;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Generated extension class for immutable beans of document type "CMTeaser".
 */
public class CMTeaserImpl extends CMTeaserBase {

  /*
   * Add additional methods here.
   * Add them to the interface {@link com.coremedia.blueprint.common.contentbeans.CMTeaser} to make them public.
   */

  @Override
  public Struct getLocalSettings() {
    Struct returnValue;

    Struct localSettings = super.getLocalSettings();
    CMLinkable target = getTarget();

    if (target == null || this.equals(target) /* avoid recursion and check for equality because identity does not work for data views*/) {
      //no target found, use local settings
      returnValue = localSettings;
    }
    else {
      //target found

      if(localSettings == null) {
        //no local settings, target should have settings
        returnValue = target.getLocalSettings();
      }
      else {
        //local settings found, merge with target settings if possible
        Struct targetSettings = target.getLocalSettings();

        if(targetSettings != null) {
          StructBuilder structBuilder = localSettings.builder();
          //tell structbuilder to allow merging of structs
          structBuilder = structBuilder.mode(StructBuilderMode.LOOSE);
          returnValue = structBuilder.defaultTo(targetSettings).build();
        }
        else {
          returnValue = localSettings;
        }
      }
    }

    return returnValue;
  }

  @Override
  public List<CMMedia> getMedia() {
    List<CMMedia> media = super.getMedia();
    if (media.isEmpty() && this.getTarget() instanceof CMTeasable && !this.getTarget().equals(this)) {
      media = ((CMTeasable) this.getTarget()).getMedia();
    }
    return media;
  }

  @Override
  public List<CallToActionButtonSettings> getCallToActionSettings() {
    Map<String, List<Map<String, Object>>> targets = getTargets();
    if (targets != null) {
      List<Map<String, Object>> links = targets.get("links");
      return links.stream()
              .map((Function<Map<String, Object>, CallToActionButtonSettings>) stringObjectMap -> {
                        ImmutableMap.Builder<String, Object> mapBuilder = ImmutableMap.builder();
                        CMLinkable target = getSettingsService().setting("target", CMLinkable.class, stringObjectMap);
                        //noinspection ConstantConditions non-null fallback provided
                        boolean enabled = getSettingsService().settingWithDefault(ANNOTATED_LINK_STRUCT_CTA_ENABLED_PROPERTY_NAME, boolean.class, false, stringObjectMap);
                        if (target != null && enabled) {
                          mapBuilder.put("target", target);
                          //noinspection ConstantConditions non-null fallback provided
                          mapBuilder.put("text", getSettingsService().settingWithDefault(ANNOTATED_LINK_STRUCT_CTA_CUSTOM_TEXT_PROPERTY_NAME, String.class, "", stringObjectMap));
                          //noinspection ConstantConditions non-null fallback provided
                          mapBuilder.put("openInNewTab", target.isOpenInNewTab());
                          mapBuilder.put("metadata", ImmutableList.of("properties." + TARGETS));
                          return getSettingsService().createProxy(CallToActionButtonSettings.class, mapBuilder.build());
                        }
                        return null;
                      }

              ).filter(Objects::nonNull).collect(ImmutableList.toImmutableList());
    }
    return ImmutableList.of();
  }
}
  
