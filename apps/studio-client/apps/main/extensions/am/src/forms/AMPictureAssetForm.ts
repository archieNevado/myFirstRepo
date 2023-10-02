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
import CreatePictureFromPictureAssetAction from "../actions/CreatePictureFromPictureAssetAction";
import AMAssetForm from "./AMAssetForm";
import AssetDetailsBlobPropertyField from "./AssetDetailsBlobPropertyField";

interface AMPictureAssetFormConfig extends Config<AMAssetForm> {
}

class AMPictureAssetForm extends AMAssetForm {
  declare Config: AMPictureAssetFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.assets.studio.config.amPictureAssetForm";

  constructor(config: Config<AMPictureAssetForm> = null) {
    super(ConfigUtils.apply(Config(AMPictureAssetForm, {
      originalIconCls: CoreIcons_properties.type_asset_picture,

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
              showImageThumbnail: false,
              visiblePropertyName: "metadata.renditions.web.show",
            }),
            Config(Component, { height: "20px" }),
            Config(ReferrerListPanel, {
              propertyName: "asset",
              showThumbnail: true,
              contentType: "CMPicture",
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
                                itemId: "createPicture",
                                baseAction: new CreatePictureFromPictureAssetAction({ contentValueExpression: config.bindTo }),
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
        Config(PropertyFieldGroup, {
          itemId: "print",
          title: AMStudioPlugin_properties.PropertyGroup_print_label,
          collapsed: true,
          items: [
            Config(AssetDetailsBlobPropertyField, {
              propertyName: AssetConstants.PROPERTY_ASSET_PRINT,
              hideLabel: true,
              showImageThumbnail: false,
              visiblePropertyName: "metadata.renditions.print.show",
            }),
          ],
        }),
      ],

    }), config));
  }
}

export default AMPictureAssetForm;
