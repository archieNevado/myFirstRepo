import NameColumn from "@coremedia/studio-client.ext.cap-base-components/columns/NameColumn";
import StatusColumn from "@coremedia/studio-client.ext.cap-base-components/columns/StatusColumn";
import TypeIconColumn from "@coremedia/studio-client.ext.cap-base-components/columns/TypeIconColumn";
import LinkListThumbnailColumn from "@coremedia/studio-client.ext.content-link-list-components/columns/LinkListThumbnailColumn";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import QuickCreateLinklistMenu from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/QuickCreateLinklistMenu";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CustomLabels_properties from "../../CustomLabels_properties";
import ValidityColumn from "../columns/ValidityColumn";

interface AuthorLinkListDocumentFormConfig extends Config<PropertyFieldGroup>, Partial<Pick<AuthorLinkListDocumentForm,
  "authorPropertyName" |
  "quickCreateTypes"
>> {
}

class AuthorLinkListDocumentForm extends PropertyFieldGroup {
  declare Config: AuthorLinkListDocumentFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.authorLinkListDocumentForm";

  constructor(config: Config<AuthorLinkListDocumentForm> = null) {
    config = ConfigUtils.apply({
      authorPropertyName: "authors",
      quickCreateTypes: "CMPerson",
    }, config);
    super(ConfigUtils.apply(Config(AuthorLinkListDocumentForm, {
      title: CustomLabels_properties.PropertyGroup_Authors_label,
      itemId: "authorLinkListDocumentForm",
      collapsed: true,

      items: [
        Config(LinkListPropertyField, {
          itemId: "authors",
          propertyName: "authors",
          hideLabel: true,
          showThumbnails: true,
          additionalToolbarItems: [
            Config(Separator),
            Config(QuickCreateLinklistMenu, {
              bindTo: config.bindTo,
              propertyName: config.authorPropertyName,
              contentTypes: config.quickCreateTypes,
            }),
          ],
          fields: [
            Config(DataField, {
              name: ValidityColumn.STATUS_ID,
              mapping: "",
              convert: ValidityColumn.convert,
            }),
          ],
          columns: [
            Config(LinkListThumbnailColumn),
            Config(TypeIconColumn),
            Config(NameColumn, { flex: 1 }),
            Config(ValidityColumn),
            Config(StatusColumn),
          ],
        }),
      ],

    }), config));
  }

  /**
   * The content property name of the list to bind the newly created content to.
   * Defaults to authors.
   */
  authorPropertyName: string = null;

  /**
   * The content types for the QuickCreate menu.
   * Defaults to CMPerson.
   */
  quickCreateTypes: string = null;
}

export default AuthorLinkListDocumentForm;
