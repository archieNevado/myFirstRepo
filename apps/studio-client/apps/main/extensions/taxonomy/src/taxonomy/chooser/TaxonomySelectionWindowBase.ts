import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import StudioDialog from "@coremedia/studio-client.ext.base-components/dialogs/StudioDialog";
import WorkArea from "@coremedia/studio-client.main.editor-components/sdk/desktop/WorkArea";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import { AnyFunction } from "@jangaroo/runtime/types";
import TaxonomyNodeFactory from "../TaxonomyNodeFactory";
import TaxonomyNodeList from "../TaxonomyNodeList";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import TaxonomyUtil from "../TaxonomyUtil";
import LetterListPanel from "./LetterListPanel";
import TaxonomySelectionWindow from "./TaxonomySelectionWindow";

interface TaxonomySelectionWindowBaseConfig extends Config<StudioDialog>, Partial<Pick<TaxonomySelectionWindowBase,
  "taxonomyIdExpression"
>> {
}

/**
 * The base class of the taxonomy selection window.
 */
class TaxonomySelectionWindowBase extends StudioDialog {
  declare Config: TaxonomySelectionWindowBaseConfig;

  #selectionExpression: ValueExpression = null;

  #propertyValueExpression: ValueExpression = null;

  #searchResultExpression: ValueExpression = null;

  #loadingExpression: ValueExpression = null;

  #nodePathExpression: ValueExpression = null;

  #singleSelection: boolean = false;

  taxonomyIdExpression: ValueExpression = null;

  constructor(config: Config<TaxonomySelectionWindow> = null) {
    super(config);
    this.#singleSelection = config.singleSelection;
    this.#propertyValueExpression = config.propertyValueExpression;

    let selection = [];
    const value = this.#propertyValueExpression.getValue();
    if (value) {
      if (this.#singleSelection && as(value, Content)) {
        selection.push(as(value, Content));
      } else {
        selection = as(value, Array);
      }
    }

    this.getSelectionExpression().setValue(selection);
  }

  protected resolveTitle(config: Config<TaxonomySelectionWindow>): string {
    const title = TaxonomyStudioPlugin_properties.taxonomy_selection_dialog_title;
    const taxonomyId: string = config.taxonomyIdExpression.getValue();
    return TaxonomyStudioPlugin_properties["TaxonomyType_" + taxonomyId + "_text"] || title;
  }

  protected getLoadingExpression(): ValueExpression {
    if (!this.#loadingExpression) {
      this.#loadingExpression = ValueExpressionFactory.createFromValue(false);
    }
    return this.#loadingExpression;
  }

  protected getSearchResultExpression(): ValueExpression {
    if (!this.#searchResultExpression) {
      this.#searchResultExpression = ValueExpressionFactory.createFromValue(null);
      this.#searchResultExpression.addChangeListener(bind(this, this.#searchChanged));
    }
    return this.#searchResultExpression;
  }

  protected getNodePathExpression(): ValueExpression {
    if (!this.#nodePathExpression) {
      this.#nodePathExpression = ValueExpressionFactory.create("nodeRef", beanFactory._.createLocalBean());
    }
    return this.#nodePathExpression;
  }

  /**
   * This callback is invoked after the "-" button has been clicked of a node inside the taxonomy link list.
   * We need this callback to check if the corresponding node is rendered in the letter list panel as well and therefore
   * needs a refresh.
   * @param nodeRef the reference of the node that has been removed from the taxonomy link list
   */
  protected removedFromLinkListCallback(nodeRef: string): void {
    const content: Content = WorkArea.ACTIVE_CONTENT_VALUE_EXPRESSION.getValue();
    const siteId = editorContext._.getSitesService().getSiteIdFor(content);
    TaxonomyNodeFactory.loadPath(this.taxonomyIdExpression.getValue(), nodeRef, siteId, (list: TaxonomyNodeList): void => {
      if (this.#isInActiveNodeList(list)) {
        this.#getLetterListPanel().updateUI();
      }
    });
  }

  /**
   * The callback once a selection is made via the search field.
   * An additional computation is made here to check if the selected node
   * is on the same level currently selected in the letter list panel.
   * We only refresh the panel to update the +/- button of the node then.
   *
   * @param ve the value expression that contains the selected node list
   */
  #searchChanged(ve: ValueExpression): void {
    const nodeList: TaxonomyNodeList = ve.getValue();
    const doRefresh = this.#isInActiveNodeList(nodeList);

    //reset previous selection if the dialog is in single selection mode
    if (this.#singleSelection) {
      this.getSelectionExpression().setValue([]);
    }

    const selectedNodeRef: string = ve.getValue().getLeafRef();
    this.updateSelection(selectedNodeRef, false, doRefresh);
  }

  /**
   * Do only refresh if the removed or added node is part of the current path selected in the letter list panel.
   * If the dialog is operating in the single selection mode, we always refresh the nodes since the +/- buttons visibility does toggle.
   *
   * @param nodeList the node list of the node that has been added or removed
   * @return true if the give node list matches the path selection of the letter list panel
   */
  #isInActiveNodeList(nodeList: TaxonomyNodeList): boolean {
    const parentRef = nodeList.getLeafParentRef();
    const currentLevelRef: string = this.#nodePathExpression.getValue();

    //refresh when a node from the selected level is selected (the default root level ref if equals the taxonomy id!)
    return this.#singleSelection || currentLevelRef === parentRef || (nodeList.getNodes().length === 2 && currentLevelRef === this.taxonomyIdExpression.getValue());
  }

  updateSelection(nodeRef: string, doRemove: boolean, doRefresh: boolean): void {
    //invoke after the operation has been executed
    const callback: AnyFunction = (): void => {
      if (doRefresh) {
        this.#getLetterListPanel().updateUI();
      }
    };

    if (!TaxonomyUtil.isInSelection(this.getSelectionExpression().getValue(), nodeRef)) {
      TaxonomyUtil.addNodeToSelection(this.#selectionExpression, nodeRef, callback);
    } else if (doRemove) {
      TaxonomyUtil.removeNodeFromSelection(this.#selectionExpression, nodeRef, callback);
    }
  }

  #getLetterListPanel(): LetterListPanel {
    return as(this.queryById(LetterListPanel.ITEM_ID), LetterListPanel);
  }

  /**
   * Ok button handler.
   */
  protected okPressed(): void {
    const selection: Array<any> = this.#selectionExpression.getValue();
    if (!this.#singleSelection) {
      this.#propertyValueExpression.setValue(selection);
    } else {
      if (selection && selection.length > 0) {
        this.#propertyValueExpression.setValue(selection[0]);
      } else {
        this.#propertyValueExpression.setValue(null);
      }
    }
    this.close();
  }

  /**
   * Cancel button handler.
   */
  protected cancelPressed(): void {
    this.close();
  }

  //noinspection JSMethodCanBeStatic
  /**
   * Depending on single selection mode, a different link list title is displayed
   * for the active selection.
   * @param singleSelection
   * @return
   */
  protected resolveSelectionTitle(singleSelection: boolean): string {
    if (singleSelection) {
      return TaxonomyStudioPlugin_properties.TaxonomyLinkList_singleSelection_title;
    }
    return TaxonomyStudioPlugin_properties.TaxonomyLinkList_title;
  }

  /**
   * Contains the entries selected by the user.
   * @return
   */
  protected getSelectionExpression(): ValueExpression {
    if (!this.#selectionExpression) {
      this.#selectionExpression = ValueExpressionFactory.createFromValue([]);
    }
    return this.#selectionExpression;
  }
}

export default TaxonomySelectionWindowBase;
