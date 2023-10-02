import ValueExpressionFactory from "@coremedia/studio-client.client-core/data/ValueExpressionFactory";
import BindListPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindListPlugin";
import BindPropertyPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindPropertyPlugin";
import BindVisibilityPlugin from "@coremedia/studio-client.ext.ui-components/plugins/BindVisibilityPlugin";
import DataField from "@coremedia/studio-client.ext.ui-components/store/DataField";
import SortTypes from "@jangaroo/ext-ts/data/SortTypes";
import { bind } from "@jangaroo/runtime";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import LivecontextStudioPlugin_properties from "../LivecontextStudioPlugin_properties";
import CommerceWorkspaceSelectorBase from "./CommerceWorkspaceSelectorBase";

interface CommerceWorkspaceSelectorConfig extends Config<CommerceWorkspaceSelectorBase> {
}

/**
 * @deprecated This class is part of the commerce integration "workspaces support" that is not
 * supported by the Commerce Hub architecture. It will be removed or changed in the future.
 * @deprecated
 */
class CommerceWorkspaceSelector extends CommerceWorkspaceSelectorBase {
  declare Config: CommerceWorkspaceSelectorConfig;

  static override readonly xtype: string = "com.coremedia.livecontext.studio.config.commerceWorkspaceSelector";

  constructor(config: Config<CommerceWorkspaceSelector> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    super(ConfigUtils.apply(Config(CommerceWorkspaceSelector, {
      emptyText: LivecontextStudioPlugin_properties.HeaderToolbar_workspaceSelector_empty_text,
      valueField: "id",
      displayField: "name",
      fieldLabel: LivecontextStudioPlugin_properties.HeaderToolbar_workspaceSelector_label,
      labelAlign: "top",
      labelSeparator: "",

      ...ConfigUtils.append({
        plugins: [
          Config(BindVisibilityPlugin, { bindTo: ValueExpressionFactory.createFromFunction(bind(this$, this$.getSelectableWorkspaces)) }),
          Config(BindListPlugin, {
            bindTo: ValueExpressionFactory.createFromFunction(bind(this$, this$.getSelectableWorkspaces)),
            fields: [
              Config(DataField, {
                name: "id",
                encode: false,
              }),
              Config(DataField, {
                name: "name",
                sortType: bind(SortTypes, SortTypes.asUCString),
              }),
            ],
          }),
          Config(BindPropertyPlugin, {
            bindTo: this$.getWorkspaceIdExpression(),
            bidirectional: true,
            componentEvent: "select",
          }),
        ],
      }),

    }), config));
  }
}

export default CommerceWorkspaceSelector;
