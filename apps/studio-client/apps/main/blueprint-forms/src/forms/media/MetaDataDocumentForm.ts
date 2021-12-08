import BindComponentsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindComponentsPlugin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import MetaDataDocumentFormBase from "./MetaDataDocumentFormBase";
import MetaDataView from "./MetaDataView";

interface MetaDataDocumentFormConfig extends Config<MetaDataDocumentFormBase>, Partial<Pick<MetaDataDocumentForm,
  "propertyName" |
  "label" |
  "metadataSectionName"
>> {
}

class MetaDataDocumentForm extends MetaDataDocumentFormBase {
  declare Config: MetaDataDocumentFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.components.metaDataDocumentForm";

  constructor(config: Config<MetaDataDocumentForm> = null) {
    config = ConfigUtils.apply({ metadataSectionName: "id3" }, config);
    super((()=> ConfigUtils.apply(Config(MetaDataDocumentForm, {
      itemId: "metaSections",
      collapsed: true,
      title: config.label,
      hidden: true,

      ...ConfigUtils.append({
        plugins: [
          Config(BindComponentsPlugin, {
            configBeanParameterName: "metaDataSection",
            clearBeforeUpdate: true,
            reuseComponents: false,
            valueExpression: this.getMetaDataExpression(),
            getKey: MetaDataDocumentFormBase.getTemplateKey,
            template: Config(MetaDataView),
          }),
        ],
      }),

    }), config))());
  }

  propertyName: string = null;

  label: string = null;

  metadataSectionName: string = null;
}

export default MetaDataDocumentForm;
