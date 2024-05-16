import DocumentTabPanel from "@coremedia/studio-client.main.editor-components/sdk/premular/DocumentTabPanel";
import Config from "@jangaroo/runtime/Config";
import CMProductListForm from "./CMProductListForm";

interface CMProductListFormBaseConfig extends Config<DocumentTabPanel> {
}

class CMProductListFormBase extends DocumentTabPanel {
  declare Config: CMProductListFormBaseConfig;

  constructor(config: Config<CMProductListForm> = null) {
    super(config);
  }
}

export default CMProductListFormBase;
