import Button from "@jangaroo/ext-ts/button/Button";
import Config from "@jangaroo/runtime/Config";
import TaxonomyUtil from "../TaxonomyUtil";
import TextLinkButton from "./TextLinkButton";

interface TextLinkButtonBaseConfig extends Config<Button> {
}

/**
 * A Button that displays its text only as overflowText. This Button also displays two icons as inline placed elements.
 */
class TextLinkButtonBase extends Button {
  declare Config: TextLinkButtonBaseConfig;

  constructor(config: Config<TextLinkButton> = null) {
    if (config.node) {
      let name = TaxonomyUtil.escapeHTML(config.node.getName());
      if (config.weight) {
        name = name + " (" + config.weight + ")";
      }
      config.text = name;
    }
    super(config);
  }
}

export default TextLinkButtonBase;
