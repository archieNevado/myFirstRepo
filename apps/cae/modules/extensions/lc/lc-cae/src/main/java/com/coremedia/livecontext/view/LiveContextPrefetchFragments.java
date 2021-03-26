package com.coremedia.livecontext.view;

import com.coremedia.blueprint.base.settings.SettingsProxy;
import com.coremedia.blueprint.base.settings.SettingsService;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;

interface LiveContextPrefetchFragments {

  @Nullable
  @SettingsProxy(target = SettingsProxy.Target.SETTING)
  LiveContextPlacementViews getPlacementViews();

  @Nullable
  @SettingsProxy(target = SettingsProxy.Target.SETTING)
  LiveContextPrefetchedViews getPrefetchedViews();

  @NonNull
  static <T> List<T> toProxyList(@Nullable List<?> beans, Class<T> clazz, SettingsService settingsService) {
    if (beans == null) {
      return List.of();
    }
    return beans.stream()
            .map(bean -> settingsService.createProxy(clazz, bean))
            .collect(Collectors.toList());
  }

}
