package com.coremedia.livecontext.view;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.struct.Struct;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;

import static com.coremedia.livecontext.view.LiveContextPrefetchFragments.toProxyList;

interface LiveContextPlacementViews {

  @Nullable
  List<Struct> getLayouts();

  @NonNull
  static List<LiveContextPlacementViewLayout> layouts(LiveContextPlacementViews views, SettingsService settingsService) {
    List<Struct> layouts = views.getLayouts();
    return toProxyList(layouts, LiveContextPlacementViewLayout.class, settingsService);
  }

  @Nullable
  List<Struct> getDefaults();

  @NonNull
  static List<LiveContextPlacementView> defaults(LiveContextPlacementViews views, SettingsService settingsService) {
    List<Struct> defaults = views.getDefaults();
    return toProxyList(defaults, LiveContextPlacementView.class, settingsService);
  }

}
