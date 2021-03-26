package com.coremedia.livecontext.view;

import com.coremedia.dispatch.NoArgDispatcher;
import com.coremedia.dispatch.Type;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;

import java.util.List;
import java.util.Map;

@DefaultAnnotation(NonNull.class)
class PrefetchConfigContentTypeDispatcher extends NoArgDispatcher {
  private final Map<String, List<String>> configuredContentTypes;

  PrefetchConfigContentTypeDispatcher(Map<String, List<String>> configuredContentTypes) {
    this.configuredContentTypes = configuredContentTypes;
  }

  @Nullable
  @Override
  protected Object doLookup(String typeName) {
    return configuredContentTypes.get(typeName);
  }

  public List<String> lookupPredefinedViews(Type type) {
    //noinspection unchecked
    return (List<String>) super.lookup(type);
  }
}
