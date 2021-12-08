import StructContentLinkListWrapper from "@coremedia/studio-client.content-link-list-models/StructContentLinkListWrapper";
import NameColumn from "@coremedia/studio-client.ext.cap-base-components/columns/NameColumn";
import StatusColumn from "@coremedia/studio-client.ext.cap-base-components/columns/StatusColumn";
import TypeIconColumn from "@coremedia/studio-client.ext.cap-base-components/columns/TypeIconColumn";
import LinkListThumbnailColumn from "@coremedia/studio-client.ext.content-link-list-components/columns/LinkListThumbnailColumn";
import AnnotatedLinkListWidget from "@coremedia/studio-client.ext.ui-components/components/AnnotatedLinkListWidget";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import ILinkListWrapper from "@coremedia/studio-client.link-list-models/ILinkListWrapper";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import QuickCreateLinklistMenu from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/QuickCreateLinklistMenu";
import AnnotatedLinkListHelper from "@coremedia/studio-client.main.editor-components/sdk/util/AnnotatedLinkListHelper";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import BlueprintDocumentTypes_properties from "../../BlueprintDocumentTypes_properties";
import ValidityColumn from "../columns/ValidityColumn";
import FixedIndexConfigurationForm from "./FixedIndexConfigurationForm";

interface FixedIndexItemsFormConfig extends Config<PropertyFieldGroup>, Partial<Pick<FixedIndexItemsForm,
  "propertyName" |
  "linkType"
>> {
}

class FixedIndexItemsForm extends PropertyFieldGroup {
  declare Config: FixedIndexItemsFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.fixedIndexItemsForm";

  #structContentLinkListWrapper: StructContentLinkListWrapper = null;

  /**
   * A constant for the linklist property name
   */
  static readonly ITEMS_PROPERTY_NAME: string = "extendedItems";

  static readonly DEFAULT_LINK_TYPE: string = "CMTeasable";

  static readonly ITEMS_ANNOTATION_WIDGET_ITEM_ID: string = "items-annotation-widget";

  constructor(config: Config<FixedIndexItemsForm> = null) {
    super((()=> ConfigUtils.apply(Config(FixedIndexItemsForm, {
      title: BlueprintDocumentTypes_properties.CMQueryList_extendedItems_title,
      itemId: "fixedIndexItemsForm",

      items: [
        Config(LinkListPropertyField, {
          propertyName: ConfigUtils.asString(config.propertyName || FixedIndexItemsForm.ITEMS_PROPERTY_NAME),
          showThumbnails: true,
          hideLabel: true,
          bindTo: config.bindTo,
          linkListWrapper: this.#getStructContentLinkListWrapper(config),
          linkType: ConfigUtils.asString(config.linkType || FixedIndexItemsForm.DEFAULT_LINK_TYPE),
          additionalToolbarItems: [
            Config(Separator),
            Config(QuickCreateLinklistMenu, {
              bindTo: config.bindTo,
              sourceLinkListVE: this.#getStructContentLinkListWrapper(config).getVE(),
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
          rowWidget: Config(AnnotatedLinkListWidget, {
            itemId: FixedIndexItemsForm.ITEMS_ANNOTATION_WIDGET_ITEM_ID,
            items: [
              Config(FixedIndexConfigurationForm, {
                bindTo: config.bindTo,
                collapsible: false,
                forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                propertyName: ConfigUtils.asString(config.propertyName || FixedIndexItemsForm.ITEMS_PROPERTY_NAME),
              }),
            ],
          }),
        }),
      ],

    }), config))());
  }

  #getStructContentLinkListWrapper(config: Config<FixedIndexItemsForm>): ILinkListWrapper {
    if (!this.#structContentLinkListWrapper) {
      this.#structContentLinkListWrapper = AnnotatedLinkListHelper.createStructContentLinkListWrapper(config.bindTo,
        config.forceReadOnlyValueExpression,
        config.linkType || FixedIndexItemsForm.DEFAULT_LINK_TYPE,
        config.propertyName || FixedIndexItemsForm.ITEMS_PROPERTY_NAME,
      );
    }
    return this.#structContentLinkListWrapper;
  }

  /** the property of the Bean to bind in this field - defaults to "extendedItems" */
  propertyName: string = null;

  /* The allowed type of links - default to "CMTeasable" */
  linkType: string = null;
}

export default FixedIndexItemsForm;
