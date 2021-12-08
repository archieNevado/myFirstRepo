import TaxonomyFilterPanel from "@coremedia-blueprint/studio-client.main.taxonomy-studio/taxonomy/filter/TaxonomyFilterPanel";
import SpacingBEMEntities from "@coremedia/studio-client.ext.ui-components/bem/SpacingBEMEntities";
import VerticalSpacingPlugin from "@coremedia/studio-client.ext.ui-components/plugins/VerticalSpacingPlugin";
import ContainerSkin from "@coremedia/studio-client.ext.ui-components/skins/ContainerSkin";
import LastEditedFilterPanel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/search/LastEditedFilterPanel";
import RelativeDateFilterPanel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/search/RelativeDateFilterPanel";
import SearchFiltersBase from "@coremedia/studio-client.main.editor-components/sdk/collectionview/search/SearchFiltersBase";
import StatusFilterPanel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/search/StatusFilterPanel";
import AnchorLayout from "@jangaroo/ext-ts/layout/container/Anchor";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import AMStudioPlugin_properties from "../AMStudioPlugin_properties";
import AssetConstants from "../AssetConstants";
import AssetManagementConfigurationUtil from "../AssetManagementConfigurationUtil";
import ExpirationDateFilterPanel from "./ExpirationDateFilterPanel";
import StringListCheckboxFilterPanel from "./StringListCheckboxFilterPanel";

interface AssetSearchFiltersConfig extends Config<SearchFiltersBase> {
}

/**
 * @public
 */
class AssetSearchFilters extends SearchFiltersBase {
  declare Config: AssetSearchFiltersConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.assets.studio.config.assetSearchFilters";

  static readonly ITEM_ID: string = "assetSearchFilter";

  constructor(config: Config<AssetSearchFilters> = null) {
    super(ConfigUtils.apply(Config(AssetSearchFilters, {
      ui: ConfigUtils.asString(ContainerSkin.GRID_200),

      items: [
        Config(StatusFilterPanel),
        Config(LastEditedFilterPanel),
        Config(TaxonomyFilterPanel, {
          taxonomyId: "Subject",
          filterId: "Subject",
          propertyName: "subjecttaxonomy",
        }),
        Config(TaxonomyFilterPanel, {
          taxonomyId: "Location",
          filterId: "Location",
          propertyName: "locationtaxonomy",
        }),
        Config(TaxonomyFilterPanel, {
          itemId: "assetDownloadPortal",
          filterId: AssetConstants.ASSET_TAXONOMY_ID,
          taxonomyId: AssetConstants.ASSET_TAXONOMY_ID,
          propertyName: AssetConstants.PROPERTY_ASSET_ASSETTAXONOMY_SEARCH,
        }),
        Config(RelativeDateFilterPanel, { dateFieldName: "modificationdate" }),
        Config(RelativeDateFilterPanel, { dateFieldName: "publicationdate" }),
        Config(StringListCheckboxFilterPanel, {
          filterId: "rightsChannels",
          propertyName: AssetConstants.PROPERTY_ASSET_METADATA_CHANNELS,
          title: AMStudioPlugin_properties.Filter_RightsChannels_text,
          availableValuesValueExpression: AssetManagementConfigurationUtil.getConfiguredRightsChannelsValueExpression(),
        }),
        Config(StringListCheckboxFilterPanel, {
          filterId: "rightsRegions",
          propertyName: AssetConstants.PROPERTY_ASSET_METADATA_REGIONS,
          title: AMStudioPlugin_properties.Filter_RightsRegions_text,
          availableValuesValueExpression: AssetManagementConfigurationUtil.getConfiguredRightsRegionsValueExpression(),
        }),
        Config(ExpirationDateFilterPanel),
      ],
      layout: Config(AnchorLayout),
      plugins: [
        Config(VerticalSpacingPlugin, { modifier: SpacingBEMEntities.VERTICAL_SPACING_MODIFIER_200 }),
      ],
    }), config));
  }
}

export default AssetSearchFilters;
