package com.coremedia.livecontext.view;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.struct.Struct;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;

import static com.coremedia.livecontext.view.LiveContextPrefetchFragments.toProxyList;

interface LiveContextPrefetchedViews {

  @Nullable
  List<String> getDefaults();

  @Nullable
  List<Struct> getContentTypes();

  @NonNull
  static List<LiveContextContentTypeView> contentTypes(LiveContextPrefetchedViews views, SettingsService settingsService) {
    List<Struct> contentTypes = views.getContentTypes();
    return toProxyList(contentTypes, LiveContextContentTypeView.class, settingsService);
  }

  @Nullable
  List<Struct> getLayouts();

  @NonNull
  static List<LiveContextPrefetchedViewLayout> layouts(LiveContextPrefetchedViews views, SettingsService settingsService) {
    List<Struct> layouts = views.getLayouts();
    return toProxyList(layouts, LiveContextPrefetchedViewLayout.class, settingsService);
  }

}
