import Ext from "@jangaroo/ext-ts";
import Button from "@jangaroo/ext-ts/button/Button";
import Config from "@jangaroo/runtime/Config";

/**
 * A Button that displays its text only as overflowText. This Button also displays two icons as inline placed elements.
 */
class LetterButton extends Button {

  constructor(config: Config<Button> = null) {
    super(Config(Button, Ext.apply({
      cls: "cm-taxonomy-letter-button",
      buttonSelector: "a",
    }, config)));
    this.xtype = this.xtype || Button["xtype"]; // set to default xtype when created through Action!
    this.overflowText = this.getText();
  }

}

export default LetterButton;
