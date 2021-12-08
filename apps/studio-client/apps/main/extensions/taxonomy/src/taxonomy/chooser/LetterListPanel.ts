import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import BindListPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindListPlugin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import BindDisablePlugin from "@coremedia/studio-client.main.editor-components/sdk/premular/fields/plugins/BindDisablePlugin";
import Container from "@jangaroo/ext-ts/container/Container";
import AnchorLayout from "@jangaroo/ext-ts/layout/container/Anchor";
import DataView from "@jangaroo/ext-ts/view/View";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LetterListPanelBase from "./LetterListPanelBase";

interface LetterListPanelConfig extends Config<LetterListPanelBase>, Partial<Pick<LetterListPanel,
  "activeLetters" |
  "selectedLetter" |
  "selectedNodeList" |
  "selectionExpression" |
  "forceReadOnlyValueExpression" |
  "taxonomyId"
>> {
}

class LetterListPanel extends LetterListPanelBase {
  declare Config: LetterListPanelConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.taxonomy.letterListPanel";

  static readonly ITEM_ID: string = "letterListPanel";

  constructor(config: Config<LetterListPanel> = null) {
    super((()=> ConfigUtils.apply(Config(LetterListPanel, {
      scrollable: true,

      plugins: [
        Config(BindDisablePlugin, {
          bindTo: this.getListValuesExpression(),
          forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
        }),
      ],
      items: [
        Config(Container, {
          flex: 1,
          autoScroll: true,
          items: [
            Config(DataView, {
              itemSelector: LetterListPanelBase.LIST_ELEMENT_ENTRY.getCSSSelector(),
              itemId: this.ITEMS_CONTAINER_ITEM_ID,
              singleSelect: true,
              multiSelect: false,
              deferEmptyText: true,
              tpl: LetterListPanelBase.TEMPLATE,
              plugins: [
                Config(BindListPlugin, {
                  bindTo: this.getListValuesExpression(),
                  fields: [
                    Config(DataField, { name: "letter" }),
                    Config(DataField, { name: "name" }),
                    Config(DataField, { name: "leaf" }),
                    Config(DataField, { name: "ref" }),
                    Config(DataField, { name: "added" }),
                    Config(DataField, { name: "renderControl" }),
                    Config(DataField, { name: "customCss" }),
                  ],
                }),
              ],
            }),
          ],
          layout: Config(AnchorLayout),
        }),
      ],

    }), config))());
  }

  /**
   * Contains an array of letters that are active for the current level.
   */
  activeLetters: ValueExpression = null;

  /**
   * Contains the letter the user has clicked.
   */
  selectedLetter: ValueExpression = null;

  /**
   * Contains node list of the selected parent.
   */
  selectedNodeList: ValueExpression = null;

  /**
   * The value expression that contains the active selected site by this combo.
   */
  selectionExpression: ValueExpression = null;

  /**
   * An optional ValueExpression which makes the component read-only if it is evaluated to true.
   */
  forceReadOnlyValueExpression: ValueExpression = null;

  /** the id of the taxonomy that's tree is used to add items from. */
  taxonomyId: string = null;
}

export default LetterListPanel;
