import TopicsHelper from "@coremedia-blueprint/studio-client.main.taxonomy-studio/TopicsHelper";
import ContentLocalizationUtil from "@coremedia/studio-client.cap-base-models/content/ContentLocalizationUtil";
import IdHelper from "@coremedia/studio-client.cap-rest-client/common/IdHelper";
import session from "@coremedia/studio-client.cap-rest-client/common/session";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ContentPropertyNames from "@coremedia/studio-client.cap-rest-client/content/ContentPropertyNames";
import UndocContentUtil from "@coremedia/studio-client.cap-rest-client/content/UndocContentUtil";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import PropertyChangeEvent from "@coremedia/studio-client.client-core/data/PropertyChangeEvent";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import EventUtil from "@coremedia/studio-client.client-core/util/EventUtil";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import IconWithTextBEMEntities from "@coremedia/studio-client.ext.ui-components/bem/IconWithTextBEMEntities";
import QtipUtil from "@coremedia/studio-client.ext.ui-components/util/QtipUtil";
import TabChangePluginBase from "@coremedia/studio-client.main.editor-components/sdk/desktop/TabChangePluginBase";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import MessageBoxUtil from "@coremedia/studio-client.main.editor-components/sdk/util/MessageBoxUtil";
import Ext from "@jangaroo/ext-ts";
import StringUtil from "@jangaroo/ext-ts/String";
import Model from "@jangaroo/ext-ts/data/Model";
import Event from "@jangaroo/ext-ts/event/Event";
import GridPanel from "@jangaroo/ext-ts/grid/Panel";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import RowSelectionModel from "@jangaroo/ext-ts/selection/RowModel";
import TableView from "@jangaroo/ext-ts/view/Table";
import MessageBoxWindow from "@jangaroo/ext-ts/window/MessageBox";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import { AnyFunction } from "@jangaroo/runtime/types";
import TopicPages_properties from "../TopicPages_properties";
import OpenTopicPagesEditorActionBase from "./OpenTopicPagesEditorActionBase";
import TopicPagesEditor from "./TopicPagesEditor";

interface TopicsPanelBaseConfig extends Config<Panel>, Partial<Pick<TopicsPanelBase,
  "selectionExpression"
>> {
}

/**
 * Base class of the taxonomy administration tab.
 */
class TopicsPanelBase extends Panel {
  declare Config: TopicsPanelBaseConfig;

  /**
   * The value expression that contains the selected topic record.
   */
  selectionExpression: ValueExpression = null;

  static readonly #COMPONENT_ID: string = "topicsPanel";

  #topicsExpression: ValueExpression = null;

  #filterValueExpression: ValueExpression = null;

  #taxonomyExpression: ValueExpression = null;

  #isFilteredExpression: ValueExpression = null;

  #selectionString: string = null;

  constructor(config: Config<TopicsPanelBase> = null) {
    config.id = TopicsPanelBase.#COMPONENT_ID;
    super(config);
  }

  protected override afterRender(): void {
    super.afterRender();
    this.#getSelectionModel().addListener("selectionchange", bind(this, this.#onSelect));
    this.#getGrid().addListener("afterlayout", bind(this, this.#addKeyMap));
    editorContext._.getSitesService().getPreferredSiteIdExpression().addChangeListener(bind(this, this.#siteSelectionChanged));
    TabChangePluginBase.getWorkAreaTabChangeExpression().addChangeListener(bind(this, this.#workAreaTabChanged));
  }

  #workAreaTabChanged(ve: ValueExpression): void {
    const component = ve.getValue();
    if (as(component, TopicPagesEditor)) {
      this.reload();
    }
  }

  /**
   * Called when the user has changed the site.
   */
  #siteSelectionChanged(): void {
    this.reload();
  }

  /**
   * Adds the key listener to the grid so that the user can input the topic
   * that should be selected.
   */
  #addKeyMap(): void {
    this.#getGrid().removeListener("afterlayout", bind(this, this.#addKeyMap));
    this.#getGrid().getEl().addListener("keyup", (evt: Event): void => {
      if (!evt.shiftKey && !evt.ctrlKey && !evt.altKey) {
        const code = evt.getCharCode();
        const character = String.fromCharCode(code).toLowerCase();
        this.#selectionString += character;
        if (!this.#selectRecordForInput(this.#selectionString)) {
          this.#selectionString = character;
          this.#selectRecordForInput(character);
        }
      }
    });
  }

  #selectRecordForInput(value: string): boolean {
    for (let i = 0; i < this.#getGrid().getStore().getCount(); i++) {
      const record = this.#getGrid().getStore().getAt(i);
      const name: string = record.data.name;
      if (name.toLowerCase().indexOf(value) === 0) {
        this.#getSelectionModel().select(i, false);
        return true;
      }
    }
    return false;
  }

  #getSelectionModel(): RowSelectionModel {
    return (as(this.#getGrid().getSelectionModel(), RowSelectionModel));
  }

  /**
   * The selection listener for the grid, will trigger the preview reload for a topic selection.
   */
  #onSelect(): void {
    const record = this.#getSelectionModel().getSelection()[0];
    this.selectionExpression.setValue(record);
  }

  /**
   * Returns the value expression that contains the list of contents to display as topics.
   * @return
   */
  protected getTopicsExpression(): ValueExpression {
    if (!this.#topicsExpression) {
      this.#topicsExpression = ValueExpressionFactory.create("topics", beanFactory._.createLocalBean());
      this.#topicsExpression.setValue([]);
    }
    return this.#topicsExpression;
  }

  /**
   * Returns the value expression that contains the list is filtered cos of length
   * @return
   */
  protected getIsFilteredExpression(): ValueExpression {
    if (!this.#isFilteredExpression) {
      this.#isFilteredExpression = ValueExpressionFactory.create("isFiltered", beanFactory._.createLocalBean());
      this.#isFilteredExpression.addChangeListener((ve: ValueExpression): void => {
        const filtered: boolean = ve.getValue();
        Ext.getCmp("topicPagesFilteredLabel").setVisible(filtered);
      });
    }
    return this.#isFilteredExpression;
  }

  /**
   * The value expression contains the value of the selected taxonomy.
   * @return
   */
  protected getTaxonomySelectionExpression(): ValueExpression {
    if (!this.#taxonomyExpression) {
      this.#taxonomyExpression = ValueExpressionFactory.create("taxonomy", beanFactory._.createLocalBean());
      this.#taxonomyExpression.addChangeListener(bind(this, this.reload));
    }
    return this.#taxonomyExpression;
  }

  /**
   * Returns the value expression that contains the active search expression.
   * @return
   */
  protected getFilterValueExpression(): ValueExpression {
    if (!this.#filterValueExpression) {
      this.#filterValueExpression = ValueExpressionFactory.create("topics", beanFactory._.createLocalBean());
    }
    return this.#filterValueExpression;
  }

  /**
   * Reloads the list of topics, fired after a search or a taxonomy selection.
   */
  protected reload(): void {
    this.#removePropertyChangeListeners();
    OpenTopicPagesEditorActionBase.isAdministrationEnabled((enabled: boolean): void => {
      if (!enabled) {
        return;
      }

      const taxonomyContent: Content = this.getTaxonomySelectionExpression().getValue();
      if (!taxonomyContent) {
        return;
      }
      const taxonomy = IdHelper.parseContentId(taxonomyContent);
      const term: string = this.#filterValueExpression.getValue() || "";
      const siteId = editorContext._.getSitesService().getPreferredSiteId();
      TopicsHelper.loadTopics(taxonomy, siteId, term, (items: Array<any>, filtered: boolean): void => {
        var initCall: AnyFunction = (): void => {
          this.#getGrid().getStore().removeListener("load", initCall);
          this.#getSelectionModel().select(0);
          this.getIsFilteredExpression().setValue(filtered);
        };
        this.#getGrid().getStore().addListener("load", initCall);
        this.getTopicsExpression().setValue(items);
      });
    });
  }

  /**
   * Returns the instance of the grid panel inside this panel.
   * @return
   */
  #getGrid(): GridPanel {
    return as(this.queryById("topicsGrid"), GridPanel);
  }

  /**
   * Displays the name of the topic page.
   */
  protected static nameRenderer(value: any, metaData: any, record: Model): string {
    return record.data.name;
  }

  /**
   * Displays the page the topic page is linked to.
   */
  protected pageRenderer(value: any, metaData: any, record: Model): string {
    const id: number = record.data.topic.getNumericId();
    const pageContent: Content = record.data.page;
    if (pageContent) {
      if (!record.data.rendered) {
        EventUtil.invokeLater((): void => //invoke later, otherwise JS error will be thrown that row is undefined.
          pageContent.load((): void => {
            record.data.rendered = true;
            record.commit(false);
          }),
        );
      } else {
        pageContent.addPropertyChangeListener(ContentPropertyNames.LIFECYCLE_STATUS, bind(this, this.#customPageChanged));
        const iconCls = ContentLocalizationUtil.getIconStyleClassForContentTypeName(pageContent.getType().getName());
        const tooltipText = pageContent.getName();
        const html = "<span class=\"" + IconWithTextBEMEntities.BLOCK + "\">"
                + "<span class=\"" + IconWithTextBEMEntities.ELEMENT_ICON + " " + TopicPages_properties.TopicPages_page_icon + "\"></span>"
                + "<a class=\"" + IconWithTextBEMEntities.ELEMENT_TEXT + "\" " + QtipUtil.formatUnsafeQtip(tooltipText) + " href=\"#\" data-topic-action=\"open\">"
                + TopicPages_properties.TopicPages_name
                + "</a>"
                + "<span class=\"" + IconWithTextBEMEntities.BLOCK + "\">"
                + "<a id=\"topicpage-delete-" + id + "\" class=\"" + IconWithTextBEMEntities.ELEMENT_ICON + " " + CoreIcons_properties.trash_bin + "\" style=\"text-decoration:none;\" href=\"#\" title=\"" + TopicPages_properties.TopicPages_deletion_tooltip + "\" data-topic-action=\"delete\"/></span>"
                + "</a>";
        return html;
      }
    }

    if (editorContext._.getSitesService().getPreferredSite()) {
      return "<div class=\"" + IconWithTextBEMEntities.BLOCK + "\"><a href=\"#\" id=\"topicpage-create-" + id + "\"  class=\"" + IconWithTextBEMEntities.ELEMENT_TEXT + "\" data-topic-action=\"create\">"
              + TopicPages_properties.TopicPages_create_link + "</a></div>";
    }
    return TopicPages_properties.TopicPages_no_preferred_site;
  }

  protected onPageColumnClick(grid: TableView, source: any, rowIndex: number, someIndex: number, event: Event): void {
    const data = grid.getStore().getAt(rowIndex).data;
    const id: number = data.topic.getNumericId();
    const pageContent: Content = data.page;
    const action = String(event.getTarget().getAttribute("data-topic-action"));
    if (action === "create") {
      this.updatePage(id, true);
    } else if (action === "open") {
      this.openPage(IdHelper.parseContentId(pageContent));
    } else if (action === "delete") {
      this.deletePage(id, IdHelper.parseContentId(pageContent));
    }
    event.preventDefault();
  }

  #customPageChanged(e: PropertyChangeEvent): void {
    const status: string = e.newValue;
    if (status === "deleted") {
      this.reload();
    }
  }

  /**
   * Called from the page rendered.
   * @param id the content id to open
   */
  openPage(id: number): void {
    const page = UndocContentUtil.getContent("" + id);
    editorContext._.getContentTabManager().openDocument(page);
  }

  /**
   * Called from the page rendered.
   * @param id
   * @param pageId
   */
  deletePage(id: number, pageId: number): void {
    const page = UndocContentUtil.getContent("" + pageId);
    MessageBoxUtil.showPrompt(
      TopicPages_properties.TopicPages_deletion_title,
      StringUtil.format(TopicPages_properties.TopicPages_deletion_text, page.getName()),
      (btn: any): void => {
        if (btn === "ok") {
          if (page.isCheckedOutByCurrentSession()) {
            page.checkIn((): void =>
              editorContext._.getContentTabManager().closeDocument(page),
            );
          }
          this.updatePage(id, false);
        }
      });
  }

  /**
   * Called by the link rendered into the page column.
   * @param id The numeric content id to link/unlink the page for
   * @param create True, if the page should be created. False to delete the linked page.
   */
  updatePage(id: number, create: boolean): void {
    TopicsHelper.loadSettings((settings: Bean): void => {
      const topicPageChannel: Content = settings.get("topicPageChannel");
      if (!topicPageChannel) {
        const siteName = editorContext._.getSitesService().getPreferredSiteName();
        const msg = StringUtil.format(TopicPages_properties.TopicPages_no_channel_configured, siteName);
        MessageBoxWindow.getInstance().alert(TopicPages_properties.TopicPages_no_channel_configured_title, msg);
        return;
      }

      topicPageChannel.invalidate((): void => {
        if (topicPageChannel.isCheckedOutByOther()) {
          const msg = StringUtil.format(TopicPages_properties.TopicPages_root_channel_checked_out_msg,
            topicPageChannel.getName());
          MessageBoxWindow.getInstance().alert(TopicPages_properties.TopicPages_root_channel_checked_out_title, msg);
          return;
        }
        this.selectionExpression.setValue(null);
        const selectedRecord = as(this.#getSelectionModel().getSelection()[0], Model);
        const siteId = editorContext._.getSitesService().getPreferredSiteId();
        TopicsHelper.updatePage(id, siteId, create, (result: any): void =>
          ValueExpressionFactory.create(ContentPropertyNames.PATH, result.topicPagesFolder).loadValue((path: string): void => {
            session._.getConnection().getContentRepository().getChild(path, (child: Content): void => {
              if (child) {
                child.invalidate();
              }
              selectedRecord.data.rendered = false;
              selectedRecord.data.page = result.page;
              selectedRecord.commit(false);

              this.selectionExpression.setValue(selectedRecord);

              const root: Content = result.rootChannel;
              if (!root) {
                const msg = StringUtil.format(TopicPages_properties.TopicPages_root_channel_not_found_msg, editorContext._.getSitesService().getPreferredSiteName());
                MessageBoxWindow.getInstance().alert(TopicPages_properties.TopicPages_root_channel_not_found_title, msg);
              }
              if (result.page) {
                editorContext._.getContentTabManager().openDocuments([result.page], true);
              }
            });
          }),
        );
      });
    });
  }

  /**
   * Remove registered listeners.
   */
  protected override onDestroy(): void {
    this.#removePropertyChangeListeners();
    super.onDestroy();
    editorContext._.getSitesService().getPreferredSiteIdExpression().removeChangeListener(bind(this, this.#siteSelectionChanged));
    TabChangePluginBase.getWorkAreaTabChangeExpression().removeChangeListener(bind(this, this.#workAreaTabChanged));
  }

  #removePropertyChangeListeners(): void {
    const topics: Array<any> = this.getTopicsExpression().getValue();
    topics && topics.forEach((data: any): void => {
      const pageContent: Content = data.page;
      if (pageContent) {
        pageContent.removePropertyChangeListener(ContentPropertyNames.LIFECYCLE_STATUS, bind(this, this.#customPageChanged));
      }
    });
  }
}

export default TopicsPanelBase;
