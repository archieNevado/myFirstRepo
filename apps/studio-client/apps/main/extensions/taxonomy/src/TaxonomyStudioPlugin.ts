import CustomLabels_properties from "@coremedia-blueprint/studio-client.main.blueprint-forms/CustomLabels_properties";
import CMLocTaxonomyForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/CMLocTaxonomyForm";
import CMTaxonomyForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/CMTaxonomyForm";
import CategoryDocumentForm
  from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/CategoryDocumentForm";
import StudioAppsImpl from "@coremedia/studio-client.app-context-models/apps/StudioAppsImpl";
import studioApps from "@coremedia/studio-client.app-context-models/apps/studioApps";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import IEditorContext from "@coremedia/studio-client.main.editor-components/sdk/IEditorContext";
import SearchFilters from "@coremedia/studio-client.main.editor-components/sdk/collectionview/search/SearchFilters";
import InputChipsField from "@coremedia/studio-client.main.editor-components/sdk/components/ChipsField/InputChipsField";
import ComponentBasedWorkAreaTabType
  from "@coremedia/studio-client.main.editor-components/sdk/desktop/ComponentBasedWorkAreaTabType";
import StudioPreferenceWindow from "@coremedia/studio-client.main.editor-components/sdk/desktop/StudioPreferenceWindow";
import WorkArea from "@coremedia/studio-client.main.editor-components/sdk/desktop/WorkArea";
import WorkAreaTabTypesPlugin from "@coremedia/studio-client.main.editor-components/sdk/desktop/WorkAreaTabTypesPlugin";
import AddTabbedDocumentFormsPlugin
  from "@coremedia/studio-client.main.editor-components/sdk/plugins/AddTabbedDocumentFormsPlugin";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import TabbedDocumentFormDispatcher
  from "@coremedia/studio-client.main.editor-components/sdk/premular/TabbedDocumentFormDispatcher";
import sitesService from "@coremedia/studio-client.multi-site-models/global/sitesService";
import Component from "@jangaroo/ext-ts/Component";
import { cast } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TaxonomyStudioPluginBase from "./TaxonomyStudioPluginBase";
import TaxonomyStudioPlugin_properties from "./taxonomy/TaxonomyStudioPlugin_properties";
import OpenTaxonomyEditorAction from "./taxonomy/action/OpenTaxonomyEditorAction";
import TaxonomyEditor from "./taxonomy/administration/TaxonomyEditor";
import TaxonomyFilterPanel from "./taxonomy/filter/TaxonomyFilterPanel";
import CMQueryListForm from "./taxonomy/forms/CMQueryListForm";
import TaxonomyChangePlugin from "./taxonomy/forms/TaxonomyChangePlugin";
import AddTaggingStrategyPlugin from "./taxonomy/preferences/AddTaggingStrategyPlugin";
import TaxonomyPreferenceWindowPlugin from "./taxonomy/preferences/TaxonomyPreferenceWindowPlugin";
import TaxonomyPropertyField from "./taxonomy/selection/TaxonomyPropertyField";

interface TaxonomyStudioPluginConfig extends Config<TaxonomyStudioPluginBase> {
}

class TaxonomyStudioPlugin extends TaxonomyStudioPluginBase {
  declare Config: TaxonomyStudioPluginConfig;

  static readonly xtype: string = "com.coremedia.blueprint.studio.config.taxonomy.taxonomyStudioPlugin";

  static readonly TAXONOMY_EDITOR_BUTTON_ID: string = "btn-taxonomy-editor";

  static readonly NEW_KEYWORD_SELECTOR_ITEM_ID: string = "newKeywordTypeSelector";

  static TAXONOMY_SEMANTIC_CALAIS_KEY: string = "semantic";

  static TAXONOMY_NAME_MATCHING_KEY: string = "nameMatching";

  static DEFAULT_SUGGESTION_KEY: string = TaxonomyStudioPlugin.TAXONOMY_NAME_MATCHING_KEY;

  override init(editorContext: IEditorContext): void {
    super.init(editorContext);

    cast(StudioAppsImpl, studioApps._).getSubAppLauncherRegistry().registerSubAppLauncher("cmTaxonomy", (): void => {
      const openTagsAction = new OpenTaxonomyEditorAction();
      openTagsAction.execute();
    });
  }

  constructor(config: Config<TaxonomyStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(TaxonomyStudioPlugin, {

      rules: [
        Config(CMTaxonomyForm, {
          plugins: [
            Config(TaxonomyChangePlugin),
          ],
        }),

        Config(CMLocTaxonomyForm, {
          plugins: [
            Config(TaxonomyChangePlugin),
          ],
        }),

        Config(CategoryDocumentForm, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(PropertyFieldGroup, {
                  title: CustomLabels_properties.PropertyGroup_Subject_label,
                  expandOnValues: "subjectTaxonomy",
                  itemId: "subjectTaxonomyItemId",
                  items: [
                    Config(TaxonomyPropertyField, {
                      propertyName: "subjectTaxonomy",
                      hideLabel: true,
                      taxonomyIdExpression: ValueExpressionFactory.createFromValue("Subject"),
                    }),
                  ],
                }),
                Config(PropertyFieldGroup, {
                  title: CustomLabels_properties.PropertyGroup_Free_Keywords_label,
                  expandOnValues: "keywords",
                  itemId: "freeKeywordsForm",
                  collapsed: true,
                  items: [
                    Config(InputChipsField, {
                      propertyName: "keywords",
                      hideLabel: true,
                    }),
                  ],
                }),
                Config(PropertyFieldGroup, {
                  title: CustomLabels_properties.PropertyGroup_Location_label,
                  expandOnValues: "locationTaxonomy",
                  itemId: "locationTaxonomyForm",
                  collapsed: true,
                  items: [
                    Config(TaxonomyPropertyField, {
                      itemId: "locTaxonomyItemId",
                      hideLabel: true,
                      propertyName: "locationTaxonomy",
                      taxonomyIdExpression: ValueExpressionFactory.createFromValue("Location"),
                    }),
                  ],
                }),
              ],
            }),
          ],
        }),

        Config(StudioPreferenceWindow, {
          plugins: [
            Config(TaxonomyPreferenceWindowPlugin),
          ],
        }),

        Config(WorkArea, {
          plugins: [
            Config(WorkAreaTabTypesPlugin, {
              tabTypes: [
                new ComponentBasedWorkAreaTabType({ tabComponent: Config(TaxonomyEditor, { closable: true }) }),
              ],
            }),
          ],
        }),

        Config(TabbedDocumentFormDispatcher, {
          plugins: [
            Config(AddTabbedDocumentFormsPlugin, {
              documentTabPanels: [
                Config(CMQueryListForm, { itemId: "CMQueryList" }),
              ],
            }),
          ],
        }),

        Config(SearchFilters, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                /*Lower case used for the property name here, since the name must match
            the SOLR field name that is complete lower case. The editor converts to lowercase to be sure too.  */
                Config(TaxonomyFilterPanel, {
                  taxonomyId: "Subject",
                  filterId: "Subject",
                  siteSelectionExpression: sitesService._.getPreferredSiteIdExpression(),
                  propertyName: "subjecttaxonomy",
                }),
                Config(TaxonomyFilterPanel, {
                  taxonomyId: "Location",
                  filterId: "Location",
                  siteSelectionExpression: sitesService._.getPreferredSiteIdExpression(),
                  propertyName: "locationtaxonomy",
                }),
              ],
              after: [
                Config(Component, { itemId: SearchFilters.LAST_EDITED_FILTER_ITEM_ID }),
              ],
            }),
          ],
        }),
      ],

      configuration: [
        new AddTaggingStrategyPlugin({
          serviceId: TaxonomyStudioPlugin.TAXONOMY_NAME_MATCHING_KEY,
          label: TaxonomyStudioPlugin_properties.TaxonomyPreferences_value_nameMatching_text,
        }),
        new AddTaggingStrategyPlugin({
          serviceId: TaxonomyStudioPlugin.TAXONOMY_SEMANTIC_CALAIS_KEY,
          label: TaxonomyStudioPlugin_properties.TaxonomyPreferences_value_semantic_opencalais_text,
        }),
      ],

    }), config));
  }
}

export default TaxonomyStudioPlugin;
