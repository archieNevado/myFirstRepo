import UserUtil from "@coremedia/studio-client.cap-base-models/util/UserUtil";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import Issue from "@coremedia/studio-client.client-core/data/validation/Issue";
import ThumbnailResolverFactory from "@coremedia/studio-client.ext.cap-base-components/thumbnails/ThumbnailResolverFactory";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import ObservableUtil from "@coremedia/studio-client.ext.ui-components/util/ObservableUtil";
import SiteAwareFeatureUtil from "@coremedia/studio-client.main.bpbase-studio-components/sites/SiteAwareFeatureUtil";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import IEditorContext from "@coremedia/studio-client.main.editor-components/sdk/IEditorContext";
import CollectionView from "@coremedia/studio-client.main.editor-components/sdk/collectionview/CollectionView";
import HeaderToolbar from "@coremedia/studio-client.main.editor-components/sdk/desktop/HeaderToolbar";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import TabExpandPlugin from "@coremedia/studio-client.main.editor-components/sdk/plugins/TabExpandPlugin";
import DocumentPanelNagBar from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentPanelNagBar";
import Premular from "@coremedia/studio-client.main.editor-components/sdk/premular/Premular";
import CheckedOutDocumentPanelNagBarEntry
  from "@coremedia/studio-client.main.editor-components/sdk/premular/nagbar/CheckedOutDocumentPanelNagBarEntry";
import UnauthorizedDocumentPanelNagBarEntry
  from "@coremedia/studio-client.main.editor-components/sdk/premular/nagbar/UnauthorizedDocumentPanelNagBarEntry";
import MetaStyleService from "@coremedia/studio-client.main.editor-components/sdk/util/MetaStyleService";
import Ext from "@jangaroo/ext-ts";
import Button from "@jangaroo/ext-ts/button/Button";
import { as } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import BlueprintFormsStudioPlugin from "./BlueprintFormsStudioPlugin";
import CMChannelExtension from "./CMChannelExtension";
import ContentInitializer from "./util/ContentInitializer";

interface BlueprintFormsStudioPluginBaseConfig extends Config<StudioPlugin> {
}

class BlueprintFormsStudioPluginBase extends StudioPlugin {
  declare Config: BlueprintFormsStudioPluginBaseConfig;

  /**
   * suppress preview if content has issue with at least one of the codes listed here
   */
  static readonly ISSUE_CODES_WITHOUT_PREVIEW: Array<any> = [
    "not_in_navigation",
  ];

  constructor(config: Config<BlueprintFormsStudioPlugin> = null) {
    super(config);
  }

  override init(editorContext: IEditorContext): void {
    super.init(editorContext);

    //caches the available users and groups
    UserUtil.init();

    // Register Navigation Parent for CMChannel
    CMChannelExtension.register(CMChannelExtension.CONTENT_TYPE_PAGE);

    //Enable advanced tabs
    TabExpandPlugin.ADVANCED_TABS_ENABLED = true;

    /**
     * Globally turns off strict consistency checks for all BindPropertyPlugins where the config option
     * 'disableStrictConsistency' is not set explicitly.
     */
    BindPropertyPlugin.DISABLE_STRICT_CONSISTENCY = true;

    SiteAwareFeatureUtil.preLoadConfiguration();

    ContentInitializer.applyInitializers();

    MetaStyleService.setDefaultStyleForMetaData(MetaStyleService.STYLE_GROUP_TURQUOISE, ["CMTeaser", "CMArticle"]);
    MetaStyleService.setDefaultStyleForMetaData(MetaStyleService.STYLE_GROUP_GREEN, [
      "CMVideo", "CMPicture", "CMAudio", "CMInteractive", "CMDownload", "CMImagemap", "CMSpinner", "CMSymbol", "CMImage",
    ]);
    MetaStyleService.setDefaultStyleForMetaData(MetaStyleService.STYLE_GROUP_PURPLE, ["CMSitemap", "CMSite", CMChannelExtension.CONTENT_TYPE_PAGE]);
    MetaStyleService.setDefaultStyleForMetaData(MetaStyleService.STYLE_GROUP_YELLOW, [
      "CMGallery", "CMQueryList", "CMCollection", "CMSelectionRules",
    ]);
    MetaStyleService.setDefaultStyleForMetaData(MetaStyleService.STYLE_GROUP_BLUE, [MetaStyleService.FALLBACK_META_DATA]);
    MetaStyleService.setDefaultStyleForMetaData(MetaStyleService.STYLE_GROUP_NONE, ["folder_"]);

    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create("CMSelectionRules", "defaultContent"));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create("CMCollection", "pictures", "items"));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create("CMTeasable", "pictures"));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create("CMSpinner", "pictures", "sequence"));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create("CMDownload", "data"));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create("CMImage", "data"));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create("CMVideo", "pictures"));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create("CMPicture", "data"));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create("CMSymbol", "icon"));
    editorContext.registerThumbnailResolver(ThumbnailResolverFactory.create("CMTheme", "icon"));

    DocumentPanelNagBar.addNagBarEntry(new CheckedOutDocumentPanelNagBarEntry(1000));
    DocumentPanelNagBar.addNagBarEntry(new UnauthorizedDocumentPanelNagBarEntry(990));
  }

  /**
   * Add folder from library tree to quick create menu
   * @return String
   */
  static calculateQuickCreateFolder(): string {
    const libToggleBtn = as(Ext.getCmp(HeaderToolbar.LIBRARY_BUTTON_ITEM_ID), Button);
    if (libToggleBtn) {
      ObservableUtil.dependOn(libToggleBtn, "toggle");
    }
    const collectionView = as(Ext.getCmp(CollectionView.COLLECTION_VIEW_ID), CollectionView);
    if (collectionView && collectionView.isVisible(true)) {
      const content = as(collectionView.getSelectedFolderValueExpression().getValue(), Content);
      if (content) {
        return content.getPath();
      }
    }
    return undefined;
  }

  /**
   * Used for columns setup:
   * Extends the sort by sorting by name.
   *
   * @param field the sortfield which is selected and should be extended
   * @param direction the sortdirection which is selected
   * @return array filled with additional order by statements
   */
  static extendOrderByName(field: string, direction: string): Array<any> {
    const orderBys = [];
    orderBys.push("name " + direction);
    return orderBys;
  }

  /**
   * Check if the given content is a CMLinkable and has no error issues.
   */
  protected static isValidCMLinkable(content: Content): boolean {
    const contentType = content.getType();
    if (contentType === undefined) {
      return undefined;
    }
    if (!contentType.isSubtypeOf("CMLinkable")) {
      return false;
    }
    const issues = content.getIssues();
    if (issues === undefined) {
      return undefined;
    }
    const all = issues.getAll();
    if (all === undefined) {
      return undefined;
    }
    return !all.some((issue: Issue): boolean =>
      BlueprintFormsStudioPluginBase.ISSUE_CODES_WITHOUT_PREVIEW.indexOf(issue.code) > -1,
    );
  }

  protected static getContentTypeVE(): ValueExpression {
    return ValueExpressionFactory.createFromFunction((): string => {
      const premular = as(editorContext._.getWorkArea().getActiveTab(), Premular);
      if (!premular) {
        return null;
      }
      const currentContent = premular.getContent();
      if (!currentContent) {
        return null;
      }
      if (!currentContent.getType()) {
        return undefined;
      }
      return currentContent.getType().getName();
    });
  }
}

export default BlueprintFormsStudioPluginBase;
