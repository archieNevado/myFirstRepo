import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import DisplayField from "@jangaroo/ext-ts/form/field/Display";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CatalogLinkPropertyField from "./CatalogLinkPropertyField";
import ReadOnlyCatalogLinkPropertyFieldBase from "./ReadOnlyCatalogLinkPropertyFieldBase";

interface ReadOnlyCatalogLinkPropertyFieldConfig extends Config<ReadOnlyCatalogLinkPropertyFieldBase>, Partial<Pick<ReadOnlyCatalogLinkPropertyField,
  "bindTo" |
  "propertyName" |
  "emptyText" |
  "showThumbnails"
>> {
}

class ReadOnlyCatalogLinkPropertyField extends ReadOnlyCatalogLinkPropertyFieldBase {
  declare Config: ReadOnlyCatalogLinkPropertyFieldConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.readOnlyCatalogLinkProperty";

  constructor(config: Config<ReadOnlyCatalogLinkPropertyField> = null) {
    super((()=> ConfigUtils.apply(Config(ReadOnlyCatalogLinkPropertyField, {
      activeItemValueExpression: this.getActiveCatalogLinkPropertyValueExpression(config),

      items: [
        Config(CatalogLinkPropertyField, {
          itemId: ReadOnlyCatalogLinkPropertyFieldBase.READ_ONLY_CATALOG_LINK_ITEM_ID,
          bindTo: config.bindTo,
          forceReadOnlyValueExpression: ValueExpressionFactory.createFromValue(true),
          hideDropArea: true,
          propertyName: config.propertyName,
          showThumbnails: config.showThumbnails,
          emptyText: config.emptyText,
          hideRemove: true,
        }),
        Config(DisplayField, {
          itemId: ReadOnlyCatalogLinkPropertyFieldBase.READ_ONLY_CATALOG_LINK_EMPTY_DISPLAYFIELD_ITEM_ID,
          value: config.emptyText,
        }),
      ],

    }), config))());
  }

  /**
   * A property path expression leading to the Bean whose property is edited.
   * This property editor assumes that this bean has a property 'properties'.
   */
  bindTo: ValueExpression = null;

  /**
   * The name of the sting property of the Bean to bind in this field.
   * The string property holds the id of the catalog product
   */
  propertyName: string = null;

  /**
   * Text shown when the property is empty
   */
  emptyText: string = null;

  /**
   * Set to false to hide the thumbnail of the entries. Default is true
   */
  showThumbnails: boolean = false;
}

export default ReadOnlyCatalogLinkPropertyField;
