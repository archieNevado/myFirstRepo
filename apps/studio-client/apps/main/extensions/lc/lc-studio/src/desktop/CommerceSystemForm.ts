import CatalogObjectPropertyNames from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObjectPropertyNames";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import BindVisibilityPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindVisibilityPlugin";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import DisplayField from "@jangaroo/ext-ts/form/field/Display";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";

interface CommerceSystemFormConfig extends Config<DocumentForm> {
}

class CommerceSystemForm extends DocumentForm {
  declare Config: CommerceSystemFormConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.commerceSystemForm";

  #catalogNameExpression: ValueExpression = null;

  constructor(config: Config<CommerceSystemForm> = null) {
    super((()=>{
      this.#catalogNameExpression = config.bindTo.extendBy(CatalogObjectPropertyNames.CATALOG + ".name");
      return ConfigUtils.apply(Config(CommerceSystemForm, {
        title: LivecontextStudioPlugin_properties.Commerce_Tab_system_title,

        items: [
          Config(PropertyFieldGroup, {
            title: LivecontextStudioPlugin_properties.Commerce_PropertyGroup_filing_title,
            itemId: "filing",
            items: [
              Config(DisplayField, {
                itemId: "store",
                fieldLabel: LivecontextStudioPlugin_properties.Commerce_store_label,
                labelAlign: "left",
                labelSeparator: ":",
                plugins: [
                  Config(BindPropertyPlugin, { bindTo: config.bindTo.extendBy(CatalogObjectPropertyNames.STORE + ".name") }),
                ],
              }),
              Config(DisplayField, {
                itemId: "catalog",
                fieldLabel: LivecontextStudioPlugin_properties.Commerce_catalog_label,
                labelAlign: "left",
                labelSeparator: ":",
                plugins: [
                  Config(BindPropertyPlugin, { bindTo: this.#catalogNameExpression }),
                  Config(BindVisibilityPlugin, { bindTo: this.#catalogNameExpression }),
                ],
              }),
              Config(DisplayField, {
                itemId: "id",
                labelAlign: "left",
                fieldLabel: LivecontextStudioPlugin_properties.Commerce_id_label,
                labelSeparator: ":",
                plugins: [
                  Config(BindPropertyPlugin, { bindTo: config.bindTo.extendBy(CatalogObjectPropertyNames.ID) }),
                ],
              }),
              Config(DisplayField, {
                itemId: "externalId",
                labelAlign: "left",
                fieldLabel: LivecontextStudioPlugin_properties.Commerce_externalId_label,
                labelSeparator: ":",
                plugins: [
                  Config(BindPropertyPlugin, { bindTo: config.bindTo.extendBy(CatalogObjectPropertyNames.EXTERNAL_ID) }),
                ],
              }),
              Config(DisplayField, {
                itemId: "externalTechId",
                labelAlign: "left",
                fieldLabel: LivecontextStudioPlugin_properties.Commerce_externalTechId_label,
                labelSeparator: ":",
                plugins: [
                  Config(BindPropertyPlugin, { bindTo: config.bindTo.extendBy(CatalogObjectPropertyNames.EXTERNAL_TECH_ID) }),
                ],
              }),
            ],
          }),
        ],

      }), config);
    })());
  }
}

export default CommerceSystemForm;
