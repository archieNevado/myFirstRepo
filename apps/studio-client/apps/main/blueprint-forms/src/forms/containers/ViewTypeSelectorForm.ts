import ViewtypePropertyField from "@coremedia/studio-client.main.bpbase-studio-components/viewtypes/ViewtypePropertyField";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import FitLayout from "@jangaroo/ext-ts/layout/container/Fit";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../../BlueprintDocumentTypes_properties";

interface ViewTypeSelectorFormConfig extends Config<PropertyFieldGroup>, Partial<Pick<ViewTypeSelectorForm,
  "paths" |
  "hideForSingleChoice"
>> {
}

class ViewTypeSelectorForm extends PropertyFieldGroup {
  declare Config: ViewTypeSelectorFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.viewTypeSelectorForm";

  constructor(config: Config<ViewTypeSelectorForm> = null) {
    config = ConfigUtils.apply({ hideForSingleChoice: true }, config);
    super(ConfigUtils.apply(Config(ViewTypeSelectorForm, {
      itemId: "viewTypeSelectorForm",
      title: BlueprintDocumentTypes_properties.CMLinkable_viewtype_text,
      collapsed: config.collapsed || true,
      itemsLazyUntilEvent: "beforerender",

      items: [
        Config(ViewtypePropertyField, {
          hideLabel: true,
          hideForSingleChoice: config.hideForSingleChoice,
          paths: config.paths,
        }),
      ],
      layout: Config(FitLayout),

    }), config));
  }

  /** Optional param to add folder for a static viewtype lookup (without adding the content type). */
  paths: Array<any> = null;

  /** If the view type combobox only contains 1x entry, the combo box will be hidden */
  hideForSingleChoice: boolean = false;
}

export default ViewTypeSelectorForm;
