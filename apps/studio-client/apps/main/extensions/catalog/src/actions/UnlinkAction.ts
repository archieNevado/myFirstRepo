import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Config from "@jangaroo/runtime/Config";
import ConfigUtils from "@jangaroo/runtime/ConfigUtils";
import UnlinkActionBase from "./UnlinkActionBase";

interface UnlinkActionConfig extends Config<UnlinkActionBase>, Partial<Pick<UnlinkAction,
  "folderValueExpression"
>> {
}

/**
 *
 * A <code>contentAction</code> that unlinkds the configured content from its parent.
 * See <code>contentAction</code> for how to configure the content.
 * @see com.coremedia.cms.editor.sdk.config.contentAction
 *
 */
class UnlinkAction extends UnlinkActionBase {
  declare Config: UnlinkActionConfig;

  constructor(config: Config<UnlinkAction> = null) {
    super(ConfigUtils.apply(Config(UnlinkAction), config));
  }

  folderValueExpression: ValueExpression = null;
}

export default UnlinkAction;
