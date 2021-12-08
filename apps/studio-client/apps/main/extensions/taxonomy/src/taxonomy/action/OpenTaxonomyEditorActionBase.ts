import EventUtil from "@coremedia/studio-client.client-core/util/EventUtil";
import Editor_properties from "@coremedia/studio-client.main.editor-components/Editor_properties";
import WorkArea from "@coremedia/studio-client.main.editor-components/sdk/desktop/WorkArea";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Ext from "@jangaroo/ext-ts";
import Action from "@jangaroo/ext-ts/Action";
import Component from "@jangaroo/ext-ts/Component";
import Button from "@jangaroo/ext-ts/button/Button";
import Panel from "@jangaroo/ext-ts/panel/Panel";
import { as, bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import TaxonomyStudioPlugin from "../../TaxonomyStudioPlugin";
import TaxonomyStudioPluginBase from "../../TaxonomyStudioPluginBase";
import TaxonomyEditor from "../administration/TaxonomyEditor";
import OpenTaxonomyEditorAction from "./OpenTaxonomyEditorAction";

interface OpenTaxonomyEditorActionBaseConfig extends Config<Action> {
}

/** Opens the TaxonomyEditor **/
class OpenTaxonomyEditorActionBase extends Action {
  declare Config: OpenTaxonomyEditorActionBaseConfig;

  #taxonomyId: string = null;

  readonly items: Array<any>;

  constructor(config: Config<OpenTaxonomyEditorAction> = null) {
    config.handler = OpenTaxonomyEditorActionBase.showTaxonomyAdministrationWithLatestSelection;
    super(config);
    this.#taxonomyId = config.taxonomyId;
  }

  override addComponent(comp: Component): void {
    super.addComponent(comp);
    TaxonomyStudioPluginBase.isAdministrationEnabled((enabled: boolean): void => {
      this.setDisabled(!enabled);
      OpenTaxonomyEditorActionBase.#updateTooltip(enabled);
    });

    /**
     * Add site selection listener and destroy the editor if the site has
     * changed and the taxonomy manager is still open.
     */
    editorContext._.getSitesService().getPreferredSiteIdExpression().addChangeListener(bind(this, this.#preferredSiteChangedHandler));
  }

  #preferredSiteChangedHandler(): void {
    TaxonomyStudioPluginBase.isAdministrationEnabled((enabled: boolean): void => {
      this.setDisabled(!enabled); //also update the action on site change
      OpenTaxonomyEditorActionBase.#updateTooltip(enabled);
      if (!enabled) {
        const editor = as(Ext.getCmp("taxonomyEditor"), TaxonomyEditor);
        if (editor) {
          editor.destroy();
        }
      }
    });
  }

  static #updateTooltip(enabled: boolean): void {
    let msg = Editor_properties.Button_disabled_insufficient_privileges;
    if (enabled) {
      msg = "";
    }
    const button = (as(Ext.getCmp(TaxonomyStudioPlugin.TAXONOMY_EDITOR_BUTTON_ID), Button));
    if (button) {
      button.setTooltip(msg);
    }
  }

  static showTaxonomyAdministrationWithLatestSelection(): void {
    OpenTaxonomyEditorActionBase.#openTaxonomyAdministration();

    EventUtil.invokeLater((): void => {
      const taxonomyAdminTab = as(Ext.getCmp("taxonomyEditor"), TaxonomyEditor);
      taxonomyAdminTab.showNodeSelectedNode();
    });
  }

  /**
   * Static call to open the taxonomy admin console.
   */
  static #openTaxonomyAdministration(): void {
    const workArea = as(editorContext._.getWorkArea(), WorkArea);
    const taxonomyAdminTab = as(Ext.getCmp("taxonomyEditor"), TaxonomyEditor);

    if (!taxonomyAdminTab) {
      const workAreaTabType = workArea.getTabTypeById(TaxonomyEditor.xtype);
      workAreaTabType.createTab(null, (tab: Panel): void => {
        const editor = as(tab, TaxonomyEditor);
        workArea.addTab(workAreaTabType, editor);
        workArea.setActiveTab(editor);
      });
    } else {
      workArea.setActiveTab(taxonomyAdminTab);
    }
  }

  override removeComponent(comp: Component): void {
    super.removeComponent(comp);
    if (this.items && this.items.length === 0) {
      editorContext._.getSitesService().getPreferredSiteIdExpression().removeChangeListener(bind(this, this.#preferredSiteChangedHandler));
    }
  }
}

export default OpenTaxonomyEditorActionBase;
