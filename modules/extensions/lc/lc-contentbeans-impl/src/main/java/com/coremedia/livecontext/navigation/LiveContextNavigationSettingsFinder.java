package com.coremedia.livecontext.navigation;

import com.coremedia.blueprint.base.settings.SettingsFinder;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.blueprint.base.settings.impl.CMLinkableSettingsFinder;
import com.coremedia.blueprint.base.util.StructUtil;
import com.coremedia.blueprint.common.navigation.Linkable;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.context.LiveContextNavigation;
import com.coremedia.objectserver.dataviews.DataViewHelper;
import com.google.common.base.Function;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class LiveContextNavigationSettingsFinder implements SettingsFinder {
  private LiveContextNavigationTreeRelation treeRelation;
  private CMLinkableSettingsFinder delegate;
  private Cache cache;

  // --- configure --------------------------------------------------

  @Required
  public void setTreeRelation(LiveContextNavigationTreeRelation treeRelation) {
    this.treeRelation = treeRelation;
  }


  // --- SettingsFinder ---------------------------------------------

  @Override
  public Object setting(Object bean, final String name, final SettingsService settingsService) {
    if (!(bean instanceof LiveContextNavigation)) {
      return null;
    }

    final LiveContextNavigation lcn = (LiveContextNavigation) bean;
    Object setting = delegate.setting(lcn.getContext().getContent(), name, settingsService);
    if (setting != null) {
      if (setting instanceof Struct) {
        return cache != null ?
                cache.get(new MergedStructCacheKey(DataViewHelper.getOriginal(lcn), name, settingsService)) :
                getMergedStructUncached(lcn, name, settingsService);
      }
      return setting;
    }

    // get setting of parent navigation, since a LiveContextCategoryNavigation
    // has no settings of its own.
    Linkable parent = getParent(lcn);
    return parent != null ? settingsService.setting(name, Object.class, parent) : null;
  }

  private Struct getMergedStructUncached(LiveContextNavigation lcn, String propertyName, SettingsService settingsService) {
    List<Linkable> pathToRoot = treeRelation.pathToRoot(lcn);
    Collections.reverse(pathToRoot);
    Iterable<Struct> transform = Iterables.transform(pathToRoot, new GetStructSetting(propertyName, settingsService));
    Iterable<Struct> settingsStructs = Iterables.filter(transform, Predicates.notNull());
    return StructUtil.mergeStructs(Iterables.toArray(settingsStructs, Struct.class));
  }

  private Linkable getParent(LiveContextNavigation lcn) {
    Linkable parent;
    if (lcn.isRoot()) {
      //find settings below the root channel of the site as fallback for the root catalog node
      parent = lcn.getRootNavigation();
    } else {
      parent = treeRelation.getParentOf(lcn);
    }
    return parent;
  }

  @Required
  public void setDelegate(CMLinkableSettingsFinder delegate) {
    this.delegate = delegate;
  }

  @Autowired(required = false)
  public void setCache(Cache cache) {
    this.cache = cache;
  }

  private class GetStructSetting implements Function<Linkable, Struct> {
    private final String name;
    private final SettingsService settingsService;

    public GetStructSetting(String name, SettingsService settingsService) {
      this.name = name;
      this.settingsService = settingsService;
    }

    @Nullable
    @Override
    public Struct apply(Linkable linkable) {
      if (linkable instanceof Navigation) {
        Object setting = delegate.setting(((Navigation) linkable).getContext().getContent(), name, settingsService);
        if (setting instanceof Struct) {
          return (Struct) setting;
        }
      }
      return null;
    }
  }

  private class MergedStructCacheKey extends CacheKey<Struct> {
    LiveContextNavigation lcn;
    String name;
    SettingsService settingsService;

    public MergedStructCacheKey(LiveContextNavigation lcn, String name, SettingsService settingsService) {
      this.lcn = lcn;
      this.name = name;
      this.settingsService = settingsService;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }

      MergedStructCacheKey that = (MergedStructCacheKey) o;

      if (lcn != null ? !lcn.equals(that.lcn) : that.lcn != null) {
        return false;
      }
      if (name != null ? !name.equals(that.name) : that.name != null) {
        return false;
      }
      return !(settingsService != null ? !settingsService.equals(that.settingsService) : that.settingsService != null);

    }

    @Override
    public int hashCode() {
      int result = lcn != null ? lcn.hashCode() : 0;
      result = 31 * result + (name != null ? name.hashCode() : 0);
      result = 31 * result + (settingsService != null ? settingsService.hashCode() : 0);
      return result;
    }

    @Override
    public Struct evaluate(Cache cache) throws Exception {
      return getMergedStructUncached(lcn, name, settingsService);
    }
  }
}
