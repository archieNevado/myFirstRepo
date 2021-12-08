import ContentLocalizationUtil from "@coremedia/studio-client.cap-base-models/content/ContentLocalizationUtil";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import OpenDialogAction from "@coremedia/studio-client.ext.ui-components/actions/OpenDialogAction";
import Item from "@jangaroo/ext-ts/menu/Item";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CreateFromTemplateDialog from "./CreateFromTemplateDialog";
import CreateFromTemplateStudioPluginSettings_properties from "./CreateFromTemplateStudioPluginSettings_properties";
import CreateFromTemplateStudioPlugin_properties from "./CreateFromTemplateStudioPlugin_properties";

interface CreateFromTemplateMenuItemConfig extends Config<Item>, Partial<Pick<CreateFromTemplateMenuItem,
  "bindTo" |
  "propertyName"
>> {
}

class CreateFromTemplateMenuItem extends Item {
  declare Config: CreateFromTemplateMenuItemConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.template.config.createFromTemplateMenuItem";

  constructor(config: Config<CreateFromTemplateMenuItem> = null) {
    super((()=> ConfigUtils.apply(Config(CreateFromTemplateMenuItem, {
      itemId: "createFromTemplate",

      baseAction: new OpenDialogAction({
        iconCls: ContentLocalizationUtil.getIconStyleClassForContentTypeName(CreateFromTemplateStudioPluginSettings_properties.doctype),
        toggleDialog: true,
        text: CreateFromTemplateStudioPlugin_properties.text,
        dialog: Config(CreateFromTemplateDialog, { positionFun: bind(this, this.getPosition) }),
      }),

    }), config))());
  }

  /**
   * Contains the active content.
   */
  bindTo: ValueExpression = null;

  /** The content property name of the list to bind the newly created content to. */
  propertyName: string = null;
}

export default CreateFromTemplateMenuItem;
