import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import BindVisibilityPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindVisibilityPlugin";
import PropertyFieldPlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldPlugin";
import BindDisablePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindDisablePlugin";
import Checkbox from "@jangaroo/ext-ts/form/field/Checkbox";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AMStudioPlugin_properties from "../AMStudioPlugin_properties";
import AssetDetailsBlobPropertyFieldBase from "./AssetDetailsBlobPropertyFieldBase";

interface AssetDetailsBlobPropertyFieldConfig extends Config<AssetDetailsBlobPropertyFieldBase>, Partial<Pick<AssetDetailsBlobPropertyField,
  "visiblePropertyName"
>> {
}

/**
 * A BlobPropertyField with an additional checkbox, which marks a blob as downloadable.
 * This component displays the blob and its string properties with an hbox layout.
 */
class AssetDetailsBlobPropertyField extends AssetDetailsBlobPropertyFieldBase {
  declare Config: AssetDetailsBlobPropertyFieldConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.assets.studio.config.assetDetailsBlobPropertyField";

  constructor(config: Config<AssetDetailsBlobPropertyField> = null) {
    super((()=> ConfigUtils.apply(Config(AssetDetailsBlobPropertyField, {

      ...ConfigUtils.append({
        plugins: [
          Config(AddItemsPlugin, {
            applyTo: AssetDetailsBlobPropertyFieldBase.findBlobDetailsContainer,
            items: [
              Config(Checkbox, {
                itemId: this.CHECKBOX_ITEM_ID,
                boxLabel: AMStudioPlugin_properties.Rendition_downloadable,
                ...{ colspan: 3 },
                plugins: [
                  Config(BindDisablePlugin, {
                    forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                    bindTo: config.bindTo,
                  }),
                  Config(BindPropertyPlugin, {
                    bindTo: config.bindTo.extendBy("properties", config.visiblePropertyName),
                    bidirectional: true,
                    ifUndefined: false,
                    transformer: (value: number): boolean => ! !value,
                    reverseTransformer: (checked: boolean): any => checked,
                  }),
                  Config(PropertyFieldPlugin, { propertyName: config.visiblePropertyName }),
                  Config(BindVisibilityPlugin, { bindTo: this.getCheckboxVisibleVE(config.visiblePropertyName, config.bindTo, config.propertyName) }),
                ],
              }),
            ],
          }),
        ],
      }),

    }), config))());
  }

  /**
   * An optional property name, which marks this blob's renditions as visible.
   */
  visiblePropertyName: string = null;
}

export default AssetDetailsBlobPropertyField;
