import BEMPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BEMPlugin";
import RichTextPropertyField from "@coremedia/studio-client.main.ckeditor4-components/fields/RichTextPropertyField";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import StringPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/StringPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CustomLabels_properties from "../../CustomLabels_properties";

interface DetailsDocumentFormConfig extends Config<PropertyFieldGroup> {
}

class DetailsDocumentForm extends PropertyFieldGroup {
  declare Config: DetailsDocumentFormConfig;

  /**
   * Workaround to eliminate the false browser calculated height.
   */
  static readonly DETAILS_DOCUMENT_FORM_BLOCK: string = "cm-details-document-form";

  static readonly DETAILS_DOCUMENT_FORM_ELEMENT: string = "item";

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.detailsDocumentForm";

  constructor(config: Config<DetailsDocumentForm> = null) {
    super(ConfigUtils.apply(Config(DetailsDocumentForm, {
      title: CustomLabels_properties.PropertyGroup_Details_label,
      itemId: "detailsDocumentForm",
      propertyNames: ["title", "detailText"],
      expandOnValues: "title,detailText",
      manageHeight: false,

      items: [
        Config(StringPropertyField, {
          itemId: "title",
          propertyName: "title",
        }),
        Config(RichTextPropertyField, {
          bindTo: config.bindTo,
          itemId: "detailText",
          propertyName: "detailText",
          initialHeight: 200,
        }),
      ],
      ...ConfigUtils.append({
        plugins: [
          Config(BEMPlugin, {
            block: DetailsDocumentForm.DETAILS_DOCUMENT_FORM_BLOCK,
            defaultElement: DetailsDocumentForm.DETAILS_DOCUMENT_FORM_ELEMENT,
          }),
        ],
      }),
    }), config));
  }
}

export default DetailsDocumentForm;
