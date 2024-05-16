package com.coremedia.blueprint.cae.config;

import com.coremedia.blueprint.cae.layout.ContentBeanBackedPageGridPlacement;
import com.coremedia.blueprint.cae.view.DynamicInclude;
import com.coremedia.blueprint.common.contentbeans.Page;
import com.coremedia.dispatch.Type;
import com.coremedia.dispatch.Types;
import com.google.common.annotations.VisibleForTesting;
import edu.umd.cs.findbugs.annotations.DefaultAnnotation;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Arrays;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Helper class to handle types of beans for configuration lookup.
 */
@DefaultAnnotation(NonNull.class)
class BlueprintCacheConfigurationTypesFactory implements Function<Object, Stream<Type>> {

  @Override
  public Stream<Type> apply(Object obj) {
    // ignore virtual DynamicInclude that may be provided to us by
    // com.coremedia.blueprint.cae.view.DynamicIncludeHelper.createDynamicIncludeRootDelegateModelAndView
    Object bean = obj instanceof DynamicInclude ? ((DynamicInclude) obj).getDelegate() : obj;
    if (bean instanceof Page) {
      Object content = ((Page) bean).getContent();
      // consider the page's content first
      return streamTypesOf(content, bean);
    } else if (bean instanceof ContentBeanBackedPageGridPlacement) {
      ContentBeanBackedPageGridPlacement placement = (ContentBeanBackedPageGridPlacement) bean;
      // consider the placement's navigation first
      return streamTypesOf(placement.getNavigation(), bean);
    }
    return streamTypesOf(bean);
  }

  @VisibleForTesting
  static Stream<Type> streamTypesOf(@NonNull Object... beans) {
    return Arrays.stream(beans).map(Types::getTypeOf);
  }

}
