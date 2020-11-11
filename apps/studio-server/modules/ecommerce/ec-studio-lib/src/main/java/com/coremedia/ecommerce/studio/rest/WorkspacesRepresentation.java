package com.coremedia.ecommerce.studio.rest;

import com.coremedia.livecontext.ecommerce.workspace.Workspace;

import java.util.Collections;
import java.util.List;

/**
 * Workspaces representation for JSON.
 *
 * @deprecated This class is part of the commerce integration "workspaces support" that is not
 * supported by the Commerce Hub architecture. It will be removed or changed in the future.
 */
@Deprecated
public class WorkspacesRepresentation extends AbstractCatalogRepresentation {

  private List<Workspace> workspaces = Collections.emptyList();

  public List<Workspace> getWorkspaces() {
    return workspaces;
  }

  public void setWorkspaces(List<Workspace> workspaces) {
    this.workspaces = workspaces;
  }
}
