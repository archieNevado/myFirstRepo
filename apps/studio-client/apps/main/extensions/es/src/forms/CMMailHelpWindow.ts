import ExtendedDisplayField from "@coremedia/studio-client.ext.ui-components/components/ExtendedDisplayField";
import OverflowBehaviour from "@coremedia/studio-client.ext.ui-components/mixins/OverflowBehaviour";
import ButtonSkin from "@coremedia/studio-client.ext.ui-components/skins/ButtonSkin";
import WindowSkin from "@coremedia/studio-client.ext.ui-components/skins/WindowSkin";
import Component from "@jangaroo/ext-ts/Component";
import Button from "@jangaroo/ext-ts/button/Button";
import VBoxLayout from "@jangaroo/ext-ts/layout/container/VBox";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import ElasticSocialStudioPlugin_properties from "../ElasticSocialStudioPlugin_properties";
import CMMailHelpWindowBase from "./CMMailHelpWindowBase";

interface CMMailHelpWindowConfig extends Config<CMMailHelpWindowBase> {
}

class CMMailHelpWindow extends CMMailHelpWindowBase {
  declare Config: CMMailHelpWindowConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.elastic.social.studio.config.cmMailHelpWindow";

  constructor(config: Config<CMMailHelpWindow> = null) {
    super(ConfigUtils.apply(Config(CMMailHelpWindow, {
      id: CMMailHelpWindowBase.ID,
      width: 500,
      height: 650,
      closeAction: "destroy",
      title: ElasticSocialStudioPlugin_properties.cmmail_help_window_title,
      resizable: false,
      constrainHeader: true,
      constrain: true,
      ui: WindowSkin.GRID_200.getSkin(),
      cls: "es-cm-mail-help-window",

      items: [
        Config(ExtendedDisplayField, {
          value: ElasticSocialStudioPlugin_properties.cmmail_help_window_content,
          overflowBehaviour: OverflowBehaviour.BREAK_WORD,
        }),
        Config(Component, { html: CMMailHelpWindowBase.TABLE }),
      ],

      buttons: [
        Config(Button, {
          ui: ButtonSkin.FOOTER_SECONDARY.getSkin(),
          scale: "small",
          text: ElasticSocialStudioPlugin_properties.btn_close,
          handler: (): void => this.close(),
        }),
      ],
      layout: Config(VBoxLayout, { align: "stretch" }),
    }), config));
  }
}

export default CMMailHelpWindow;
