package com.coremedia.livecontext.view;

import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.blueprint.common.services.context.ContextHelper;
import com.coremedia.cache.Cache;
import com.coremedia.cache.CacheKey;
import com.coremedia.dispatch.NoArgDispatcher;
import com.coremedia.dispatch.Type;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;

class PrefetchConfigContentTypeDispatcher extends NoArgDispatcher {
  private final PrefetchFragmentsConfigReader prefetchFragmentsConfigReader;

  PrefetchConfigContentTypeDispatcher(@NonNull PrefetchFragmentsConfigReader prefetchFragmentsConfigReader) {
    this.prefetchFragmentsConfigReader = prefetchFragmentsConfigReader;
  }

  private List<String> configuredContentTypes = emptyList();

  @Nullable
  @Override
  protected Object doLookup(String typeName) {
    return configuredContentTypes.contains(typeName) ? typeName : null;
  }

  @Nullable
  @Override
  public Object lookup(@NonNull Type type) {
    updateConfiguredContentTypesAndCaches();
    return super.lookup(type);
  }

  void updateConfiguredContentTypesAndCaches() {
    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
    if (requestAttributes != null) {
      Object pageAttribute = requestAttributes.getAttribute(ContextHelper.ATTR_NAME_PAGE, 0);
      if (pageAttribute instanceof Page) {
        //read configured types from cache
        configuredContentTypes = prefetchFragmentsConfigReader.getCache() != null ?
                prefetchFragmentsConfigReader.getCache().get(new ConfiguredContentTypesCacheKey((Page) pageAttribute)) :
                prefetchFragmentsConfigReader.getConfiguredContentTypes((Page) pageAttribute);
      }
    }
  }

  class ConfiguredContentTypesCacheKey extends CacheKey<List<String>> {
    private Page page;

    ConfiguredContentTypesCacheKey(Page page) {
      this.page = page;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      ConfiguredContentTypesCacheKey that = (ConfiguredContentTypesCacheKey) o;
      return Objects.equals(page, that.page);
    }

    @Override
    public int hashCode() {
      return Objects.hash(page);
    }

    @Override
    public List<String> evaluate(Cache cache) throws Exception {
      //reset lookup cache of NoArgDispatcher when settings changed
      reset();
      return prefetchFragmentsConfigReader.getConfiguredContentTypes(page);
    }
  }
}
