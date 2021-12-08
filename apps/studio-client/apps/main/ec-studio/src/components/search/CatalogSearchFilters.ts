import SpacingBEMEntities from "@coremedia/studio-client.ext.ui-components/bem/SpacingBEMEntities";
import VerticalSpacingPlugin from "@coremedia/studio-client.ext.ui-components/plugins/VerticalSpacingPlugin";
import ContainerSkin from "@coremedia/studio-client.ext.ui-components/skins/ContainerSkin";
import SearchFiltersBase from "@coremedia/studio-client.main.editor-components/sdk/collectionview/search/SearchFiltersBase";
import AnchorLayout from "@jangaroo/ext-ts/layout/container/Anchor";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import FacetsFilterPanel from "./filters/FacetsFilterPanel";

interface CatalogSearchFiltersConfig extends Config<SearchFiltersBase> {
}

/**
 * @public
 */
class CatalogSearchFilters extends SearchFiltersBase {
  declare Config: CatalogSearchFiltersConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.catalogSearchFilters";

  static readonly ITEM_ID: string = "catalogSearchFilter";

  static readonly FACET_FILTER_ID: string = "catalogFacetFilter";

  constructor(config: Config<CatalogSearchFilters> = null) {
    super(ConfigUtils.apply(Config(CatalogSearchFilters, {
      ui: ConfigUtils.asString(ContainerSkin.GRID_200),

      items: [
        Config(FacetsFilterPanel, { filterId: CatalogSearchFilters.FACET_FILTER_ID }),
      ],
      layout: Config(AnchorLayout),
      plugins: [
        Config(VerticalSpacingPlugin, { modifier: SpacingBEMEntities.VERTICAL_SPACING_MODIFIER_200 }),
      ],
    }), config));
  }
}

export default CatalogSearchFilters;
