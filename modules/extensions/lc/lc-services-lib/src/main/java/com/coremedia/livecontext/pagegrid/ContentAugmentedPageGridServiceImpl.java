package com.coremedia.livecontext.pagegrid;

import com.coremedia.blueprint.base.pagegrid.impl.ContentBackedPageGridServiceImpl;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.tree.ExternalChannelContentTreeRelation;
import org.springframework.beans.factory.annotation.Required;

/**
 * PageGridService merges content backed pageGrids along an external category hierarchy.
 */
public class ContentAugmentedPageGridServiceImpl extends ContentBackedPageGridServiceImpl<Content> {
  private static final String CM_EXTERNAL_CHANNEL = "CMExternalChannel";

  private ExternalChannelContentTreeRelation treeRelation;

  @Override
  protected Content getParentOf(Content content) {
    if (content == null || !(content.getType().isSubtypeOf(CM_EXTERNAL_CHANNEL))) {
      return null;
    }
    return treeRelation.getParentOf(content);
  }

  @Required
  public void setTreeRelation(ExternalChannelContentTreeRelation treeRelation) {
    this.treeRelation = treeRelation;
  }
}
