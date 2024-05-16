package com.coremedia.livecontext.view;

import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;

import static com.coremedia.livecontext.view.LiveContextPrefetchFragments.toProxyList;

interface LiveContextPlacementViewLayout {

  @Nullable
  Content getLayout();

  @Nullable
  List<Struct> getPlacementViews();

  @NonNull
  static List<LiveContextPlacementView> placementViews(LiveContextPlacementViewLayout layout, SettingsService settingsService) {
    List<Struct> placementViews = layout.getPlacementViews();
    return toProxyList(placementViews, LiveContextPlacementView.class, settingsService);
  }
}
