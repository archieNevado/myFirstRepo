import CategoryDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/CategoryDocumentForm";
import TaxonomyPropertyField from "@coremedia-blueprint/studio-client.main.taxonomy-studio/taxonomy/selection/TaxonomyPropertyField";
import AdvancedFieldContainer from "@coremedia/studio-client.ext.ui-components/components/AdvancedFieldContainer";
import ButtonSkin from "@coremedia/studio-client.ext.ui-components/skins/ButtonSkin";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import DatePropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/DatePropertyField";
import StringListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringListPropertyField";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import BindDisablePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindDisablePlugin";
import SetPropertyLabelPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/SetPropertyLabelPlugin";
import Button from "@jangaroo/ext-ts/button/Button";
import Container from "@jangaroo/ext-ts/container/Container";
import ColumnLayout from "@jangaroo/ext-ts/layout/container/Column";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AMStudioPlugin_properties from "../AMStudioPlugin_properties";
import AssetConstants from "../AssetConstants";
import AssetManagementConfigurationUtil from "../AssetManagementConfigurationUtil";
import StringListCheckboxPropertyField from "./StringListCheckboxPropertyField";

interface AMAssetMetaDataTabConfig extends Config<DocumentForm> {
}

class AMAssetMetaDataTab extends DocumentForm {
  declare Config: AMAssetMetaDataTabConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.assets.studio.config.amAssetMetaDataTab";

  constructor(config: Config<AMAssetMetaDataTab> = null) {
    super(ConfigUtils.apply(Config(AMAssetMetaDataTab, {
      title: AMStudioPlugin_properties.Tab_metadata_title,

      items: [
        Config(CategoryDocumentForm, { bindTo: config.bindTo }),
        Config(PropertyFieldGroup, {
          title: AMStudioPlugin_properties.PropertyGroup_categories_label,
          bindTo: config.bindTo,
          expandOnValues: AssetConstants.PROPERTY_ASSET_ASSETTAXONOMY,
          itemId: "assetTaxonomyForm",
          collapsed: true,
          items: [
            Config(TaxonomyPropertyField, {
              itemId: "assetTaxonomyItemId",
              bindTo: config.bindTo,
              hideLabel: true,
              forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
              propertyName: AssetConstants.PROPERTY_ASSET_ASSETTAXONOMY,
              taxonomyId: AssetConstants.ASSET_TAXONOMY_ID,
            }),
          ],
        }),

        Config(PropertyFieldGroup, {
          title: AMStudioPlugin_properties.PropertyGroup_rights_label,
          itemId: "rightsForm",
          propertyNames: [],
          expandOnValues: AssetConstants.PROPERTY_ASSET_METADATA + "." + AssetConstants.PROPERTY_ASSET_METADATA_CHANNELS + ","
                                   + AssetConstants.PROPERTY_ASSET_METADATA + "." + AssetConstants.PROPERTY_ASSET_METADATA_REGIONS + ","
                                   + AssetConstants.PROPERTY_ASSET_METADATA + "." + AssetConstants.PROPERTY_ASSET_METADATA_EXPIRATIONDATE + ","
                                   + AssetConstants.PROPERTY_ASSET_METADATA + "." + AssetConstants.PROPERTY_ASSET_METADATA_COPYRIGHT,
          items: [
            Config(Container, {
              layout: Config(ColumnLayout),
              items: [
                Config(StringListCheckboxPropertyField, {
                  bindTo: config.bindTo,
                  structName: AssetConstants.PROPERTY_ASSET_METADATA,
                  propertyName: AssetConstants.PROPERTY_ASSET_METADATA_CHANNELS,
                  availableValuesValueExpression: AssetManagementConfigurationUtil.getConfiguredRightsChannelsValueExpression(),
                  forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                  columnWidth: 0.5,
                }),
                Config(StringListCheckboxPropertyField, {
                  bindTo: config.bindTo,
                  structName: AssetConstants.PROPERTY_ASSET_METADATA,
                  propertyName: AssetConstants.PROPERTY_ASSET_METADATA_REGIONS,
                  availableValuesValueExpression: AssetManagementConfigurationUtil.getConfiguredRightsRegionsValueExpression(),
                  forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                  columnWidth: 0.5,
                }),
              ],
            }),

            Config(AdvancedFieldContainer, {
              labelAlign: "top",
              labelSeparator: "",
              defaultField: ":first",
              layout: Config(HBoxLayout),
              plugins: [
                Config(SetPropertyLabelPlugin, {
                  bindTo: config.bindTo,
                  propertyName: AssetConstants.PROPERTY_ASSET_METADATA + "." + AssetConstants.PROPERTY_ASSET_METADATA_EXPIRATIONDATE,
                }),
              ],
              items: [
                Config(DatePropertyField, {
                  bindTo: config.bindTo,
                  propertyName: AssetConstants.PROPERTY_ASSET_METADATA + "." + AssetConstants.PROPERTY_ASSET_METADATA_EXPIRATIONDATE,
                  format: AMStudioPlugin_properties.ExpirationDate_dateFormat,
                  anchor: ConfigUtils.asString(null),
                  forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                  width: 150,
                  hideLabel: true,
                  writeCalendar: true,
                }),
                Config(Button, {
                  itemId: "reset",
                  ui: ButtonSkin.SIMPLE.getSkin(),
                  handler: (): void => {
                    config.bindTo.extendBy("properties", AssetConstants.PROPERTY_ASSET_METADATA, AssetConstants.PROPERTY_ASSET_METADATA_EXPIRATIONDATE).setValue(null);
                  },
                  text: "Reset",
                  plugins: [
                    Config(BindDisablePlugin, {
                      forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                      bindTo: config.bindTo,
                    }),
                  ],
                }),
              ],
            }),

            Config(StringPropertyField, {
              propertyName: AssetConstants.PROPERTY_ASSET_METADATA + "." + AssetConstants.PROPERTY_ASSET_METADATA_COPYRIGHT,
              ifUndefined: "",
            }),
          ],
        }),

        Config(PropertyFieldGroup, {
          title: AMStudioPlugin_properties.PropertyGroup_product_codes_label,
          itemId: "productCodesPropertyForm",
          items: [
            Config(StringListPropertyField, {
              propertyName: AssetConstants.PROPERTY_ASSET_METADATA + "." +
                                                     AssetConstants.PROPERTY_ASSET_METADATA_PRODUCT_CODES,
              textFieldEmptyText: AMStudioPlugin_properties.PropertyGroup_product_codes_textfield_empty_text,
              forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
            }),
          ],
        }),
      ],

    }), config));
  }
}

export default AMAssetMetaDataTab;
