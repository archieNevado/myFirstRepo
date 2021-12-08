import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import NestedRulesPlugin from "@coremedia/studio-client.ext.ui-components/plugins/NestedRulesPlugin";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import ReferrerListPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/ReferrerListPanel";
import Component from "@jangaroo/ext-ts/Component";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import Toolbar from "@jangaroo/ext-ts/toolbar/Toolbar";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AMStudioPlugin_properties from "../AMStudioPlugin_properties";
import AssetConstants from "../AssetConstants";
import CreateVideoFromVideoAssetAction from "../actions/CreateVideoFromVideoAssetAction";
import AMAssetForm from "./AMAssetForm";
import AssetDetailsBlobPropertyField from "./AssetDetailsBlobPropertyField";

interface AMVideoAssetFormConfig extends Config<AMAssetForm> {
}

class AMVideoAssetForm extends AMAssetForm {
  declare Config: AMVideoAssetFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.assets.studio.config.amVideoAssetForm";

  constructor(config: Config<AMVideoAssetForm> = null) {
    super(ConfigUtils.apply(Config(AMVideoAssetForm, {
      originalIconCls: CoreIcons_properties.type_asset_video,

      renditions: [
        Config(PropertyFieldGroup, {
          itemId: "web",
          title: AMStudioPlugin_properties.PropertyGroup_web_label,
          expandOnValues: AssetConstants.PROPERTY_ASSET_WEB,
          collapsed: true,
          items: [
            Config(AssetDetailsBlobPropertyField, {
              propertyName: AssetConstants.PROPERTY_ASSET_WEB,
              hideLabel: true,
              visiblePropertyName: "metadata.renditions.web.show",
            }),
            Config(Component, { height: "20px" }),
            Config(ReferrerListPanel, {
              propertyName: "asset",
              showThumbnail: true,
              contentType: "CMVideo",
              title: AMStudioPlugin_properties.PropertyGroup_web_referrers_label,
              displayToolbarWhenEmpty: true,
              ...ConfigUtils.append({
                plugins: [
                  Config(NestedRulesPlugin, {
                    rules: [
                      Config(Toolbar, {
                        plugins: [
                          Config(AddItemsPlugin, {
                            items: [
                              Config(Separator),
                              Config(IconButton, {
                                itemId: "createVideo",
                                baseAction: new CreateVideoFromVideoAssetAction({ contentValueExpression: config.bindTo }),
                              }),
                            ],
                            after: [
                              Config(Component, { itemId: "copyToClipboard" }),
                            ],
                          }),
                        ],
                      }),
                    ],
                  }),
                ],
              }),
            }),
          ],
        }),
      ],

    }), config));
  }
}

export default AMVideoAssetForm;
