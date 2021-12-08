import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import OpenInTabAction from "@coremedia/studio-client.ext.form-services-toolkit/actions/OpenInTabAction";
import ShowInRepositoryAction from "@coremedia/studio-client.ext.library-services-toolkit/actions/ShowInRepositoryAction";
import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import ContextMenuPlugin from "@coremedia/studio-client.ext.ui-components/plugins/ContextMenuPlugin";
import ToolbarSkin from "@coremedia/studio-client.ext.ui-components/skins/ToolbarSkin";
import CopyToClipboardAction from "@coremedia/studio-client.main.editor-components/sdk/clipboard/CopyToClipboardAction";
import LinkListGridPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListGridPanel";
import PropertyFieldContextMenu from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/PropertyFieldContextMenu";
import DisplayField from "@jangaroo/ext-ts/form/field/Display";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import Toolbar from "@jangaroo/ext-ts/toolbar/Toolbar";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CatalogAssetsLinkListWrapper from "./CatalogAssetsLinkListWrapper";
import CatalogAssetsPropertyBase from "./CatalogAssetsPropertyBase";

interface CatalogAssetsPropertyConfig extends Config<CatalogAssetsPropertyBase>, Partial<Pick<CatalogAssetsProperty,
  "bindTo" |
  "propertyName" |
  "assetContentTypes" |
  "maxCardinality" |
  "emptyText"
>> {
}

class CatalogAssetsProperty extends CatalogAssetsPropertyBase {
  declare Config: CatalogAssetsPropertyConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.catalogAssetsProperty";

  constructor(config: Config<CatalogAssetsProperty> = null) {
    super((()=> ConfigUtils.apply(Config(CatalogAssetsProperty, {
      activeItemValueExpression: CatalogAssetsPropertyBase.getActiveCatalogAssetPropertyValueExpression(config),

      items: [
        Config(LinkListGridPanel, {
          itemId: CatalogAssetsPropertyBase.CATALOG_ASSET_PROPERTY_ITEM_ID,
          enableColumnMove: false,
          showThumbnails: true,
          selectedValuesExpression: this.getSelectedExpression(),
          hideDropArea: true,
          readOnlyValueExpression: this.getReadOnlyVE(),
          linkListWrapper: new CatalogAssetsLinkListWrapper({
            bindTo: config.bindTo,
            linksVE: config.bindTo.extendBy(config.propertyName),
            assetContentTypes: config.assetContentTypes,
            maxCardinality: config.maxCardinality,
            readOnlyVE: this.getReadOnlyVE(),
          }),
          tbar: Config(Toolbar, {
            itemId: "catalogAssetsToolbar",
            ui: ToolbarSkin.FIELD.getSkin(),
            items: [
              Config(IconButton, {
                itemId: "openInTab",
                baseAction: new OpenInTabAction({ contentValueExpression: this.getSelectedExpression() }),
              }),
              Config(IconButton, {
                itemId: "open",
                baseAction: new ShowInRepositoryAction({ contentValueExpression: this.getSelectedExpression() }),
              }),
              Config(Separator, { itemId: "openActionsSeparator" }),
              Config(IconButton, {
                itemId: "copyToClipboard",
                baseAction: new CopyToClipboardAction({ contentValueExpression: this.getSelectedExpression() }),
              }),
            ],
          }),
          ...ConfigUtils.append({
            plugins: [
              Config(ContextMenuPlugin, { contextMenu: Config(PropertyFieldContextMenu, { selectedItemsVE: this.getSelectedExpression() }) }),
            ],
          }),
        }),

        Config(DisplayField, {
          itemId: CatalogAssetsPropertyBase.CATALOG_EMPTY_LABEL_ITEM_ID,
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

  assetContentTypes: Array<any> = null;

  maxCardinality: number = NaN;

  /**
   * Text shown when the property is empty
   */
  emptyText: string = null;
}

export default CatalogAssetsProperty;
