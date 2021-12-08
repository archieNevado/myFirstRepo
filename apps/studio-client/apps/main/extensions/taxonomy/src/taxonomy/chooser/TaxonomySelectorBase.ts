import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import IconDisplayField from "@coremedia/studio-client.ext.ui-components/components/IconDisplayField";
import IconDisplayFieldSkin from "@coremedia/studio-client.ext.ui-components/skins/IconDisplayFieldSkin";
import WorkArea from "@coremedia/studio-client.main.editor-components/sdk/desktop/WorkArea";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Ext from "@jangaroo/ext-ts";
import Button from "@jangaroo/ext-ts/button/Button";
import Container from "@jangaroo/ext-ts/container/Container";
import ExtEvent from "@jangaroo/ext-ts/event/Event";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import { AnyFunction } from "@jangaroo/runtime/types";
import TaxonomyNode from "../TaxonomyNode";
import TaxonomyNodeFactory from "../TaxonomyNodeFactory";
import TaxonomyNodeList from "../TaxonomyNodeList";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import LetterButton from "./LetterButton";
import TaxonomySelector from "./TaxonomySelector";
import TextLinkButton from "./TextLinkButton";

interface TaxonomySelectorBaseConfig extends Config<Container>, Partial<Pick<TaxonomySelectorBase,
  "loadingExpression" |
  "nodePathExpression" |
  "taxonomyIdExpression"
>> {
}

/**
 * The taxonomy selector panel that steps through the hierarchy of a taxonomy type.
 */
class TaxonomySelectorBase extends Container {
  declare Config: TaxonomySelectorBaseConfig;

  loadingExpression: ValueExpression = null;

  nodePathExpression: ValueExpression = null;

  #ALPHABET: Array<any> = null;

  #activeLettersVE: ValueExpression = null;

  #selectedLetterVE: ValueExpression = null;

  #selectedNodeListVE: ValueExpression = null;

  taxonomyIdExpression: ValueExpression = null;

  #activePathList: TaxonomyNodeList = null;

  #buttonCache: Array<any> = null;

  constructor(config: Config<TaxonomySelector> = null) {
    super((()=>{
      this.#ALPHABET = [];
      this.#ALPHABET[0] = "A";
      this.#ALPHABET[1] = "B";
      this.#ALPHABET[2] = "C";
      this.#ALPHABET[3] = "D";
      this.#ALPHABET[4] = "E";
      this.#ALPHABET[5] = "F";
      this.#ALPHABET[6] = "G";
      this.#ALPHABET[7] = "H";
      this.#ALPHABET[8] = "I";
      this.#ALPHABET[9] = "J";
      this.#ALPHABET[10] = "K";
      this.#ALPHABET[11] = "L";
      this.#ALPHABET[12] = "M";
      this.#ALPHABET[13] = "N";
      this.#ALPHABET[14] = "O";
      this.#ALPHABET[15] = "P";
      this.#ALPHABET[16] = "Q";
      this.#ALPHABET[17] = "R";
      this.#ALPHABET[18] = "S";
      this.#ALPHABET[19] = "T";
      this.#ALPHABET[20] = "U";
      this.#ALPHABET[21] = "V";
      this.#ALPHABET[22] = "W";
      this.#ALPHABET[23] = "X";
      this.#ALPHABET[24] = "Y";
      this.#ALPHABET[25] = "Z";
      return config;
    })());
  }

  /**
   * Adds missing components to the container like the button list.
   */
  protected override initComponent(): void {
    super.initComponent();
    this.#buttonCache = [];

    const alphabetPanel = as(this.queryById("alphabetPanel"), Container);
    for (let i = 0; i < this.#ALPHABET.length; i++) {
      const letter: string = this.#ALPHABET[i];
      const alphaButton = new LetterButton(Config(Button, {
        text: letter,
        disabled: true,
        flex: 1,
        handler: bind(this, this.#buttonClicked),
      }));
      alphabetPanel.add(alphaButton);
      this.#buttonCache.push(alphaButton);
    }

    this.nodePathExpression.addChangeListener(bind(this, this.#updateLevel));
  }

  /**
   * Updates the status of the letter buttons, depending on the active selection.
   */
  #updateLetters(): void {
    const activeLetters: Array<any> = this.#activeLettersVE.getValue();

    for (let i = 0; i < this.#buttonCache.length; i++) {
      const alphaButton: LetterButton = this.#buttonCache[i];
      alphaButton.setDisabled(true);
      const letter = alphaButton.getText().toLowerCase();
      for (let j = 0; j < activeLetters.length; j++) {
        const activeLetter: string = activeLetters[j];
        if (activeLetter === letter) {
          alphaButton.setDisabled(false);
          break;
        }
      }
    }
  }

  /**
   * Creates the value expression that contains an array with the active letters.
   * @return
   */
  protected getActiveLettersExpression(): ValueExpression {
    if (!this.#activeLettersVE) {
      this.#activeLettersVE = ValueExpressionFactory.create("letters", beanFactory._.createLocalBean());
      this.#activeLettersVE.addChangeListener(bind(this, this.#updateLetters));
    }
    return this.#activeLettersVE;
  }

  /**
   * Creates the value expression that contains the taxonomy node list
   * @return
   */
  protected getSelectedNodeListValueExpression(): ValueExpression {
    if (!this.#selectedNodeListVE) {
      this.#selectedNodeListVE = ValueExpressionFactory.create("nodeList", beanFactory._.createLocalBean());
    }
    return this.#selectedNodeListVE;
  }

  /**
   * Creates the value expression that contains the active selected button, if user clicked one.
   * @return
   */
  protected getSelectedLetterExpression(): ValueExpression {
    if (!this.#selectedLetterVE) {
      this.#selectedLetterVE = ValueExpressionFactory.create("letter", beanFactory._.createLocalBean());
    }
    return this.#selectedLetterVE;
  }

  /**
   * Fire when the user double clicked a node, so that the next sub-level/children are shown.
   */
  #updateLevel(): void {
    this.loadingExpression.setValue(true);
    const ref: string = this.nodePathExpression.getValue();
    if (ref) {
      const content: Content = WorkArea.ACTIVE_CONTENT_VALUE_EXPRESSION.getValue();
      const siteId = editorContext._.getSitesService().getSiteIdFor(content);
      const taxonomyId: string = this.taxonomyIdExpression.getValue();
      if (ref === taxonomyId) {
        //update the list with the root children
        TaxonomyNodeFactory.loadTaxonomyRoot(siteId, taxonomyId, (parent: TaxonomyNode): void =>
          parent.loadChildren(false, (list: TaxonomyNodeList): void => {
            this.getSelectedNodeListValueExpression().setValue(list);
          }),
        );
        //we do not have to build the path for the root taxonomy, since this only exists of the taxonomy id value.
        this.#updatePathPanel(null, null);
      } else {
        //update the list with a regular child
        const currentList: TaxonomyNodeList = this.#selectedNodeListVE.getValue();
        let newSelection = currentList.getNode(ref);
        if (!newSelection) {
          /**
           * the update was triggered by a path item, so the selection node list (contains children of a path node),
           * so the active ref won't be found in the list.
           * Instead we look up the ref in the path list, this an item of this list was selected.
           */
          newSelection = this.#activePathList.getNode(ref);
        }
        if (!newSelection.isLeaf()) {//do not show children of leafs, which are empty of course).

          //update the path of the current selection, this will build the path above the list
          TaxonomyNodeFactory.loadPath(taxonomyId, ref, siteId,
            (list: TaxonomyNodeList): void =>
              this.#updatePathPanel(list, (): void =>
                newSelection.loadChildren(false, (list: TaxonomyNodeList): void => {
                  this.getSelectedNodeListValueExpression().setValue(list);
                }),
              ),
          );
        }
      }
    }
  }

  /**
   * Displays the current path selection.
   * @param list the node list to render the path for
   * @param callback the optional callback called after update
   */
  #updatePathPanel(list: TaxonomyNodeList, callback: AnyFunction): void {
    Ext.suspendLayouts();
    this.#activePathList = list;
    const pathPanel = this.#getPathPanel();
    pathPanel.removeAll(true);

    //Add root
    let text: string = this.taxonomyIdExpression.getValue();
    const taxonomyId: string = this.taxonomyIdExpression.getValue();
    const rootName = TaxonomyStudioPlugin_properties[taxonomyId];
    if (rootName) {
      text = rootName;
    }

    const root = new TextLinkButton(Config(TextLinkButton, {
      text: text,
      itemId: taxonomyId.replace(/\s+/g, ""),
      handler: bind(this, this.#doSetLevel),
      node: new TaxonomyNode({
        name: text,
        ref: taxonomyId,
        taxonomyId: taxonomyId,
      }),
    }));

    //add each level incl. root
    if (this.#activePathList && this.#activePathList.getNodes()) {
      const nodes = this.#activePathList.getNodes();
      let index = 0;
      for (const node of nodes as TaxonomyNode[]) {
        if (index > 0) {
          const ref = node.getRef().replace("/", "-").replace(/\s+/g, "");//format valid itemId
          if (index == this.#activePathList.getNodes().length - 1) {
            const label = new IconDisplayField(Config(IconDisplayField, {
              value: node.getDisplayName(),
              iconCls: CoreIcons_properties.arrow_right,
            }));
            label.setUI(IconDisplayFieldSkin.DEFAULT.getSkin());
            pathPanel.add(label);
          } else {
            const pathItem = new TextLinkButton(Config(TextLinkButton, {
              node: node,
              itemId: ref,
              handler: bind(this, this.#doSetLevel),
            }));
            pathItem.setIconCls(CoreIcons_properties.arrow_right);
            pathPanel.add(pathItem);
          }
        } else {
          // add root
          pathPanel.add(root);
        }

        index++;
      }
    } else {
      // add root
      pathPanel.add(root);
    }

    Ext.resumeLayouts(true);

    //refresh the layout
    pathPanel.updateLayout();
    //scroll right
    pathPanel.layout.overflowHandler.scrollTo(4000, true);

    if (callback) {
      callback();
    }
  }

  //noinspection JSUnusedLocalSymbols
  /**
   * The path selector item handler.
   */
  #doSetLevel(button: TextLinkButton, e: ExtEvent): void {
    const nodeRef = button.node.getRef();
    this.nodePathExpression.setValue(nodeRef);
  }

  /**
   * Returns the path panel that contains the path and level selectors.
   * @return
   */
  #getPathPanel(): Container {
    return as(this.queryById("levelSelector"), Container);
  }

  //noinspection JSUnusedLocalSymbols
  /**
   * Applies the active button to the selected letter expression, so that the link list is updated.
   * @param b The button component.
   * @param e The js event.
   */
  #buttonClicked(b: Button, e: ExtEvent): void {
    const letter = b.getText();
    this.getSelectedLetterExpression().setValue(letter.toLowerCase());
  }
}

export default TaxonomySelectorBase;
