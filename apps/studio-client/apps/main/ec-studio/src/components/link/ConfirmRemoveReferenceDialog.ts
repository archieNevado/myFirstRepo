import CoreIcons_properties from "@coremedia/studio-client.core-icons/CoreIcons_properties";
import StudioDialog from "@coremedia/studio-client.ext.base-components/dialogs/StudioDialog";
import SpacingBEMEntities from "@coremedia/studio-client.ext.ui-components/bem/SpacingBEMEntities";
import ExtendedDisplayField from "@coremedia/studio-client.ext.ui-components/components/ExtendedDisplayField";
import IconDisplayField from "@coremedia/studio-client.ext.ui-components/components/IconDisplayField";
import OverflowBehaviour from "@coremedia/studio-client.ext.ui-components/mixins/OverflowBehaviour";
import HorizontalSpacingPlugin from "@coremedia/studio-client.ext.ui-components/plugins/HorizontalSpacingPlugin";
import ButtonSkin from "@coremedia/studio-client.ext.ui-components/skins/ButtonSkin";
import DisplayFieldSkin from "@coremedia/studio-client.ext.ui-components/skins/DisplayFieldSkin";
import IconDisplayFieldSkin from "@coremedia/studio-client.ext.ui-components/skins/IconDisplayFieldSkin";
import WindowSkin from "@coremedia/studio-client.ext.ui-components/skins/WindowSkin";
import Button from "@jangaroo/ext-ts/button/Button";
import Container from "@jangaroo/ext-ts/container/Container";
import HBoxLayout from "@jangaroo/ext-ts/layout/container/HBox";
import Toolbar from "@jangaroo/ext-ts/toolbar/Toolbar";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import { AnyFunction } from "@jangaroo/runtime/types";
import ECommerceStudioPlugin_properties from "../../ECommerceStudioPlugin_properties";

interface ConfirmRemoveReferenceDialogConfig extends Config<StudioDialog>, Partial<Pick<ConfirmRemoveReferenceDialog,
  "changeFunction" | "closeCallback">> {}

class ConfirmRemoveReferenceDialog extends StudioDialog {
  declare Config: ConfirmRemoveReferenceDialogConfig;

  static override readonly xtype: string = "com.coremedia.ecommerce.studio.config.confirmRemoveReferenceDialog";

  static readonly APPLY_BUTTON_ITEM_ID: string = "confirmremovereference-apply-button";

  static readonly CANCEL_BUTTON_ITEM_ID: string = "confirmremovereference-cancel-button";

  static DIALOG_ID: string = "removeReferenceDialog";

  static DIALOG_OPTION_CONFIRM: string = "confirm";

  multiWorkflow: boolean = false;

  changeFunction: AnyFunction = null;

  closeCallback: AnyFunction = null;

  constructor(config: Config<ConfirmRemoveReferenceDialog> = null) {
    // @ts-expect-error Ext JS semantics
    const this$ = this;
    super(ConfigUtils.apply(Config(ConfirmRemoveReferenceDialog, {
      modal: true,
      stateful: true,
      resizable: false,
      width: 450,
      ui: WindowSkin.GRID_200.getSkin(),
      title: ECommerceStudioPlugin_properties.Catalog_remove_reference_title,

      items: [
        Config(Container, {
          items: [
            Config(IconDisplayField, {
              scale: "extra-large",
              ui: IconDisplayFieldSkin.EXTRA_LARGE_READONLY.getSkin(),
              iconCls: CoreIcons_properties.warning,
            }),
            Config(ExtendedDisplayField, {
              overflowBehaviour: OverflowBehaviour.BREAK_WORD,
              value: ECommerceStudioPlugin_properties.Catalog_remove_reference_text,
              ui: DisplayFieldSkin.SUB_LABEL.getSkin(),
              flex: 1,
            }),
          ],
          plugins: [
            Config(HorizontalSpacingPlugin, { modifier: SpacingBEMEntities.HORIZONTAL_SPACING_MODIFIER_200 }),
          ],
          layout: Config(HBoxLayout, { align: "stretch" }),
        }),
      ],

      fbar: Config(Toolbar, {
        items: [
          Config(Button, {
            itemId: ConfirmRemoveReferenceDialog.APPLY_BUTTON_ITEM_ID,
            ui: ButtonSkin.FOOTER_PRIMARY.getSkin(),
            scale: "small",
            text: ECommerceStudioPlugin_properties.Catalog_remove_reference_button_confirm,
            handler: (): void => {
              if (this$.changeFunction) {
                this$.changeFunction();
              }
              this$.close() ;
            },
          }),
          Config(Button, {
            itemId: ConfirmRemoveReferenceDialog.CANCEL_BUTTON_ITEM_ID,
            ui: ButtonSkin.FOOTER_SECONDARY.getSkin(),
            scale: "small",
            text: ECommerceStudioPlugin_properties.Catalog_remove_reference_button_abort,
            handler: (): void => {
              if (this$.closeCallback) {
                this$.closeCallback();
              }
              this$.close() ;
            },
          }),
        ],
      }),
    }), config));
  }
}

export default ConfirmRemoveReferenceDialog;
