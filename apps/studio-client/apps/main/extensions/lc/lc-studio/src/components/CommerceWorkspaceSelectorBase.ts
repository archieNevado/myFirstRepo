import Store from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Store";
import Workspace from "@coremedia-blueprint/studio-client.main.ec-studio-model/model/Workspace";
import CatalogHelper from "@coremedia-blueprint/studio-client.main.ec-studio/helper/CatalogHelper";
import Struct from "@coremedia/studio-client.cap-rest-client/struct/Struct";
import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import LocalComboBox from "@coremedia/studio-client.ext.ui-components/components/LocalComboBox";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import Site from "@coremedia/studio-client.multi-site-models/Site";
import StringUtil from "@jangaroo/ext-ts/String";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";
import CommerceWorkspaceSelector from "./CommerceWorkspaceSelector";

interface CommerceWorkspaceSelectorBaseConfig extends Config<LocalComboBox> {
}

/**
 * @deprecated This class is part of the commerce integration "workspaces support" that is not
 * supported by the Commerce Hub architecture. It will be removed or changed in the future.
 * @deprecated
 */
class CommerceWorkspaceSelectorBase extends LocalComboBox {
  declare Config: CommerceWorkspaceSelectorBaseConfig;

  static readonly #NO_WORKSPACE: string = "no-workspace";

  static readonly #NO_WORKSPACE_ENTRY: Record<string, any> = {
    id: CommerceWorkspaceSelectorBase.#NO_WORKSPACE,
    name: LivecontextStudioPlugin_properties.commerce_workspace_no_workspace,
  };

  constructor(config: Config<CommerceWorkspaceSelector> = null) {
    super(config);
    const activeSiteExpression = ValueExpressionFactory.createFromFunction((): Site =>
      editorContext._.getSitesService().getPreferredSite(),
    );

    //reset the workspace when changing the site
    activeSiteExpression.addChangeListener((): void => {
      this.#setWorkspaceId(CommerceWorkspaceSelectorBase.#NO_WORKSPACE);
      this.setValue(undefined);
    });
  }

  override setValue(value: any): this {
    //TODO: Workaround for CMS-9822: Somehow the bindListPlugin tries to update the store after the first selection of a workspace
    //which leads to null value
    //the possible cause of the problem is that the list depends on the active store which again depends on the editor preference
    //and we write the workspace id in the same editor preference document.
    if (value === null) {
      return this.getValue();
    } else {
      return super.setValue(value);
    }
  }

  override validate(): boolean {
    this.#validateId();
    return this.#isWorkspaceValid();
  }

  getSelectableWorkspaces(): Array<any> {
    const workspaces = this.#getWorkspaces();
    if (!workspaces || workspaces.length === 0) {
      return undefined;
    } else {
      //when there is at least one workspace add a virtual entry 'no workspace'
      const selectableWorkspaces = [CommerceWorkspaceSelectorBase.#NO_WORKSPACE_ENTRY];
      return selectableWorkspaces.concat(workspaces);
    }
  }

  getWorkspaceIdExpression(): ValueExpression {
    const expression = ValueExpressionFactory.createFromFunction(bind(this, this.#getWorkspaceId));
    expression.setValue = bind(this, this.#setWorkspaceId);
    return expression;
  }

  #validateId(): void {
    if (this.#isWorkspaceValid()) {
      this.clearInvalid();
    } else {
      this.markInvalid(this.#getErrorMessage(CatalogHelper.getInstance().getCommerceWorkspaceExpression().getValue()));
    }
  }

  #getWorkspaceId(): string {
    const workspaceId: string = CatalogHelper.getInstance().getCommerceWorkspaceExpression().getValue();
    if (!this.#isWorkspaceValid()) {
      return this.#getErrorMessage(workspaceId);
    }
    return workspaceId;
  }

  //noinspection JSMethodCanBeStatic
  #getErrorMessage(workspaceId: string): string {
    const extractedWorkspaceId = workspaceId.substr(workspaceId.lastIndexOf("/") + 1, workspaceId.length);
    const errorMessage = StringUtil.format(LivecontextStudioPlugin_properties.commerce_workspace_invalid, extractedWorkspaceId);
    return errorMessage;
  }

  #isWorkspaceValid(): boolean {
    let workspaceValid = true;
    const workspaceId: string = CatalogHelper.getInstance().getCommerceWorkspaceExpression().getValue();
    if (workspaceId) {
      const workspaces = this.#getWorkspaces();
      if (workspaces) {
        workspaceValid = workspaces.some((workspace: Workspace): boolean =>
          workspaceId === workspace.getId(),
        );
      }
    }
    return workspaceValid;
  }

  //noinspection JSMethodCanBeStatic
  #setWorkspaceId(workspaceId: string): void {
    const workspaceExpression = CatalogHelper.getInstance().getCommerceWorkspaceExpression();
    if (workspaceId === CommerceWorkspaceSelectorBase.#NO_WORKSPACE) {
      // unset the workspace id if 'no-workspace' selected
      // remove the commerce.workspace struct entry
      const commerceStruct: Struct = workspaceExpression.getParent().getValue();
      if (commerceStruct) {
        commerceStruct.getType().removeProperty(CatalogHelper.COMMERCE_STRUCT_WORKSPACE);
      }
    } else {
      workspaceExpression.setValue(workspaceId);
    }
  }

  //noinspection JSMethodCanBeStatic
  #getWorkspaces(): Array<any> {
    let workspaces: Array<any>;
    const activeStore: Store = CatalogHelper.getInstance().getActiveStoreExpression().getValue();
    if (activeStore) {
      if (activeStore.getWorkspaces()) {
        workspaces = activeStore.getWorkspaces().getWorkspaces();
      }
    }
    return workspaces;
  }
}

export default CommerceWorkspaceSelectorBase;
