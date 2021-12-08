import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import MemoryLinkListWrapper from "@coremedia/studio-client.content-link-list-models/MemoryLinkListWrapper";
import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import SpacingBEMEntities from "@coremedia/studio-client.ext.ui-components/bem/SpacingBEMEntities";
import IconDisplayField from "@coremedia/studio-client.ext.ui-components/components/IconDisplayField";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import VerticalSpacingPlugin from "@coremedia/studio-client.ext.ui-components/plugins/VerticalSpacingPlugin";
import ButtonSkin from "@coremedia/studio-client.ext.ui-components/skins/ButtonSkin";
import TextfieldSkin from "@coremedia/studio-client.ext.ui-components/skins/TextfieldSkin";
import WindowSkin from "@coremedia/studio-client.ext.ui-components/skins/WindowSkin";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import Button from "@jangaroo/ext-ts/button/Button";
import Container from "@jangaroo/ext-ts/container/Container";
import FieldContainer from "@jangaroo/ext-ts/form/FieldContainer";
import FitLayout from "@jangaroo/ext-ts/layout/container/Fit";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import VBoxLayout from "@jangaroo/ext-ts/layout/container/VBox";
import Toolbar from "@jangaroo/ext-ts/toolbar/Toolbar";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TaxonomyStudioPlugin_properties from "../TaxonomyStudioPlugin_properties";
import TaxonomyLinkListGridPanel from "../selection/TaxonomyLinkListGridPanel";
import TaxonomySearchField from "../selection/TaxonomySearchField";
import TaxonomySelectionWindowBase from "./TaxonomySelectionWindowBase";
import TaxonomySelector from "./TaxonomySelector";

interface TaxonomySelectionWindowConfig extends Config<TaxonomySelectionWindowBase>, Partial<Pick<TaxonomySelectionWindow,
  "propertyValueExpression" |
  "bindTo" |
  "siteSelectionExpression" |
  "forceReadOnlyValueExpression" |
  "singleSelection"
>> {
}

class TaxonomySelectionWindow extends TaxonomySelectionWindowBase {
  declare Config: TaxonomySelectionWindowConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.taxonomy.taxonomySelectionWindow";

  static readonly ID: string = "taxonomySelectionDialog";

  constructor(config: Config<TaxonomySelectionWindow> = null) {
    super((()=> ConfigUtils.apply(Config(TaxonomySelectionWindow, {
      id: TaxonomySelectionWindow.ID,
      stateId: "taxonomySelectionState",
      stateful: true,
      title: this.resolveTitle(config),
      width: 510,
      height: 550,
      modal: true,
      constrainHeader: true,
      ui: WindowSkin.GRID_200.getSkin(),

      layout: Config(VBoxLayout, { align: "stretch" }),
      items: [
        Config(Container, {
          items: [
            Config(IconDisplayField, { iconCls: CoreIcons_properties.search }),
            Config(TaxonomySearchField, {
              searchResultExpression: this.getSearchResultExpression(),
              siteSelectionExpression: config.siteSelectionExpression,
              taxonomyIdExpression: config.taxonomyIdExpression,
              resetOnBlur: true,
              cls: "taxonomy-search-field-selector",
              flex: 1,
              emptyText: TaxonomyStudioPlugin_properties.TaxonomyChooser_search_tag_emptyText,
              ui: TextfieldSkin.DEFAULT.getSkin(),
              itemId: "taxonomySearchField",
            }),
          ],
          layout: Config(HBoxLayout, { align: "stretch" }),
        }),

        Config(FieldContainer, {
          labelAlign: "top",
          labelSeparator: "",
          fieldLabel: this.resolveSelectionTitle(config.singleSelection),
          height: 128,
          layout: Config(FitLayout),
          items: [
            Config(TaxonomyLinkListGridPanel, {
              id: "selectedTaxonomies",
              scrollable: true,
              selectionMode: "SINGLE",
              bindTo: config.bindTo,
              removeCallback: bind(this, this.removedFromLinkListCallback),
              taxonomyIdExpression: config.taxonomyIdExpression,
              readOnlyValueExpression: this.getLoadingExpression(),
              emptyText: TaxonomyStudioPlugin_properties.TaxonomyLinkList_empty_chooser_text,
              linkListWrapper: new MemoryLinkListWrapper({
                linksVE: this.getSelectionExpression(),
                linkTypeName: "CMTaxonomy",
              }),
            }),
          ],
        }),
        Config(FieldContainer, {
          labelAlign: "top",
          labelSeparator: "",
          fieldLabel: TaxonomyStudioPlugin_properties.TaxonomyChooser_selection_text,
          flex: 1,
          layout: Config(FitLayout),
          items: [
            Config(TaxonomySelector, {
              selectionExpression: this.getSelectionExpression(),
              singleSelection: config.singleSelection ? "SINGLE" : "MULTI",
              taxonomyIdExpression: config.taxonomyIdExpression,
              loadingExpression: this.getLoadingExpression(),
              nodePathExpression: this.getNodePathExpression(),
              forceReadOnlyValueExpression: config.forceReadOnlyValueExpression,
            }),
          ],
        }),
      ],
      plugins: [
        Config(VerticalSpacingPlugin, { modifier: SpacingBEMEntities.VERTICAL_SPACING_MODIFIER_200 }),
      ],
      fbar: Config(Toolbar, {
        items: [
          Config(Button, {
            ui: ButtonSkin.FOOTER_PRIMARY.getSkin(),
            scale: "small",
            text: Editor_properties.btn_ok,
            handler: bind(this, this.okPressed),
            plugins: [
              Config(BindPropertyPlugin, {
                componentProperty: "disabled",
                bindTo: this.getLoadingExpression(),
              }),
            ],
          }),
          Config(Button, {
            ui: ButtonSkin.FOOTER_SECONDARY.getSkin(),
            scale: "small",
            text: Editor_properties.dialog_defaultCancelButton_text,
            handler: (): void => this.cancelPressed(),
          }),
        ],
      }),
    }), config))());
  }

  /**
   * the property expression to bind in this field
   */
  propertyValueExpression: ValueExpression = null;

  bindTo: ValueExpression = null;

  siteSelectionExpression: ValueExpression = null;

  /**
   * An optional ValueExpression which makes the component read-only if it is evaluated to true.
   */
  forceReadOnlyValueExpression: ValueExpression = null;

  /** If true, only one item can be selected from the list. */
  singleSelection: boolean = null;
}

export default TaxonomySelectionWindow;
