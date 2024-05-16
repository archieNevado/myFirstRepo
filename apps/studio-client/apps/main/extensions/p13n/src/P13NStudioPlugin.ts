import ContentTypes_properties from "@coremedia/studio-client.cap-base-models/content/ContentTypes_properties";
import ConditionsField from "@coremedia/studio-client.main.cap-personalization-ui/ConditionsField";
import PersonalizationContextNames_properties from "@coremedia/studio-client.main.cap-personalization-ui/PersonalizationContextNames_properties";
import SelectionRulesField from "@coremedia/studio-client.main.cap-personalization-ui/SelectionRulesField";
import Addconditionitems from "@coremedia/studio-client.main.cap-personalization-ui/plugin/Addconditionitems";
import ConfigureDocumentTypes from "@coremedia/studio-client.main.editor-components/configuration/ConfigureDocumentTypes";
import CopyResourceBundleProperties from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import AddTabbedDocumentFormsPlugin from "@coremedia/studio-client.main.editor-components/sdk/plugins/AddTabbedDocumentFormsPlugin";
import TabbedDocumentFormDispatcher from "@coremedia/studio-client.main.editor-components/sdk/premular/TabbedDocumentFormDispatcher";
import PreviewPanel from "@coremedia/studio-client.main.editor-components/sdk/preview/PreviewPanel";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import CMP13NSearchForm from "./CMP13NSearchForm";
import CMPersonaForm from "./CMPersonaForm";
import CMSegmentForm from "./CMSegmentForm";
import CMSelectionRulesForm from "./CMSelectionRulesForm";
import P13NStudioPluginBase from "./P13NStudioPluginBase";
import PersonalizationContext_properties from "./PersonalizationContext_properties";
import PersonalizationDocTypes_properties from "./PersonalizationDocTypes_properties";
import PersonalizationPlugIn_properties from "./PersonalizationPlugIn_properties";
import AddPersonaSelectorPlugin from "./extensions/AddPersonaSelectorPlugin";
import BooleanTaxonomyCondition from "./taxonomy/BooleanTaxonomyCondition";
import PercentageTaxonomyCondition from "./taxonomy/PercentageTaxonomyCondition";

interface P13NStudioPluginConfig extends Config<P13NStudioPluginBase> {
}

class P13NStudioPlugin extends P13NStudioPluginBase {
  declare Config: P13NStudioPluginConfig;

  constructor(config: Config<P13NStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(P13NStudioPlugin, {

      rules: [

        Config(TabbedDocumentFormDispatcher, {
          plugins: [
            Config(AddTabbedDocumentFormsPlugin, {
              documentTabPanels: [
                Config(CMSelectionRulesForm, { itemId: "CMSelectionRules" }),
                Config(CMP13NSearchForm, { itemId: "CMP13NSearch" }),
                Config(CMPersonaForm, { itemId: "CMUserProfile" }),
                Config(CMSegmentForm, { itemId: "CMSegment" }),
              ],
            }),
          ],
        }),

        Config(PreviewPanel, {
          plugins: [
            Config(AddPersonaSelectorPlugin),
          ],
        }),

        Config(SelectionRulesField, {
          plugins: [
            Config(Addconditionitems, {
              items: [
                Config(PercentageTaxonomyCondition, {
                  conditionName: PersonalizationPlugIn_properties.p13n_context_locationTaxonomies,
                  keywordEmptyText: PersonalizationPlugIn_properties.p13n_context_locationTaxonomies,
                  propertyPrefix: "locationTaxonomies",
                  suffixText: "%",
                }),
                Config(PercentageTaxonomyCondition, {
                  conditionName: PersonalizationPlugIn_properties.p13n_context_subjectTaxonomies,
                  keywordEmptyText: PersonalizationPlugIn_properties.p13n_context_subjectTaxonomies,
                  propertyPrefix: "subjectTaxonomies",
                  suffixText: "%",
                }),
                Config(BooleanTaxonomyCondition, {
                  conditionName: PersonalizationPlugIn_properties.p13n_context_explicit,
                  propertyPrefix: "explicit",
                }),
              ],
            }),
          ],
        }),

        Config(ConditionsField, {
          plugins: [
            Config(Addconditionitems, {
              items: [
                Config(PercentageTaxonomyCondition, {
                  conditionName: PersonalizationPlugIn_properties.p13n_context_locationTaxonomies,
                  keywordEmptyText: PersonalizationPlugIn_properties.p13n_context_locationTaxonomies,
                  propertyPrefix: "locationTaxonomies",
                  suffixText: "%",
                }),
                Config(PercentageTaxonomyCondition, {
                  conditionName: PersonalizationPlugIn_properties.p13n_context_subjectTaxonomies,
                  keywordEmptyText: PersonalizationPlugIn_properties.p13n_context_subjectTaxonomies,
                  propertyPrefix: "subjectTaxonomies",
                  suffixText: "%",
                }),
                Config(BooleanTaxonomyCondition, {
                  conditionName: PersonalizationPlugIn_properties.p13n_context_explicit,
                  propertyPrefix: "explicit",
                }),
              ],
            }),
          ],
        }),
      ],

      configuration: [
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, ContentTypes_properties),
          source: resourceManager.getResourceBundle(null, PersonalizationDocTypes_properties),
        }),
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, PersonalizationContextNames_properties),
          source: resourceManager.getResourceBundle(null, PersonalizationContext_properties),
        }),
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, PersonalizationContextNames_properties),
          source: resourceManager.getResourceBundle(null, PersonalizationPlugIn_properties),
        }),
        new ConfigureDocumentTypes({
          names: "CMSegment,CMUserProfile",
          preview: false,
        }),
      ],

    }), config));
  }
}

export default P13NStudioPlugin;
