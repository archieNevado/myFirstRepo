import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import IEditorContext from "@coremedia/studio-client.main.editor-components/sdk/IEditorContext";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TaxonomyPreferencesBase from "./TaxonomyPreferencesBase";

interface AddTaggingStrategyPluginConfig extends Config<StudioPlugin>, Partial<Pick<AddTaggingStrategyPlugin,
  "serviceId" |
  "label"
>> {
}

/**
 *
 * This plugin offers the possibility add tagging strategies to the Preferences dialog.
 *
 * @public
 */
class AddTaggingStrategyPlugin extends StudioPlugin {
  declare Config: AddTaggingStrategyPluginConfig;

  constructor(config: Config<AddTaggingStrategyPlugin> = null) {
    super(ConfigUtils.apply(Config(AddTaggingStrategyPlugin), config));
    TaxonomyPreferencesBase.addTaggingStrategy(config.serviceId, config.label);
  }

  /**
   * The serviceId of the tagging strategy, must match the name of the Spring bean instance.
   */
  serviceId: string = null;

  /**
   * The label of the tagging strategy, will be shown in the taxonomy tagging strategy combo box
   */
  label: string = null;

  override init(editorContext: IEditorContext): void {
    super.init(editorContext);
  }
}

export default AddTaggingStrategyPlugin;
