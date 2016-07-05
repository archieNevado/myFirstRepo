package com.coremedia.blueprint.common.layout;

import com.coremedia.blueprint.common.contentbeans.CMNavigation;

import javax.annotation.Nonnull;

public interface PageGridService {

  /**
   * Returns a PageGrid for the given navigation context
   */
  @Nonnull
  PageGrid getContentBackedPageGrid(CMNavigation navigationContext);
}
