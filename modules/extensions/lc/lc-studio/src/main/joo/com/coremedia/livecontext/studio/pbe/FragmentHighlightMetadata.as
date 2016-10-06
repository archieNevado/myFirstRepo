package com.coremedia.livecontext.studio.pbe {
import com.coremedia.cms.editor.sdk.preview.metadata.IMetadataService;
import com.coremedia.cms.editor.sdk.preview.metadata.MetadataTree;
import com.coremedia.cms.editor.sdk.preview.metadata.MetadataTreeNode;

public class FragmentHighlightMetadata {
  private var metadataService:IMetadataService;

  public function FragmentHighlightMetadata(metadataService:IMetadataService) {
    this.metadataService = metadataService;
  }

  public function previewContainsFragmentMetadata():Boolean {
    var metadataTree:MetadataTree = metadataService.getMetadataTree();
    if (!metadataTree) {
      return undefined;
    }

    var root:MetadataTreeNode = metadataTree.getRoot();
    if (!root) {
      return undefined;
    }

    var fragmentsOnPage:int = root.findChildrenBy(selectIsFragmentRequest).length;
    var contains:Boolean = fragmentsOnPage !== 0;
    return contains;
  }

  private function selectIsFragmentRequest(node:MetadataTreeNode):Boolean {
    var property:Array = node.getProperty("fragmentRequest") as Array;
    if (property !== null && property.length > 0) {
      return true;
    }
    return false;
  }
}
}