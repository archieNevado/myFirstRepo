import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import NestedRulesPlugin from "@coremedia/studio-client.ext.ui-components/plugins/NestedRulesPlugin";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import Component from "@jangaroo/ext-ts/Component";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AMStudioPluginBase from "./AMStudioPluginBase";

interface AddAssetLinkPluginConfig extends Config<NestedRulesPlugin>, Partial<Pick<AddAssetLinkPlugin,
  "tabItemId" |
  "afterItemId" |
  "title"
>> {
}

class AddAssetLinkPlugin extends NestedRulesPlugin {
  declare Config: AddAssetLinkPluginConfig;

  static readonly ASSET_LINK_ITEM_ID: string = "assetLink";

  constructor(config: Config<AddAssetLinkPlugin> = null) {
    const componentConfig = cast(DocumentTabPanel, config.cmp.initialConfig);
    super(ConfigUtils.apply(Config(AddAssetLinkPlugin, {

      rules: [
        Config(DocumentForm, {
          itemId: config.tabItemId,
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(PropertyFieldGroup, {
                  collapsed: true,
                  itemId: AddAssetLinkPlugin.ASSET_LINK_ITEM_ID,
                  title: config.title,
                  forceReadOnlyValueExpression: componentConfig.forceReadOnlyValueExpression,
                  items: [
                    Config(LinkListPropertyField, {
                      propertyName: "asset",
                      showThumbnails: true,
                      hideLabel: true,
                      openCollectionViewHandler: AMStudioPluginBase.openAssetSearch,
                    }),
                  ],
                }),
              ],
              after: [
                Config(Component, { itemId: config.afterItemId }),
              ],
            }),
          ],
        }),
      ],

    }), config));
  }

  tabItemId: string = null;

  afterItemId: string = null;

  title: string = null;
}

export default AddAssetLinkPlugin;
