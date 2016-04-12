package com.coremedia.blueprint.cae.web.i18n;

import com.coremedia.blueprint.base.util.ObjectCacheKey;
import com.coremedia.blueprint.base.util.StructUtil;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.navigation.Navigation;
import com.coremedia.cache.Cache;
import com.coremedia.cap.common.CapStructHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class LinklistPageResourceBundleFactory implements PageResourceBundleFactory {
  private static final Logger LOG = LoggerFactory.getLogger(LinklistPageResourceBundleFactory.class);

  private static final String BUNDLES_PROPERTY = "resourceBundles";  // CMLinkable#resourceBundles
  private static final String STRUCT_PROPERTY = "settings";  // CMSettings#settings

  private Cache cache = null;


  // --- configure --------------------------------------------------

  /**
   * Usage of a cache is strongly recommended for production use.
   */
  public void setCache(Cache cache) {
    this.cache = cache;
  }


  // --- PageResourceBundleFactory ----------------------------------

  @Override
  public ResourceBundle resourceBundle(Page page) {
    // For performance and cache size reasons this implementation supports
    // resource bundles only for the page's navigation.  If you really need
    // resource bundles at content level, you can include the page content's
    // resourceBundles here.
    return resourceBundle(page.getNavigation());
  }

  @Override
  public ResourceBundle resourceBundle(Navigation navigation) {
    if (cache == null) {
      LOG.warn("Using " + getClass().getName() + " without cache.  Ok for testing, too expensive for production.");
      return fetchNavigationResourceBundle(navigation);
    } else {
      return cache.get(new NavigationCacheKey(navigation));
    }
  }


  // --- internal ---------------------------------------------------

  private ResourceBundle fetchNavigationResourceBundle(Navigation navigation) {
    Struct struct = hierarchicalMergedResourceBundles(navigation);
    return struct==null ? EmptyResourceBundle.emptyResourceBundle() : CapStructHelper.asResourceBundle(struct);
  }

  private Struct hierarchicalMergedResourceBundles(Navigation navigation) {
    Struct bundle = null;
    while (navigation!=null) {
      if (navigation instanceof CMNavigation) {
        Content navContent = ((CMNavigation)navigation).getContent();
        bundle = StructUtil.mergeStructs(bundle, mergedResourceBundles(navContent));
      }
      navigation = navigation.getParentNavigation();
    }
    return bundle;
  }

  private static Struct mergedResourceBundles(Content linkable) {
    List<Content> bundles = linkable.getLinks(BUNDLES_PROPERTY);
    List<Struct> structs = new ArrayList<>();
    for (Content bundle : bundles) {
      structs.add(bundle.getStruct(STRUCT_PROPERTY));
    }
    return StructUtil.mergeStructList(structs);
  }


  // --- caching ----------------------------------------------------

  private class NavigationCacheKey extends ObjectCacheKey<Navigation, ResourceBundle> {
    public NavigationCacheKey(Navigation navigation) {
      super(navigation);
    }

    @Override
    public ResourceBundle evaluate(Cache cache) throws Exception {
      return fetchNavigationResourceBundle(getObj());
    }
  }
}
