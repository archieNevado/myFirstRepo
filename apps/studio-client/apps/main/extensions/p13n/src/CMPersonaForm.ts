import BlueprintTabs_properties from "@coremedia-blueprint/studio-client.main.blueprint-forms/BlueprintTabs_properties";
import MetaDataWithoutSettingsForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/MetaDataWithoutSettingsForm";
import MultiLanguageDocumentForm from "@coremedia-blueprint/studio-client.main.blueprint-forms/forms/containers/MultiLanguageDocumentForm";
import TaxonomyLinkListPropertyField from "@coremedia-blueprint/studio-client.main.taxonomy-studio/taxonomy/selection/TaxonomyLinkListPropertyField";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import SpacingBEMEntities from "@coremedia/studio-client.ext.ui-components/bem/SpacingBEMEntities";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import VerticalSpacingPlugin from "@coremedia/studio-client.ext.ui-components/plugins/VerticalSpacingPlugin";
import PersonaErrorScreen from "@coremedia/studio-client.main.cap-personalization-ui/persona/form/PersonaErrorScreen";
import PersonaMainContainer from "@coremedia/studio-client.main.cap-personalization-ui/persona/form/PersonaMainContainer";
import PersonaDatePropertyField from "@coremedia/studio-client.main.cap-personalization-ui/persona/form/fields/PersonaDatePropertyField";
import PersonaDateTimeProperty from "@coremedia/studio-client.main.cap-personalization-ui/persona/form/fields/PersonaDateTimeProperty";
import PersonaStringPropertyField from "@coremedia/studio-client.main.cap-personalization-ui/persona/form/fields/PersonaStringPropertyField";
import DocumentForm from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentForm";
import PropertyFieldGroup from "@coremedia/studio-client.main.editor-components/sdk/premular/PropertyFieldGroup";
import LinkListPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/LinkListPropertyField";
import TextBlobPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/TextBlobPropertyField";
import BindDisablePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindDisablePlugin";
import StructPropertyField from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/struct/StructPropertyField";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import CMPersonaFormBase from "./CMPersonaFormBase";
import PersonalizationContext_properties from "./PersonalizationContext_properties";
import PersonalizationPlugIn_properties from "./PersonalizationPlugIn_properties";
import CMPersonaFormCheckboxField from "./property/CMPersonaFormCheckboxField";
import CMPersonaFormComboBoxField from "./property/CMPersonaFormComboBoxField";

interface CMPersonaFormConfig extends Config<CMPersonaFormBase> {
}

/*
HOW TO STRUCTURE:
<perso:personaMainContainer>                -> define the valueExpression
      <perso:personaUiContainer>            -> visible when properties are VALID
          <editor:collapsibleFormPanel>     -> nested propertyFields are grouped with grey background
              <perso:*propertiefield/>
              <perso:*propertiefield/>
              <perso:*propertiefield/>
              ...
          </editor:collapsibleFormPanel>

         <editor:collapsibleFormPanel>       -> nested propertyFields are grouped with grey background
              <perso:*propertiefield/>
              <perso:*propertiefield/>
              <perso:*propertiefield/>
              ...
          </editor:collapsibleFormPanel>
      </perso:personaUiContainer>

      <perso:personaErrorScreen/>            -> visible when properties are INVALID
</perso:personaMainContainer>
 */
class CMPersonaForm extends CMPersonaFormBase {
  declare Config: CMPersonaFormConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.personalization.editorplugin.config.cmPersonaForm";

  static readonly PERSONA_IMAGE_ITEM_ID: string = "persona_image";

  constructor(config: Config<CMPersonaForm> = null) {
    super(ConfigUtils.apply(Config(CMPersonaForm, {

      items: [
        Config(DocumentForm, {
          title: BlueprintTabs_properties.Tab_standard_title,
          itemId: "contentTab",
          items: [
            Config(PersonaMainContainer, {
              itemId: "profileSettings",
              profileSettingsExpression: config.bindTo.extendBy("properties.profileSettings"),
              items: [
                /*Persona Image*/
                Config(PropertyFieldGroup, {
                  itemId: CMPersonaForm.PERSONA_IMAGE_ITEM_ID,
                  title: PersonalizationContext_properties.p13n_context_testuser_profile_image,
                  items: [
                    Config(LinkListPropertyField, {
                      linkType: "CMPicture",
                      showThumbnails: true,
                      propertyName: "profileExtensions.properties." + CMPersonaFormBase.PROFILE_IMAGE_NAME,
                      hideLabel: true,
                      maxCardinality: 1,
                      bindTo: config.bindTo,
                    }),
                  ],
                }),
                /*GroupContainer: Elastic Social*/
                Config(PropertyFieldGroup, {
                  itemId: "elasticSocial",
                  title: PersonalizationContext_properties.p13n_context_testuser_profile_socialsoftware,
                  ...ConfigUtils.append({
                    plugins: [
                      Config(BindDisablePlugin, {
                        bindTo: config.bindTo,
                        forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                      }),
                    ],
                  }),
                  items: [
                    Config(PersonaStringPropertyField, {
                      itemId: "givenname",
                      fieldLabel: PersonalizationContext_properties.p13n_context_testuser_profile_givenname,
                      propertyName: "givenname",
                      propertyContext: "personal",
                      emptyText: PersonalizationContext_properties.p13n_context_testuser_profile_givenname_emptyText,
                    }),
                    Config(PersonaStringPropertyField, {
                      itemId: "familyname",
                      fieldLabel: PersonalizationContext_properties.p13n_context_testuser_profile_name,
                      propertyName: "familyname",
                      propertyContext: "personal",
                      emptyText: PersonalizationContext_properties.p13n_context_testuser_profile_name_emptyText,
                    }),
                    Config(PersonaDatePropertyField, {
                      itemId: "dateofbirth",
                      fieldLabel: PersonalizationContext_properties.p13n_context_testuser_profile_birthday,
                      propertyName: "dateofbirth",
                      propertyContext: "personal",
                    }),
                    Config(PersonaStringPropertyField, {
                      itemId: "emailaddress",
                      fieldLabel: PersonalizationContext_properties.p13n_context_testuser_profile_email,
                      propertyName: "emailaddress",
                      propertyContext: "personal",
                    }),
                    Config(PersonaStringPropertyField, {
                      itemId: "numberOfComments",
                      fieldLabel: PersonalizationPlugIn_properties.con_number_of_comments,
                      propertyName: "numberOfComments",
                      propertyContext: "es_check",
                    }),
                    Config(PersonaStringPropertyField, {
                      itemId: "numberOfRatings",
                      fieldLabel: PersonalizationPlugIn_properties.con_number_of_ratings,
                      propertyName: "numberOfRatings",
                      propertyContext: "es_check",
                    }),
                    Config(PersonaStringPropertyField, {
                      itemId: "numberOfLikes",
                      fieldLabel: PersonalizationPlugIn_properties.con_number_of_likes,
                      propertyName: "numberOfLikes",
                      propertyContext: "es_check",
                    }),
                    Config(PersonaStringPropertyField, {
                      itemId: "numberOfExplicitInterests",
                      fieldLabel: PersonalizationPlugIn_properties.con_number_of_explicit_interests,
                      propertyName: "numberOfExplicitInterests",
                      propertyContext: "explicit",
                    }),
                    Config(CMPersonaFormComboBoxField, {
                      itemId: "gender",
                      fieldLabel: PersonalizationContext_properties.p13n_context_gender,
                      propertyName: "gender",
                      propertyContext: "socialuser",
                      values: [
                        ["male", PersonalizationPlugIn_properties.con_gender_male],
                        ["female", PersonalizationPlugIn_properties.con_gender_female],
                      ],
                    }),
                    Config(CMPersonaFormCheckboxField, {
                      itemId: "userLoggedIn",
                      fieldLabel: PersonalizationPlugIn_properties.con_social_background_login,
                      propertyName: "userLoggedIn",
                      propertyContext: "es_check",
                    }),
                  ],
                }),
                /*GroupContainer: Geolocation*/
                Config(PropertyFieldGroup, {
                  itemId: "geoLocation",
                  title: "Geolocation",
                  ...ConfigUtils.append({
                    plugins: [
                      Config(BindDisablePlugin, {
                        bindTo: config.bindTo,
                        forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                      }),
                    ],
                  }),
                  items: [
                    Config(CMPersonaFormComboBoxField, {
                      itemId: "city",
                      fieldLabel: PersonalizationContext_properties.p13n_context_location_city,
                      propertyName: "city",
                      propertyContext: "location",
                      values: [
                        ["\"Hamburg\"", "Hamburg"],
                        ["\"SanFrancisco\"", "San Francisco"],
                        ["\"London\"", "London"],
                        ["\"Singapore\"", "Singapore"],
                      ],
                    }),
                  ],
                }),
                /*GroupContainer: Search Engine*/
                Config(PropertyFieldGroup, {
                  itemId: "search",
                  title: "Search",
                  ...ConfigUtils.append({
                    plugins: [
                      Config(BindDisablePlugin, {
                        bindTo: config.bindTo,
                        forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                      }),
                    ],
                  }),
                  items: [
                    Config(PersonaStringPropertyField, {
                      itemId: "url",
                      fieldLabel: PersonalizationContext_properties.p13n_context_referrer,
                      propertyName: "url",
                      propertyContext: "referrer",
                      emptyText: PersonalizationContext_properties.p13n_context_referrer_emptyText,
                    }),
                    Config(CMPersonaFormComboBoxField, {
                      itemId: "searchengine",
                      fieldLabel: PersonalizationContext_properties.p13n_context_referrer_searchengine,
                      propertyName: "searchengine",
                      propertyContext: "referrer",
                      values: [
                        ["", " "],
                        ["bing", "Bing"],
                        ["google", "Google"],
                        ["yahoo", "Yahoo!"],
                      ],
                    }),
                  ],
                }),
                /*GroupContainer: Date and Time*/
                Config(PropertyFieldGroup, {
                  itemId: "dateAndTime",
                  title: PersonalizationContext_properties.p13n_context_testuser_profile_systemproperties,
                  ...ConfigUtils.append({
                    plugins: [
                      Config(BindDisablePlugin, {
                        bindTo: config.bindTo,
                        forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                      }),
                    ],
                  }),
                  items: [
                    Config(PersonaDateTimeProperty, {
                      fieldLabel: PersonalizationContext_properties.p13n_context_testuser_profile_date_and_time,
                      itemId: "dateTime",
                      propertyName: "dateTime",
                      propertyContext: "system",
                    }),
                  ],
                }),
                /*Explicit Taxonomy Container */
                Config(PropertyFieldGroup, {
                  itemId: "explictTaxonomy",
                  title: PersonalizationPlugIn_properties.p13n_context_taxonomy_explicit_interests_label,
                  items: [
                    Config(TaxonomyLinkListPropertyField, {
                      bindTo: config.bindTo,
                      taxonomyIdExpression: ValueExpressionFactory.createFromValue("Subject"),
                      propertyName: "profileExtensions.properties." + CMPersonaFormBase.TAXONOMY_PROPERTY_NAME_EXPLICIT,
                      hideLabel: true,
                      forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                    }),
                  ],
                }),
                /*Implicit Taxonomy Container */
                Config(PropertyFieldGroup, {
                  itemId: "implicitTaxonomy",
                  title: PersonalizationPlugIn_properties.p13n_context_taxonomy_implicit_interests_label,
                  items: [
                    Config(TaxonomyLinkListPropertyField, {
                      bindTo: config.bindTo,
                      taxonomyIdExpression: ValueExpressionFactory.createFromValue("Subject"),
                      propertyName: "profileExtensions.properties." + CMPersonaFormBase.TAXONOMY_PROPERTY_NAME_IMPLICIT,
                      hideLabel: true,
                      forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
                    }),
                  ],
                }),
                Config(PersonaErrorScreen, { bindTo: config.bindTo }),
              ],
            }),
          ],
        }),

        Config(MultiLanguageDocumentForm),
        Config(MetaDataWithoutSettingsForm, {
          bindTo: config.bindTo,
          ...ConfigUtils.append({
            plugins: [
              Config(AddItemsPlugin, {
                items: [
                  Config(PropertyFieldGroup, {
                    title: PersonalizationPlugIn_properties.Tab_test_user_profile_raw_title,
                    itemId: "cmPersonaProfileSettingsForm",
                    collapsed: true,
                    items: [
                      Config(TextBlobPropertyField, {
                        propertyName: "profileSettings",
                        height: 550,
                      }),
                    ],
                  }),
                  Config(PropertyFieldGroup, {
                    title: PersonalizationPlugIn_properties.Tab_test_user_profile_struct_title,
                    itemId: "cmPersonaProfileExtensionsForm",
                    collapsed: true,
                    items: [
                      Config(StructPropertyField, {
                        propertyName: "profileExtensions",
                        hideLabel: true,
                      }),
                    ],
                  }),
                ],
              }),
              Config(VerticalSpacingPlugin, { modifier: SpacingBEMEntities.VERTICAL_SPACING_MODIFIER_200 }),
            ],
          }),

        }),

      ],

    }), config));
  }
}

export default CMPersonaForm;
