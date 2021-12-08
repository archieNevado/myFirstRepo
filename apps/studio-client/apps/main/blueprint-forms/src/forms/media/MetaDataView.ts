import BindListPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindListPlugin";
import DisplayFieldSkin from "@coremedia/studio-client.ext.ui-components/skins/DisplayFieldSkin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import DisplayField from "@jangaroo/ext-ts/form/field/Display";
import DataView from "@jangaroo/ext-ts/view/View";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintStudio_properties from "../../BlueprintStudio_properties";
import MetaDataSection from "./MetaDataSection";
import MetaDataViewBase from "./MetaDataViewBase";

interface MetaDataViewConfig extends Config<MetaDataViewBase>, Partial<Pick<MetaDataView,
  "metaDataSection"
>> {
}

class MetaDataView extends MetaDataViewBase {
  declare Config: MetaDataViewConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.components.metaDataView";

  constructor(config: Config<MetaDataView> = null) {
    super((()=> ConfigUtils.apply(Config(MetaDataView, {

      items: [
        Config(DisplayField, {
          ui: DisplayFieldSkin.BOLD.getSkin(),
          value: config.metaDataSection.getMetaDataType(),
        }),
        Config(DataView, {
          itemId: "metaDataList",
          itemSelector: MetaDataViewBase.PROPERTIES_BLOCK.getCSSSelector(),
          singleSelect: true,
          multiSelect: false,
          emptyText: BlueprintStudio_properties.DataView_empty_text,
          deferEmptyText: true,
          tpl: MetaDataViewBase.getXTemplate(),
          plugins: [
            Config(BindListPlugin, {
              bindTo: this.getMetaDataExpression(config.metaDataSection),
              fields: [
                Config(DataField, { name: "property" }),
                Config(DataField, { name: "value" }),
                Config(DataField, { name: "formattedValue" }),
              ],
            }),
          ],
        }),
      ],

    }), config))());
  }

  metaDataSection: MetaDataSection = null;
}

export default MetaDataView;
