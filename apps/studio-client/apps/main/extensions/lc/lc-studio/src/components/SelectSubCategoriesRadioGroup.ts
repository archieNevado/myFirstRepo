import BoundRadioGroup from "@coremedia/studio-client.ext.ui-components/components/BoundRadioGroup";
import Radio from "@jangaroo/ext-ts/form/field/Radio";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";

interface SelectSubCategoriesRadioGroupConfig extends Config<BoundRadioGroup> {
}

class SelectSubCategoriesRadioGroup extends BoundRadioGroup {
  declare Config: SelectSubCategoriesRadioGroupConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.selectSubCategoriesRadioGroup";

  constructor(config: Config<SelectSubCategoriesRadioGroup> = null) {
    super(ConfigUtils.apply(Config(SelectSubCategoriesRadioGroup, {
      width: "auto",
      columns: 1,
      hideLabel: true,
      defaultValue: "inheritFromCatalogRadioButton",

      items: [
        Config(Radio, {
          itemId: "inheritFromCatalogRadioButton",
          boxLabel: LivecontextStudioPlugin_properties.Commerce_Child_Categories_inherit_label,
        }),
        Config(Radio, {
          itemId: "selectChildrenRadioButton",
          boxLabel: LivecontextStudioPlugin_properties.Commerce_Child_Categories_select_label,
        }),
      ],

    }), config));
  }
}

export default SelectSubCategoriesRadioGroup;
