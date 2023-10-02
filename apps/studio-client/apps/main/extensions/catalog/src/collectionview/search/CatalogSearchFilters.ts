import TaxonomyFilterPanel from "@coremedia-blueprint/studio-client.main.taxonomy-studio/taxonomy/filter/TaxonomyFilterPanel";
import SpacingBEMEntities from "@coremedia/studio-client.ext.ui-components/bem/SpacingBEMEntities";
import VerticalSpacingPlugin from "@coremedia/studio-client.ext.ui-components/plugins/VerticalSpacingPlugin";
import ContainerSkin from "@coremedia/studio-client.ext.ui-components/skins/ContainerSkin";
import LastEditedFilterPanel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/search/LastEditedFilterPanel";
import RelativeDateFilterPanel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/search/RelativeDateFilterPanel";
import SearchFiltersBase from "@coremedia/studio-client.main.editor-components/sdk/collectionview/search/SearchFiltersBase";
import SiteFilterPanel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/search/SiteFilterPanel";
import StatusFilterPanel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/search/StatusFilterPanel";
import TranslationStatusFilterPanel from "@coremedia/studio-client.main.editor-components/sdk/collectionview/search/TranslationStatusFilterPanel";
import AnchorLayout from "@jangaroo/ext-ts/layout/container/Anchor";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LostandfoundFilterPanel from "./LostandfoundFilterPanel";

interface CatalogSearchFiltersConfig extends Config<SearchFiltersBase> {
}

/**
 * @public
 */
class CatalogSearchFilters extends SearchFiltersBase {
  declare Config: CatalogSearchFiltersConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.assets.studio.config.contentCatalogSearchFilters";

  static readonly ITEM_ID: string = "contentCatalogSearchFilters";

  constructor(config: Config<CatalogSearchFilters> = null) {
    super(ConfigUtils.apply(Config(CatalogSearchFilters, {
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
        Config(SiteFilterPanel),
        Config(RelativeDateFilterPanel, { dateFieldName: "modificationdate" }),
        Config(RelativeDateFilterPanel, { dateFieldName: "publicationdate" }),
        Config(TranslationStatusFilterPanel),
        Config(LostandfoundFilterPanel),
      ],
      layout: Config(AnchorLayout),
      plugins: [
        Config(VerticalSpacingPlugin, { modifier: SpacingBEMEntities.VERTICAL_SPACING_MODIFIER_200 }),
      ],
    }), config));
  }
}

export default CatalogSearchFilters;
