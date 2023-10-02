import ContentTypes_properties from "@coremedia/studio-client.cap-base-models/content/ContentTypes_properties";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import IconButton from "@coremedia/studio-client.ext.ui-components/components/IconButton";
import AddArrayItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddArrayItemsPlugin";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import ButtonSkin from "@coremedia/studio-client.ext.ui-components/skins/ButtonSkin";
import CopyResourceBundleProperties from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import AddTabbedDocumentFormsPlugin from "@coremedia/studio-client.main.editor-components/sdk/plugins/AddTabbedDocumentFormsPlugin";
import DocumentFormDispatcher from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentFormDispatcher";
import TabbedDocumentFormDispatcher from "@coremedia/studio-client.main.editor-components/sdk/premular/TabbedDocumentFormDispatcher";
import OpenQuickCreateAction from "@coremedia/studio-client.main.editor-components/sdk/quickcreate/OpenQuickCreateAction";
import CommentExtensionTabPanel from "@coremedia/studio-client.main.social-studio-plugin/moderation/shared/details/comments/CommentExtensionTabPanel";
import UserProfileExtensionTabPanel from "@coremedia/studio-client.main.social-studio-plugin/moderation/shared/details/user/UserProfileExtensionTabPanel";
import CuratedTransferExtensionPoint from "@coremedia/studio-client.main.social-studio-plugin/plugins/CuratedTransferExtensionPoint";
import CustomUserInformationContainer from "@coremedia/studio-client.main.social-studio-plugin/usermanagement/details/CustomUserInformationContainer";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import ElasticSocialStudioPluginBase from "./ElasticSocialStudioPluginBase";
import ElasticSocialStudioPlugin_properties from "./ElasticSocialStudioPlugin_properties";
import CuratedUtil from "./curated/CuratedUtil";
import CMMailForm from "./forms/CMMailForm";
import ESDynamicListForm from "./forms/ESDynamicListForm";

interface ElasticSocialStudioPluginConfig extends Config<ElasticSocialStudioPluginBase> {
}

class ElasticSocialStudioPlugin extends ElasticSocialStudioPluginBase {
  declare Config: ElasticSocialStudioPluginConfig;

  static readonly xtype: string = "com.coremedia.blueprint.elastic.social.studio.config.elasticSocialStudioPlugin";

  /**
   * The itemId of the first curatedTransferExtensionPoint button group separator.
   */
  static readonly CURATED_TRANSFER_EXTENSION_POINT_SEP_FIRST_ITEM_ID: string = "curatedTransferExtensionPointSepFirst";

  /**
   * The itemId of the second curatedTransferExtensionPoint button group separator.
   */
  static readonly CURATED_TRANSFER_EXTENSION_POINT_SEP_SECOND_ITEM_ID: string = "curatedTransferExtensionPointSepSecond";

  /**
   * The itemId of the third curatedTransferExtensionPoint button group separator.
   */
  static readonly CURATED_TRANSFER_EXTENSION_POINT_SEP_THIRD_ITEM_ID: string = "curatedTransferExtensionPointSepThird";

  constructor(config: Config<ElasticSocialStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(ElasticSocialStudioPlugin, {

      rules: [
        Config(DocumentFormDispatcher, {
          plugins: [
            Config(AddArrayItemsPlugin, {
              arrayProperty: "lazyItems",
              items: [
                Config(CMMailForm, { itemId: "CMMail" }),
                Config(ESDynamicListForm, { itemId: "ESDynamicList" }),
              ],
            }),
          ],
        }),

        Config(TabbedDocumentFormDispatcher, {
          plugins: [
            Config(AddTabbedDocumentFormsPlugin, {
              documentTabPanels: [
                Config(CMMailForm, { itemId: "CMMail" }),
                Config(ESDynamicListForm, { itemId: "ESDynamicList" }),
              ],
            }),
          ],
        }),

        Config(CommentExtensionTabPanel, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                /*
            This is an example of how you can add custom components to the comment extension container
            inside the moderation-tab and access the REST resource.
             */
                /*<Panel ui="{PanelSkin.COLLAPSIBLE_200}" title="customStuff">
              <items>
                <es:ElasticPluginLabel fieldLabel="Note"
                                            expression="activeContributionAdministration.displayed.note"/>
              </items>
            </Panel>   */
              ],
            }),
          ],
        }),

        Config(UserProfileExtensionTabPanel, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                /*
            This is an example of how you can add custom components to the user profile extension container
            inside the moderation-tab and access the REST resource.
             */
                /* <Panel ui="{PanelSkin.COLLAPSIBLE_200}" title="customProfileStuff">
              <items>
                <es:ElasticPluginLabel fieldLabel="Note"
                                            expression="activeContributionAdministration.displayed.note"/>
              </items>
            </Panel> */
              ],
            }),
          ],
        }),

        Config(CustomUserInformationContainer, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                /*
            This is an example of how you can add custom components to the user management window
            and access the REST resource.
             */
                /*<Container>
              <items>
                <es:ElasticPluginLabel fieldLabel="Note"
                                            expression="userAdministration.edited.note"/>
              </items>
            </Container> */
              ],
            }),
          ],
        }),

        Config(CuratedTransferExtensionPoint, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(Separator, { itemId: ElasticSocialStudioPlugin.CURATED_TRANSFER_EXTENSION_POINT_SEP_FIRST_ITEM_ID }),
                Config(IconButton, {
                  itemId: "createArticleBtn",
                  scale: "medium",
                  ui: ButtonSkin.WORKAREA.getSkin(),
                  tooltip: ElasticSocialStudioPlugin_properties.curated_content_create_article_button_tooltip,
                  text: ElasticSocialStudioPlugin_properties.curated_content_create_article_button_tooltip,
                  iconCls: CoreIcons_properties.create_type_article,
                  baseAction: new OpenQuickCreateAction({
                    contentType: "CMArticle",
                    skipInitializers: true,
                    onSuccess: CuratedUtil.postCreateArticleFromComments,
                  }),
                }),
                Config(Separator, { itemId: ElasticSocialStudioPlugin.CURATED_TRANSFER_EXTENSION_POINT_SEP_SECOND_ITEM_ID }),
                Config(IconButton, {
                  itemId: "createPictureBtn",
                  scale: "medium",
                  ui: ButtonSkin.WORKAREA.getSkin(),
                  tooltip: ElasticSocialStudioPlugin_properties.curated_content_create_gallery_button_tooltip,
                  text: ElasticSocialStudioPlugin_properties.curated_content_create_gallery_button_tooltip,
                  iconCls: CoreIcons_properties.create_type_image_gallery,
                  baseAction: new OpenQuickCreateAction({
                    contentType: "CMGallery",
                    skipInitializers: true,
                    onSuccess: CuratedUtil.postCreateGalleryFromComments,
                  }),
                }),
                Config(Separator, { itemId: ElasticSocialStudioPlugin.CURATED_TRANSFER_EXTENSION_POINT_SEP_THIRD_ITEM_ID }),
              ],
            }),
          ],
        }),
      ],

      configuration: [
        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, ContentTypes_properties),
          source: resourceManager.getResourceBundle(null, ElasticSocialStudioPlugin_properties),
        }),
      ],

    }), config));
  }
}

export default ElasticSocialStudioPlugin;
