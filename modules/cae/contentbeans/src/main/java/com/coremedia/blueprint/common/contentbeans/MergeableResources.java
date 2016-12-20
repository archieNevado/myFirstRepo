package com.coremedia.blueprint.common.contentbeans;

import com.coremedia.blueprint.coderesources.CodeResourcesModel;

import java.util.List;

/**
 * A list of code resources that may be merged/minified by the renderer.
 * <p>
 * A MergeableResources object is backed by a CodeResourcesModel.  We could
 * have hidden this implementation detail by declaring/delegating the features
 * relevant for link building (five out of six), but for now that seems
 * overengineered.
 * <p>
 * MergeableResources are renderable (s. MergeableResourcesView) and linkable
 * (s. CodeResourceHandler).
 */
public interface MergeableResources {
  /**
   * For link building.
   * <p>
   * Since we support only one set of mergeable resources per
   * CodeResourcesModel, you can build a MergeableResources link
   * uniquely from the CodeResourcesModel data.
   *
   * @return the underlying CodeResourcesModel
   */
  CodeResourcesModel getCodeResourceModel();

  /**
   * For rendering.
   *
   * @return the single resources that may be merged
   */
  List<CMAbstractCode> getMergeableResources();
}
