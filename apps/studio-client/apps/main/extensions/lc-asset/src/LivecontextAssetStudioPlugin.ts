import CMDownloadForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/CMDownloadForm";
import CMPictureForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/CMPictureForm";
import CMSpinnerForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/CMSpinnerForm";
import CMVideoForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/CMVideoForm";
import DefaultExtraDataForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/components/DefaultExtraDataForm";
import Validators_properties from "@coremedia/studio-client.ext.errors-validation-components/validation/Validators_properties";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import CopyResourceBundleProperties from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import CollectionView from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionView";
import LicenseNames_properties from "@coremedia/studio-client.main.editor-components/sdk/desktop/LicenseNames_properties";
import Component from "@jangaroo/ext-ts/Component";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import LivecontextAssetStudioPluginBase from "./LivecontextAssetStudioPluginBase";
import LivecontextAssetStudioPlugin_properties from "./LivecontextAssetStudioPlugin_properties";
import CommerceReferencesForm from "./components/CommerceReferencesForm";
import InheritReferencesButton from "./components/InheritReferencesButton";
import LivecontextAssetLibraryPlugin from "./library/LivecontextAssetLibraryPlugin";

interface LivecontextAssetStudioPluginConfig extends Config<LivecontextAssetStudioPluginBase> {
}

/* Extend the standard Studio and Blueprint components for Live Context */
class LivecontextAssetStudioPlugin extends LivecontextAssetStudioPluginBase {
  declare Config: LivecontextAssetStudioPluginConfig;

  static readonly xtype: string = "com.coremedia.livecontext.asset.studio.config.livecontextAssetStudioPlugin";

  static readonly SEARCH_PRODUCT_PICTURES_BUTTON_ITEM_ID: string = LivecontextAssetLibraryPlugin.SEARCH_PRODUCT_PICTURES_BUTTON_ITEM_ID;

  static readonly SEARCH_PRODUCT_PICTURES_MENU_ITEM_ID: string = LivecontextAssetLibraryPlugin.SEARCH_PRODUCT_PICTURES_MENU_ITEM_ID;

  constructor(config: Config<LivecontextAssetStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(LivecontextAssetStudioPlugin, {

      rules: [

        /*Add a Product Link List to the 'tags' tab of the form...*/

        Config(CMPictureForm, {
          plugins: [
            Config(AddItemsPlugin, {
              recursive: true,
              items: [
                Config(CommerceReferencesForm, {
                  additionalToolbarItems: [
                    Config(InheritReferencesButton),
                  ],
                }),
              ],
              after: [
                Config(Component, { itemId: CMPictureForm.COPYRIGHT_FORM_ITEM_ID }),
              ],
            }),
          ],
        }),

        Config(CMSpinnerForm, {
          plugins: [
            Config(AddItemsPlugin, {
              recursive: true,
              items: [
                Config(CommerceReferencesForm, {
                  additionalToolbarItems: [
                    Config(InheritReferencesButton),
                  ],
                }),
              ],
              after: [
                Config(Component, { itemId: CMPictureForm.COPYRIGHT_FORM_ITEM_ID }),
              ],
            }),
          ],
        }),

        Config(CMVideoForm, {
          plugins: [
            Config(AddItemsPlugin, {
              recursive: true,
              items: [
                Config(CommerceReferencesForm),
              ],
              after: [
                Config(Component, { itemId: CMVideoForm.COPYRIGHT_FORM_ITEM_ID }),
              ],
            }),
          ],
        }),

        Config(CMDownloadForm, {
          plugins: [
            Config(AddItemsPlugin, {
              recursive: true,
              items: [
                Config(CommerceReferencesForm),
              ],
              after: [
                Config(Component, { itemId: DefaultExtraDataForm.CATEGORY_DOCUMENT_FORM_ID }),
              ],
            }),
          ],
        }),

        Config(CollectionView, {
          plugins: [
            Config(LivecontextAssetLibraryPlugin),
          ],
        }),

      ],

      configuration: [
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, LicenseNames_properties),
          source: resourceManager.getResourceBundle(null, LivecontextAssetStudioPlugin_properties),
        }),
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, Validators_properties),
          source: resourceManager.getResourceBundle(null, LivecontextAssetStudioPlugin_properties),
        }),
      ],

    }), config));
  }
}

export default LivecontextAssetStudioPlugin;
