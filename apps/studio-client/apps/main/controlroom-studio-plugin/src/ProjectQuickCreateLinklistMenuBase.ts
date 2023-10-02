import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ProjectContentToolbar from "@coremedia/studio-client.main.control-room-editor-components/project/components/ProjectContentToolbar";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import QuickCreateLinklistMenuBase from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/QuickCreateLinklistMenuBase";
import { as } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";

interface ProjectQuickCreateLinklistMenuBaseConfig extends Config<QuickCreateLinklistMenuBase> {
}

class ProjectQuickCreateLinklistMenuBase extends QuickCreateLinklistMenuBase {
  declare Config: ProjectQuickCreateLinklistMenuBaseConfig;

  constructor(config: Config<ProjectQuickCreateLinklistMenuBase> = null) {
    super(config);
  }

  protected updateProject(content: Content): void {
    const projectContentToolbarCmp = as(this.findParentByType(ProjectContentToolbar.xtype), ProjectContentToolbar);
    if (projectContentToolbarCmp) {
      // TODO: Quick fix, needs better solution
      projectContentToolbarCmp.project.addContents([content]);
    }
    editorContext._.getContentTabManager().openDocument(content);
  }
}

export default ProjectQuickCreateLinklistMenuBase;
