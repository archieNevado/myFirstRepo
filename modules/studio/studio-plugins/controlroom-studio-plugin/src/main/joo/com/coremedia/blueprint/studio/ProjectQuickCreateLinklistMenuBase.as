package com.coremedia.blueprint.studio {

import com.coremedia.blueprint.base.components.quickcreate.QuickCreateLinklistMenuBase;
import com.coremedia.blueprint.studio.config.controlroom.projectQuickCreateLinklistMenu;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.controlroom.config.projectContentToolbar;
import com.coremedia.cms.editor.controlroom.project.components.ProjectContentToolbar;
import com.coremedia.cms.editor.sdk.editorContext;

public class ProjectQuickCreateLinklistMenuBase extends QuickCreateLinklistMenuBase {

  public function ProjectQuickCreateLinklistMenuBase(config:projectQuickCreateLinklistMenu = null) {
    super(config);
  }

  protected function updateProject(content:Content):void {
    var projectContentToolbarCmp:ProjectContentToolbar = findParentByType(projectContentToolbar.xtype) as ProjectContentToolbar;
    if (projectContentToolbarCmp) {
      // TODO: Quick fix, needs better solution
      projectContentToolbarCmp.initialConfig.project.addContents([content]);
    }
    editorContext.getContentTabManager().openDocument(content);
  }
}
}
