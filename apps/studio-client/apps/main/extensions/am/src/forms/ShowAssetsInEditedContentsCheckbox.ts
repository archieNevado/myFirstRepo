import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AMStudioPlugin_properties from "../AMStudioPlugin_properties";
import ShowAssetsInEditedContentsCheckboxBase from "./ShowAssetsInEditedContentsCheckboxBase";

interface ShowAssetsInEditedContentsCheckboxConfig extends Config<ShowAssetsInEditedContentsCheckboxBase> {
}

class ShowAssetsInEditedContentsCheckbox extends ShowAssetsInEditedContentsCheckboxBase {
  declare Config: ShowAssetsInEditedContentsCheckboxConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.assets.studio.config.showAssetsInEditedContentsCheckbox";

  constructor(config: Config<ShowAssetsInEditedContentsCheckbox> = null) {
    super((()=> ConfigUtils.apply(Config(ShowAssetsInEditedContentsCheckbox, {
      boxLabel: AMStudioPlugin_properties.EditedContents_showAssets_label,

      plugins: [
        Config(BindPropertyPlugin, {
          bindTo: this.getCheckedValueExpression(),
          componentProperty: "value",
          bidirectional: true,
        }),
      ],

    }), config))());
  }
}

export default ShowAssetsInEditedContentsCheckbox;
