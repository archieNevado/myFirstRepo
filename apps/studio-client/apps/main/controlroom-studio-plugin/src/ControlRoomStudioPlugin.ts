import FreshnessColumn from "@coremedia/studio-client.ext.cap-base-components/columns/FreshnessColumn";
import AddItemsPlugin from "@coremedia/studio-client.ext.ui-components/plugins/AddItemsPlugin";
import ProcessDefinitions_properties from "@coremedia/studio-client.ext.workflow-components/components/ProcessDefinitions_properties";
import ControlRoomPlugin from "@coremedia/studio-client.main.control-room-editor-components/ControlRoomPlugin";
import ConfigureProjectListViewPlugin from "@coremedia/studio-client.main.control-room-editor-components/project/ConfigureProjectListViewPlugin";
import ProjectContentContainer from "@coremedia/studio-client.main.control-room-editor-components/project/components/ProjectContentContainer";
import ProjectContentControlsToolbar from "@coremedia/studio-client.main.control-room-editor-components/project/components/ProjectContentControlsToolbar";
import ProjectListViewTypeIconColumn from "@coremedia/studio-client.main.control-room-editor-components/project/components/ProjectListViewTypeIconColumn";
import WorkflowStateTransition from "@coremedia/studio-client.main.control-room-editor-components/workflow/WorkflowStateTransition";
import WorkflowUtils from "@coremedia/studio-client.main.control-room-editor-components/workflow/WorkflowUtils";
import AddTranslationWorkflowPlugin from "@coremedia/studio-client.main.control-room-editor-components/workflow/translation/AddTranslationWorkflowPlugin";
import DefaultStartSynchronizationWorkflowForm from "@coremedia/studio-client.main.control-room-editor-components/workflow/translation/DefaultStartSynchronizationWorkflowForm";
import DefaultStartTranslationWorkflowForm from "@coremedia/studio-client.main.control-room-editor-components/workflow/translation/DefaultStartTranslationWorkflowForm";
import DefaultTranslationWorkflowDetailForm from "@coremedia/studio-client.main.control-room-editor-components/workflow/translation/DefaultTranslationWorkflowDetailForm";
import DefaultTranslationWorkflowInfoForm from "@coremedia/studio-client.main.control-room-editor-components/workflow/translation/DefaultTranslationWorkflowInfoForm";
import CopyResourceBundleProperties from "@coremedia/studio-client.main.editor-components/configuration/CopyResourceBundleProperties";
import StudioPlugin from "@coremedia/studio-client.main.editor-components/configuration/StudioPlugin";
import ListViewCreationDateColumn from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/ListViewCreationDateColumn";
import ListViewNameColumn from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/ListViewNameColumn";
import ListViewSiteColumn from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/ListViewSiteColumn";
import ListViewSiteLocaleColumn from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/ListViewSiteLocaleColumn";
import ListViewStatusColumn from "@coremedia/studio-client.main.editor-components/sdk/collectionview/list/ListViewStatusColumn";
import Separator from "@jangaroo/ext-ts/toolbar/Separator";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import resourceManager from "@jangaroo/runtime/l10n/resourceManager";
import BlueprintProcessDefinitions_properties from "./BlueprintProcessDefinitions_properties";
import BookmarkProjectContentViewPlugin from "./BookmarkProjectContentViewPlugin";
import ProjectQuickCreateLinklistMenu from "./ProjectQuickCreateLinklistMenu";

interface ControlRoomStudioPluginConfig extends Config<StudioPlugin> {
}

class ControlRoomStudioPlugin extends StudioPlugin {
  declare Config: ControlRoomStudioPluginConfig;

  static readonly xtype: string = "com.coremedia.blueprint.studio.config.controlroom.controlRoomStudioPlugin";

  constructor(config: Config<ControlRoomStudioPlugin> = null) {
    super(ConfigUtils.apply(Config(ControlRoomStudioPlugin, {

      rules: [
        Config(ProjectContentControlsToolbar, {
          plugins: [
            Config(AddItemsPlugin, {
              items: [
                Config(Separator),
                Config(ProjectQuickCreateLinklistMenu),
              ],
            }),
          ],
        }),
        Config(ProjectContentContainer, {
          plugins: [
            Config(BookmarkProjectContentViewPlugin),
          ],
        }),
      ],

      configuration: [
        new ControlRoomPlugin({}),

        new ConfigureProjectListViewPlugin({
          projectListViewColumns: [
            Config(ProjectListViewTypeIconColumn, {
              showTypeName: true,
              sortable: true,
            }),
            Config(ListViewNameColumn, { sortable: true }),
            Config(ListViewSiteColumn, { sortable: true }),
            Config(ListViewSiteLocaleColumn, { sortable: true }),
            Config(ListViewCreationDateColumn, { sortable: true }),
            Config(FreshnessColumn, {
              hidden: true,
              sortable: true,
            }),
            Config(ListViewStatusColumn, { sortable: true }),
          ],
        }),

        /* configure the UI for the translation workxflow */
        new AddTranslationWorkflowPlugin({
          processDefinitionName: "Translation",
          listToolbarButtonsFunction: WorkflowUtils.getTranslationToolbarButtons(),
          inboxForm: Config(DefaultTranslationWorkflowDetailForm, {
            workflowStateTransitions: [
              new WorkflowStateTransition({
                task: "Translate",
                nextSteps: [{
                  nextStep: "sendToTranslationService",
                  allowAlways: false,
                }, {
                  nextStep: "rollbackTranslation",
                  allowAlways: true,
                }, {
                  nextStep: "finishTranslation",
                  allowAlways: false,
                }],
                defaultStep: "finishTranslation",
              }),
              new WorkflowStateTransition({
                task: "TranslateSelf",
                nextSteps: [{
                  nextStep: "sendToTranslationService",
                  allowAlways: false,
                }, {
                  nextStep: "rollbackTranslation",
                  allowAlways: true,
                }, {
                  nextStep: "finishTranslation",
                  allowAlways: false,
                }],
                defaultStep: "finishTranslation",
              }),
              new WorkflowStateTransition({
                task: "Review",
                nextSteps: [{
                  nextStep: "translationReviewed",
                  allowAlways: false,
                }],
              }),
            ],
          }),

          pendingForm: Config(DefaultTranslationWorkflowInfoForm, { showTranslationStatus: true }),

          finishedForm: Config(DefaultTranslationWorkflowInfoForm),

          startForm: Config(DefaultStartTranslationWorkflowForm),
        }),
        new AddTranslationWorkflowPlugin({
          processDefinitionName: "Synchronization",
          listToolbarButtonsFunction: WorkflowUtils.getTranslationToolbarButtons(),
          inboxForm: Config(DefaultTranslationWorkflowDetailForm, {
            workflowStateTransitions: [
              new WorkflowStateTransition({
                task: "Synchronize",
                nextSteps: [{
                  nextStep: "finishSynchronization",
                  allowAlways: false,
                }],
                defaultStep: "finishSynchronization",
              }),
            ],
          }),

          pendingForm: Config(DefaultTranslationWorkflowInfoForm, { showTranslationStatus: true }),

          finishedForm: Config(DefaultTranslationWorkflowInfoForm),

          startForm: Config(DefaultStartSynchronizationWorkflowForm),
        }),

        new CopyResourceBundleProperties({
          destination: resourceManager.getResourceBundle(null, ProcessDefinitions_properties),
          source: resourceManager.getResourceBundle(null, BlueprintProcessDefinitions_properties),
        }),
      ],

    }), config));
  }
}

export default ControlRoomStudioPlugin;
