import BlueprintTabs_properties from "@coremedia-blueprint/studio-client.main.blueprint-forms/BlueprintTabs_properties";
import MetaDataWithoutSettingsForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/MetaDataWithoutSettingsForm";
import MultiLanguageDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/MultiLanguageDocumentForm";
import ConditionsField from "@coremedia/studio-client.main.cap-personalization-ui/ConditionsField";
import BooleanCondition from "@coremedia/studio-client.main.cap-personalization-ui/condition/BooleanCondition";
import DateCondition from "@coremedia/studio-client.main.cap-personalization-ui/condition/DateCondition";
import DateTimeCondition from "@coremedia/studio-client.main.cap-personalization-ui/condition/DateTimeCondition";
import EnumCondition from "@coremedia/studio-client.main.cap-personalization-ui/condition/EnumCondition";
import IntegerCondition from "@coremedia/studio-client.main.cap-personalization-ui/condition/IntegerCondition";
import PercentageKeywordCondition from "@coremedia/studio-client.main.cap-personalization-ui/condition/PercentageKeywordCondition";
import StringCondition from "@coremedia/studio-client.main.cap-personalization-ui/condition/StringCondition";
import TimeCondition from "@coremedia/studio-client.main.cap-personalization-ui/condition/TimeCondition";
import RichTextPropertyField from "@coremedia/studio-client.main.ckeditor4-components/fields/RichTextPropertyField";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import PersonalizationContext_properties from "./PersonalizationContext_properties";
import PersonalizationDocTypes_properties from "./PersonalizationDocTypes_properties";
import PersonalizationPlugIn_properties from "./PersonalizationPlugIn_properties";

interface CMSegmentFormConfig extends Config<DocumentTabPanel> {
}

class CMSegmentForm extends DocumentTabPanel {
  declare Config: CMSegmentFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.personalization.editorplugin.config.cmSegmentForm";

  constructor(config: Config<CMSegmentForm> = null) {
    super(ConfigUtils.apply(Config(CMSegmentForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_content_title,
          itemId: "contentTab",
          items: [
            Config(PropertyFieldGroup, {
              title: PersonalizationDocTypes_properties.CMSegment_description_text,
              collapsible: false,
              itemId: "cmSegmentsDescriptionForm",
              items: [
                Config(RichTextPropertyField, {
                  itemId: "description",
                  propertyName: "description",
                  hideLabel: true,
                }),
              ],
            }),
            Config(PropertyFieldGroup, {
              title: PersonalizationDocTypes_properties.CMSegment_conditions_text,
              itemId: "cmSegmentConditionsForm",
              items: [
                /* workaround for rulesField-layout-bug (full-width-bug): use custom margin style */
                Config(ConditionsField, {
                  propertyName: "conditions",
                  forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                  conditionItems: [
                    Config(TimeCondition, {
                      conditionName: PersonalizationContext_properties.p13n_context_system_timeOfDay,
                      propertyName: "system.timeOfDay",
                    }),
                    Config(DateCondition, {
                      conditionName: PersonalizationContext_properties.p13n_context_system_date,
                      propertyName: "system.date",
                    }),
                    Config(DateTimeCondition, {
                      conditionName: PersonalizationContext_properties.p13n_context_system_dateTime,
                      propertyName: "system.dateTime",
                    }),
                    Config(EnumCondition, {
                      conditionName: PersonalizationContext_properties.p13n_context_system_dayOfWeek,
                      propertyName: "system.dayOfWeek",
                      operators: ["lt", "eq", "gt"],
                      operatorNames: {
                        lt: PersonalizationPlugIn_properties.con_day_week_lt,
                        eq: PersonalizationPlugIn_properties.con_day_week_eq,
                        gt: PersonalizationPlugIn_properties.con_day_week_gt,
                      },
                      value: [
                        [1, PersonalizationContext_properties.p13n_context_system_dayOfWeek_1],
                        [2, PersonalizationContext_properties.p13n_context_system_dayOfWeek_2],
                        [3, PersonalizationContext_properties.p13n_context_system_dayOfWeek_3],
                        [4, PersonalizationContext_properties.p13n_context_system_dayOfWeek_4],
                        [5, PersonalizationContext_properties.p13n_context_system_dayOfWeek_5],
                        [6, PersonalizationContext_properties.p13n_context_system_dayOfWeek_6],
                        [7, PersonalizationContext_properties.p13n_context_system_dayOfWeek_7],
                      ],
                    }),
                    Config(StringCondition, {
                      conditionName: PersonalizationContext_properties.p13n_context_referrer,
                      propertyName: "referrer.url",
                      valueEmptyText: PersonalizationContext_properties.p13n_context_referrer_emptyText,
                    }),
                    Config(StringCondition, {
                      conditionName: PersonalizationContext_properties.p13n_context_referrer_query,
                      propertyName: "referrer.query",
                      operators: ["ct"],
                    }),
                    Config(EnumCondition, {
                      conditionName: PersonalizationContext_properties.p13n_context_referrer_searchengine,
                      propertyName: "referrer.searchengine",
                      operators: ["eq", "ne"],
                      value: [["bing", "Bing"],
                        ["google", "Google"],
                        ["yahoo", "Yahoo!"]],
                    }),
                    Config(PercentageKeywordCondition, {
                      conditionName: PersonalizationContext_properties.p13n_context_keyword,
                      propertyPrefix: "keyword",
                      isDefault: true,
                    }),
                    Config(IntegerCondition, {
                      conditionName: PersonalizationPlugIn_properties.con_number_of_comments,
                      propertyName: "es_check.numberOfComments",
                      operator: "greater than",
                    }),
                    Config(IntegerCondition, {
                      conditionName: PersonalizationPlugIn_properties.con_number_of_ratings,
                      propertyName: "es_check.numberOfRatings",
                      operator: "greater than",
                    }),
                    Config(IntegerCondition, {
                      conditionName: PersonalizationPlugIn_properties.con_number_of_likes,
                      propertyName: "es_check.numberOfLikes",
                      operator: "greater than",
                    }),
                    Config(IntegerCondition, {
                      conditionName: PersonalizationPlugIn_properties.con_number_of_explicit_interests,
                      propertyName: "explicit.numberOfExplicitInterests",
                      operator: "greater than",
                    }),
                    Config(BooleanCondition, {
                      conditionName: PersonalizationPlugIn_properties.con_social_background_login,
                      propertyName: "es_check.userLoggedIn",
                      checkedByDefault: "true",
                    }),
                    Config(EnumCondition, {
                      conditionName: PersonalizationPlugIn_properties.con_gender,
                      propertyName: "socialuser.gender",
                      operators: ["eq", "ne"],
                      value: [
                        ["male", PersonalizationPlugIn_properties.con_gender_male],
                        ["female", PersonalizationPlugIn_properties.con_gender_female],
                      ],
                    }),
                  ],
                }),
              ],
            }),

          ],
        }),
        Config(MultiLanguageDocumentForm),
        Config(MetaDataWithoutSettingsForm, { bindTo: config.bindTo }),
      ],

    }), config));
  }
}

export default CMSegmentForm;
