import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import StudioConfigurationUtil from "@coremedia/studio-client.ext.cap-base-components/util/config/StudioConfigurationUtil";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import AssetConstants from "./AssetConstants";

/**
 * Utility class for easy access to asset management configuration.
 */
class AssetManagementConfigurationUtil {
  static #configuredRightsChannelsValueExpression: ValueExpression = null;

  static #configuredRightsRegionsValueExpression: ValueExpression = null;

  /**
   * Return a value expression evaluating to the list of channels configured in the asset management settings document.
   * @return a value expression evaluating to the list of channels
   */
  static getConfiguredRightsChannelsValueExpression(): ValueExpression {
    if (!AssetManagementConfigurationUtil.#configuredRightsChannelsValueExpression) {
      AssetManagementConfigurationUtil.#configuredRightsChannelsValueExpression = ValueExpressionFactory.createFromFunction(
        StudioConfigurationUtil.getConfiguration,
        AssetManagementConfigurationUtil.getSettingsDocumentName(),
        AssetConstants.PROPERTY_ASSET_METADATA_CHANNELS);
    }
    return AssetManagementConfigurationUtil.#configuredRightsChannelsValueExpression;
  }

  /**
   * Return a value expression evaluating to the list of regions configured in the asset management settings document.
   * @return a value expression evaluating to the list of regions
   */
  static getConfiguredRightsRegionsValueExpression(): ValueExpression {
    if (!AssetManagementConfigurationUtil.#configuredRightsRegionsValueExpression) {
      AssetManagementConfigurationUtil.#configuredRightsRegionsValueExpression = ValueExpressionFactory.createFromFunction(
        StudioConfigurationUtil.getConfiguration,
        AssetManagementConfigurationUtil.getSettingsDocumentName(),
        AssetConstants.PROPERTY_ASSET_METADATA_REGIONS);
    }
    return AssetManagementConfigurationUtil.#configuredRightsRegionsValueExpression;
  }

  /**
   * Return the name of the settings document for asset management.
   * @return the name of the settings document
   */
  static getSettingsDocumentName(): string {
    const assetManagementConfig = AssetManagementConfigurationUtil.getAssetManagementConfiguration();
    return assetManagementConfig["settingsDocument"];
  }

  /**
   * Return the asset management configuration object.
   * See com.coremedia.blueprint.assets.studio.AssetManagementConfiguration.java for details.
   *
   * @return the configuration object
   */
  static getAssetManagementConfiguration(): any {
    return editorContext._.getConfiguration()["assetManagement"];
  }

  constructor() {
    throw new Error("Utility class AssetManagementConfigurationUtil must not be instantiated");
  }
}

export default AssetManagementConfigurationUtil;
