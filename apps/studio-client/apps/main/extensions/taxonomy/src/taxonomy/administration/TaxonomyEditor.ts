import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import TaxonomyEditorBase from "./TaxonomyEditorBase";
import TaxonomyExplorerPanel from "./TaxonomyExplorerPanel";

interface TaxonomyEditorConfig extends Config<TaxonomyEditorBase> {
}

class TaxonomyEditor extends TaxonomyEditorBase {
  declare Config: TaxonomyEditorConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.taxonomy.taxonomyEditor";

  constructor(config: Config<TaxonomyEditor> = null) {
    super((()=> ConfigUtils.apply(Config(TaxonomyEditor, {
      title: TaxonomyStudioPlugin_properties.TaxonomyEditor_title,
      id: "taxonomyEditor",
      iconCls: TaxonomyStudioPlugin_properties.TaxonomyEditor_icon,
      layout: "fit",
      itemId: "taxonomyEditor",
      items: [
        Config(TaxonomyExplorerPanel, {
          id: "taxonomyExplorerPanel",
          searchResultExpression: this.getSearchResultExpression(),
          siteSelectionExpression: this.getSiteSelectionExpression(),
        }),
      ],

    }), config))());
  }
}

export default TaxonomyEditor;
