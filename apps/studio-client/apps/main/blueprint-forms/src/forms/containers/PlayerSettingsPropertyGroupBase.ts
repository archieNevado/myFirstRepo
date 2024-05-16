import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import Component from "@jangaroo/ext-ts/Component";
import CheckboxGroup from "@jangaroo/ext-ts/form/CheckboxGroup";
import { as } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import PlayerSettingsPropertyGroup from "./PlayerSettingsPropertyGroup";

interface PlayerSettingsPropertyGroupBaseConfig extends Config<PropertyFieldGroup> {
}

class PlayerSettingsPropertyGroupBase extends PropertyFieldGroup {
  declare Config: PlayerSettingsPropertyGroupBaseConfig;

  constructor(config: Config<PlayerSettingsPropertyGroup> = null) {
    // this needs to be done to prevent hidden items from being rendered.
    // Otherwise hidden checkboxes would still use space in a column of the checkboxGroup
    const checkboxGroup = as(config.items[0], CheckboxGroup);
    if (checkboxGroup && checkboxGroup.isInstance !== true && checkboxGroup.items && checkboxGroup.items.length > 0) {
      checkboxGroup.items = checkboxGroup.items.filter((cmp: Component): boolean =>
        !cmp.hidden,
      );
    }
    super(config);
  }
}

export default PlayerSettingsPropertyGroupBase;
