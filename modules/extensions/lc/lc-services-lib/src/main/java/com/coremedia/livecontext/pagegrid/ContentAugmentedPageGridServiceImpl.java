package com.coremedia.livecontext.pagegrid;

import com.coremedia.blueprint.base.pagegrid.impl.ContentBackedPageGridServiceImpl;
import com.coremedia.cap.content.Content;

/**
 * PageGridService merges content backed pageGrids along an external category hierarchy.
 */
public class ContentAugmentedPageGridServiceImpl extends ContentBackedPageGridServiceImpl<Content> {
  private static final String CM_EXTERNAL_CHANNEL = "CMExternalChannel";

  @Override
  protected Content getParentOf(Content content) {
    if (content == null || !(content.getType().isSubtypeOf(CM_EXTERNAL_CHANNEL))) {
      return null;
    }
    return getTreeRelation().getParentOf(content);
  }
}
