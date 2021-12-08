import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import DateTimePropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/DateTimePropertyField";
import AutoLayout from "@jangaroo/ext-ts/layout/container/Auto";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CustomLabels_properties from "../../CustomLabels_properties";

interface ValidityDocumentFormConfig extends Config<PropertyFieldGroup> {
}

class ValidityDocumentForm extends PropertyFieldGroup {
  declare Config: ValidityDocumentFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.validityDocumentForm";

  constructor(config: Config<ValidityDocumentForm> = null) {
    super(ConfigUtils.apply(Config(ValidityDocumentForm, {
      title: CustomLabels_properties.PropertyGroup_Validity_label,
      itemId: "validityForm",
      collapsed: true,
      expandOnValues: "validFrom,validTo",

      items: [
        Config(DateTimePropertyField, {
          bindTo: config.bindTo,
          forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
          itemId: "validFrom",
          labelAlign: "top",
          propertyName: "validFrom",
          timeZoneHidden: false,
        }),
        Config(DateTimePropertyField, {
          bindTo: config.bindTo,
          forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
          itemId: "validTo",
          labelAlign: "top",
          propertyName: "validTo",
          timeZoneHidden: false,
        }),
      ],
      /* autolayout keeps validityForm from breaking when additional data is added to the documentForm, see CMS-9884 */
      layout: Config(AutoLayout),

    }), config));
  }
}

export default ValidityDocumentForm;
