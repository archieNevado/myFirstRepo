import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ToolbarSkin from "@coremedia/studio-client.ext.ui-components/skins/ToolbarSkin";
import Container from "@jangaroo/ext-ts/container/Container";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import VBoxLayout from "@jangaroo/ext-ts/layout/container/VBox";
import Toolbar from "@jangaroo/ext-ts/toolbar/Toolbar";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LetterListPanel from "./LetterListPanel";
import TaxonomySelectorBase from "./TaxonomySelectorBase";

interface TaxonomySelectorConfig extends Config<TaxonomySelectorBase>, Partial<Pick<TaxonomySelector,
  "selectionExpression" |
  "forceReadOnlyValueExpression" |
  "singleSelection"
>> {
}

class TaxonomySelector extends TaxonomySelectorBase {
  declare Config: TaxonomySelectorConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.taxonomy.taxonomySelector";

  constructor(config: Config<TaxonomySelector> = null) {
    super((()=> ConfigUtils.apply(Config(TaxonomySelector, {

      layout: Config(VBoxLayout, { align: "stretch" }),
      items: [
        /*Selected tag path*/
        Config(Toolbar, {
          itemId: "levelSelector",
          ui: ConfigUtils.asString(ToolbarSkin.FIELD),
          layout: Config(HBoxLayout, { overflowHandler: "scroller" }),
        }),
        /*Alphabetically sorted tag list*/
        Config(Container, {
          flex: 1,
          cls: "cm-taxonomy-selector",
          layout: Config(VBoxLayout, { align: "stretch" }),
          items: [
            /* Column Grid*/
            Config(LetterListPanel, {
              activeLetters: this.getActiveLettersExpression(),
              selectedLetter: this.getSelectedLetterExpression(),
              selectedNodeList: this.getSelectedNodeListValueExpression(),
              loadingExpression: config.loadingExpression,
              selectionExpression: config.selectionExpression,
              singleSelection: config.singleSelection,
              nodePathExpression: config.nodePathExpression,
              taxonomyId: ConfigUtils.asString(config.taxonomyIdExpression.getValue()),
              itemId: LetterListPanel.ITEM_ID,
              flex: 1,
              forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
            }),
            /*Selectable Letter List*/
            Config(Container, {
              itemId: "alphabetPanel",
              layout: Config(HBoxLayout, { align: "stretch" }),
              items: [
                /*Letters will be dynamically added here.*/
              ],
            }),
          ],
        }),
      ],

    }), config))());
  }

  /**
   * The value expression that contains the active selected site by this combo.
   */
  selectionExpression: ValueExpression = null;

  /**
   * An optional ValueExpression which makes the component read-only if it is evaluated to true.
   */
  forceReadOnlyValueExpression: ValueExpression = null;

  /** If true, only one item can be selected from the list. */
  singleSelection: "SINGLE" | "MULTI" = null;
}

export default TaxonomySelector;
