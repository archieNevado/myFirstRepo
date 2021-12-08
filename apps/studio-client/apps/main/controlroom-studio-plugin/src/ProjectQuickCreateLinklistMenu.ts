import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import Menu from "@jangaroo/ext-ts/menu/Menu";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ProjectQuickCreateLinklistMenuBase from "./ProjectQuickCreateLinklistMenuBase";
import ProjectStudioPluginSettings_properties from "./ProjectStudioPluginSettings_properties";
import ProjectStudioPlugin_properties from "./ProjectStudioPlugin_properties";

interface ProjectQuickCreateLinklistMenuConfig extends Config<ProjectQuickCreateLinklistMenuBase> {
}

class ProjectQuickCreateLinklistMenu extends ProjectQuickCreateLinklistMenuBase {
  declare Config: ProjectQuickCreateLinklistMenuConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.controlroom.projectQuickCreateLinklistMenu";

  constructor(config: Config<ProjectQuickCreateLinklistMenu> = null) {
    super((()=> ConfigUtils.apply(Config(ProjectQuickCreateLinklistMenu, {
      text: ProjectStudioPlugin_properties.Project_create_content_tooltip,
      tooltip: ProjectStudioPlugin_properties.Project_create_content_tooltip,
      iconCls: CoreIcons_properties.create_content,
      onSuccess: bind(this, this.updateProject),
      contentTypes: ProjectStudioPluginSettings_properties.default_project_content_quickcreate_contentTypes,

      menu: Config(Menu, {
        items: [
        /*Menu Items will be added here.*/
        ],
      }),

    }), config))());
  }
}

export default ProjectQuickCreateLinklistMenu;
