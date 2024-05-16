import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintTabs_properties from "../../BlueprintTabs_properties";
import CategoryDocumentForm from "../containers/CategoryDocumentForm";
import SEOForm from "../containers/SEOForm";

interface DefaultExtraDataFormConfig extends Config<DocumentForm> {
}

class DefaultExtraDataForm extends DocumentForm {
  declare Config: DefaultExtraDataFormConfig;

  static readonly CATEGORY_DOCUMENT_FORM_ID: string = "categoryDocumentFormId";

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.defaultExtraDataForm";

  constructor(config: Config<DefaultExtraDataForm> = null) {
    super(ConfigUtils.apply(Config(DefaultExtraDataForm, {
      title: BlueprintTabs_properties.Tab_extras_title,
      itemId: "metadata",

      items: [
        Config(CategoryDocumentForm, {
          bindTo: config.bindTo,
          itemId: DefaultExtraDataForm.CATEGORY_DOCUMENT_FORM_ID,
        }),
        Config(SEOForm, { bindTo: config.bindTo }),
      ],

    }), config));
  }
}

export default DefaultExtraDataForm;
