import Catalog from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Catalog";
import CatalogObject from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObject";
import CatalogObjectPropertyNames from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/CatalogObjectPropertyNames";
import Category from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Category";
import Product from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Product";
import AugmentationUtil from "@coremedia-blueprint/studio-client.main.ec-studio/helper/AugmentationUtil";
import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import session from "@coremedia/studio-client.cap-rest-client/common/session";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import Locale from "@coremedia/studio-client.client-core/data/Locale";
import EncodingUtil from "@coremedia/studio-client.client-core/util/EncodingUtil";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import TabTooltipInfo from "@coremedia/studio-client.main.editor-components/sdk/desktop/TabTooltipInfo";
import WorkAreaTab from "@coremedia/studio-client.main.editor-components/sdk/desktop/WorkAreaTab";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import PreviewPanel from "@coremedia/studio-client.main.editor-components/sdk/preview/PreviewPanel";
import MessageBoxUtil from "@coremedia/studio-client.main.editor-components/sdk/util/MessageBoxUtil";
import Ext from "@jangaroo/ext-ts";
import Component from "@jangaroo/ext-ts/Component";
import StringUtil from "@jangaroo/ext-ts/String";
import Container from "@jangaroo/ext-ts/container/Container";
import { as, bind, cast, is } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";
import CommerceWorkAreaTab from "./CommerceWorkAreaTab";

interface CommerceWorkAreaTabBaseConfig extends Config<WorkAreaTab> {
}

class CommerceWorkAreaTabBase extends WorkAreaTab {
  declare Config: CommerceWorkAreaTabBaseConfig;

  /**
   * The itemId of the whole document (left-hand side) container
   */
  static readonly DOCUMENT_CONTAINER_ITEM_ID: string = "documentContainer";

  /**
   * The itemId of the movable splitter between form and preview
   */
  static readonly PREVIEW_SPLIT_BAR_ITEM_ID: string = "previewSplitBar";

  static readonly PREVIEW_PANEL_ITEM_ID: string = "previewPanel";

  #documentPanelVisible: boolean = true;

  #previewVisible: boolean = true;

  constructor(config: Config<CommerceWorkAreaTab> = null) {
    config.entity = config.entity || config.object;
    super(config);

    const catalogObject = this.#getCatalogObject();

    catalogObject && catalogObject.load((): void => {
      this.#replaceTab(false);
      catalogObject.addPropertyChangeListener(CatalogObjectPropertyNames.CONTENT, bind(this, this.#replaceTab));
    });

    catalogObject && catalogObject.addValueChangeListener(bind(this, this.#reloadPreview));
  }

  override calculateTitle(): string {
    const catalogObject = this.#getCatalogObject();
    return catalogObject && CatalogHelper.getInstance().getDisplayName(catalogObject);
  }

  override calculateTooltip(): TabTooltipInfo {
    const tabTooltipInfo = new TabTooltipInfo();

    const title = this.calculateTitle();
    if (!title) return undefined;
    tabTooltipInfo.addTooltipEntry(TabTooltipInfo.TITLE, null, title);

    const site = editorContext._.getSitesService().getSite(this.#getCatalogObject().getSiteId());
    const siteName: string = site && site.getName();
    const siteLocale: Locale = site && site.getLocale();

    tabTooltipInfo.addTooltipEntry(TabTooltipInfo.SITE,
      Editor_properties.WorkArea_Premular_tooltip_siteName,
      siteName ? EncodingUtil.encodeForHTML(siteName) : Editor_properties.WorkArea_Premular_tooltip_noSite);

    tabTooltipInfo.addTooltipEntry(TabTooltipInfo.LOCALE,
      Editor_properties.WorkArea_Premular_tooltip_locale,
      siteLocale.getDisplayName());

    //add the catalog name
    let catalog: Catalog;
    if (is(this.#getCatalogObject(), Category)) {
      catalog = cast(Category, this.#getCatalogObject()).getCatalog();
    } else if (is(this.#getCatalogObject(), Product)) {
      catalog = cast(Product, this.#getCatalogObject()).getCatalog();
    }

    if (catalog) {
      const catalogName = catalog.getName();
      //if there is a catalog then wait until the name is available
      if (catalogName == undefined) {
        return undefined;
      }
      tabTooltipInfo.addTooltipEntry(CatalogObjectPropertyNames.CATALOG,
        LivecontextStudioPlugin_properties.Commerce_catalog_label,
        catalogName);
    }

    return tabTooltipInfo;
  }

  override calculateIcon(): string {
    const catalogObject = this.#getCatalogObject();
    return catalogObject ? AugmentationUtil.getTypeCls(catalogObject) : super.calculateIcon();
  }

  /**
   * Handler for collapsing either the document panel or the preview panel.
   */
  protected collapsePanel(itemId: string): void {
    this.handleCollapse(itemId);
  }

  handleCollapse(itemId: string): void {
    switch (itemId) {
    case CommerceWorkAreaTabBase.DOCUMENT_CONTAINER_ITEM_ID: {
      if (this.#previewVisible) {
        this.#documentPanelVisible = false;
      } else {
        this.#previewVisible = true;
      }
      break;
    }
    case CommerceWorkAreaTabBase.PREVIEW_PANEL_ITEM_ID: {
      if (this.#documentPanelVisible) {
        this.#previewVisible = false;
      } else {
        this.#documentPanelVisible = true;
      }
      break;
    }
    }

    this.#premularStateUpdated();

  }

  #premularStateUpdated(): void {

    if (this.rendered) {
      this.#updateVisibility();
      this.updateLayout();
    }
  }

  #updateVisibility(): void {
    Ext.suspendLayouts();
    try {
      this.#getDocumentContainer().setVisible(this.#documentPanelVisible);
      this.#getPreviewSplitBox().setVisible(this.#documentPanelVisible && this.#previewVisible);
      this.#getPreviewPanel().setVisible(this.#previewVisible);
    } finally {
      Ext.resumeLayouts();
    }
  }

  #getDocumentContainer(): Container {
    return cast(Container, this.queryById(CommerceWorkAreaTabBase.DOCUMENT_CONTAINER_ITEM_ID));
  }

  #getPreviewPanel(): PreviewPanel {
    return cast(PreviewPanel, this.queryById(CommerceWorkAreaTabBase.PREVIEW_PANEL_ITEM_ID));
  }

  #getPreviewSplitBox(): Component {
    return cast(Component, this.queryById(CommerceWorkAreaTabBase.PREVIEW_SPLIT_BAR_ITEM_ID));
  }

  #getCatalogObject(): CatalogObject {
    return as(this.getEntity(), CatalogObject);
  }

  #replaceTab(showMessage: boolean = true): void {
    const catalogObject = this.#getCatalogObject();
    const augmentingContent = as(catalogObject.get(CatalogObjectPropertyNames.CONTENT), Content);
    if (augmentingContent) { // the commerce object has been augmented
      editorContext._.getWorkAreaTabManager().replaceTab(catalogObject, augmentingContent);
      if (this.destroyed && showMessage) { //show the message only for the already rendered and then destroyed tabs
        augmentingContent.load((): void => {
          if (augmentingContent.getCreator() !== session._.getUser()) { //don't show the message if the category is augmented by myself.
            CommerceWorkAreaTabBase.#showAugmentationMessage(catalogObject);
          }
        });
      }
    }
  }

  static #showAugmentationMessage(augmentedCatalogObject: CatalogObject): void {
    const title = LivecontextStudioPlugin_properties.Category_augmentedMessage_title;
    const categoryName = CatalogHelper.getInstance().getDisplayName(augmentedCatalogObject);
    const text = StringUtil.format(LivecontextStudioPlugin_properties.Category_augmentedMessage_text, categoryName);

    MessageBoxUtil.showInfo(title, text);
  }

  #reloadPreview(): void {
    const previewPanel = as(this.getComponent(CommerceWorkAreaTabBase.PREVIEW_PANEL_ITEM_ID), PreviewPanel);
    //TODO: the preview panel cannot be found sometimes
    previewPanel && previewPanel.reloadFrame();
  }

  protected override onDestroy(): void {
    this.#getCatalogObject().removePropertyChangeListener(CatalogObjectPropertyNames.CONTENT, bind(this, this.#replaceTab));
    this.#getCatalogObject().removeValueChangeListener(bind(this, this.#reloadPreview));
    super.onDestroy();
  }

}

export default CommerceWorkAreaTabBase;
