import Validators_properties from "@coremedia/studio-client.ext.errors-validation-components/validation/Validators_properties";
import CopyResourceBundleProperties from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import BlueprintValidators_properties from "./BlueprintValidators_properties";

interface ValidatorsStudioPluginConfig extends Config<StudioPlugin> {
}

class ValidatorsStudioPlugin extends StudioPlugin {
  declare Config: ValidatorsStudioPluginConfig;

  static readonly xtype: string = "com.coremedia.blueprint.validators.studio.config.validatorsStudioPlugin";

  constructor(config: Config<ValidatorsStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(ValidatorsStudioPlugin, {

      configuration: [
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, Validators_properties),
          source: resourceManager.getResourceBundle(null, BlueprintValidators_properties),
        }),
      ],

    }), config));
  }
}

export default ValidatorsStudioPlugin;
