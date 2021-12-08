import com_coremedia_ui_store_DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import ext_data_field_DataField from "@jangaroo/ext-ts/data/field/Field";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ThemeSelectorFormBase from "./ThemeSelectorFormBase";

interface ThemeSelectorFormConfig extends Config<ThemeSelectorFormBase>, Partial<Pick<ThemeSelectorForm,
  "themesFolderPaths"
>> {
}

class ThemeSelectorForm extends ThemeSelectorFormBase {
  declare Config: ThemeSelectorFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.themeSelectorForm";

  constructor(config: Config<ThemeSelectorForm> = null) {
    config = ConfigUtils.apply({ themesFolderPaths: ThemeSelectorFormBase.DEFAULT_PATHS }, config);
    super(ConfigUtils.apply(Config(ThemeSelectorForm, {
      displayField: ThemeSelectorFormBase.DISPLAY_FIELD_NAME,
      hideLabel: true,
      offeredContentsValueExpression: ThemeSelectorFormBase.createAvailableThemesValueExpression(config),
      comboBoxTemplate: ThemeSelectorFormBase.COMBO_BOX_TEMPLATE,
      displayTemplate: ThemeSelectorFormBase.DISPLAY_TEMPLATE,

      fields: [
        Config(com_coremedia_ui_store_DataField, {
          name: ThemeSelectorFormBase.DISPLAY_FIELD_NAME,
          encode: false,
          mapping: "",
          convert: ThemeSelectorFormBase.localizeText,
        }),
        Config(ext_data_field_DataField, {
          name: ThemeSelectorFormBase.TITLE_FIELD_NAME,
          mapping: "",
          convert: ThemeSelectorFormBase.localizeText,
        }),
        Config(ext_data_field_DataField, {
          name: ThemeSelectorFormBase.DESCRIPTION_FIELD_NAME,
          mapping: "",
          convert: ThemeSelectorFormBase.localizeDescription,
        }),
        Config(ext_data_field_DataField, {
          name: ThemeSelectorFormBase.THUMBNAIL_URI_FIELD_NAME,
          mapping: "",
          convert: ThemeSelectorFormBase.getThumbnailUri,
        }),
        Config(ext_data_field_DataField, {
          name: ThemeSelectorFormBase.THUMBNAIL_TOOLTIP_FIELD_NAME,
          mapping: "",
          convert: ThemeSelectorFormBase.getThumbnailTooltip,
        }),
      ],

    }), config));
  }

  /**
   * An array of strings that specify absolute paths to folders in the
   * repository where to look for themes (CMTheme documents).
   */
  themesFolderPaths: Array<any> = null;
}

export default ThemeSelectorForm;
