import ButtonSkin from "@coremedia/studio-client.ext.ui-components/skins/ButtonSkin";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import TaxonomyNode from "../TaxonomyNode";
import TextLinkButtonBase from "./TextLinkButtonBase";

interface TextLinkButtonConfig extends Config<TextLinkButtonBase>, Partial<Pick<TextLinkButton,
  "node" |
  "addable"
>> {
}

class TextLinkButton extends TextLinkButtonBase {
  declare Config: TextLinkButtonConfig;

  static override readonly xtype: string = "com.coremedia.blueprint.studio.config.taxonomy.textLinkButton";

  constructor(config: Config<TextLinkButton> = null) {
    super(ConfigUtils.apply(Config(TextLinkButton, {
      scale: "small",
      ui: ButtonSkin.SIMPLE.getSkin(),

    }), config));
  }

  node: TaxonomyNode = null;

  addable: boolean = false;
}

export default TextLinkButton;
