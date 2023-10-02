import LivecontextStudioPlugin_properties from "@coremedia-blueprint/studio-client.main.lc-studio/LivecontextStudioPlugin_properties";
import Ext from "@jangaroo/ext-ts";
import Component from "@jangaroo/ext-ts/Component";
import Element from "@jangaroo/ext-ts/dom/Element";
import Config from "@jangaroo/runtime/Config";
import ManagementCenterFrame from "./ManagementCenterFrame";
import ManagementCenterUtil from "./ManagementCenterUtil";

interface ManagementCenterFrameBaseConfig extends Config<Component> {
}

class ManagementCenterFrameBase extends Component {
  declare Config: ManagementCenterFrameBaseConfig;

  constructor(config: Config<ManagementCenterFrame> = null) {
    super(Config(ManagementCenterFrame, Ext.apply({
      layout: "fit",
      title: LivecontextStudioPlugin_properties.Window_ManagementCenter_title,
    }, config)));
  }

  #setUrl(): void {
    const url = ManagementCenterUtil.getUrl();
    const elem = this.getEl();
    if (elem) {
      elem.set({ src: url });

      // only IE doesn't reload the iframe now, so we have to enforce it:
      if (Ext.isIE) {
        const contentWindow = this.getContentWindow();
        if (contentWindow) {
          contentWindow.location.href = url; // necessary for IE only!
        }
      }
    }
  }

  protected override onRender(parentNode: Element, containerIdx: number): void {
    super.onRender(parentNode, containerIdx);
    this.#setUrl();
    (this as unknown)["el"] = parentNode.createChild({
      tag: "iframe",
      id: "iframe-" + this.getId(),
      frameBorder: 0,
      width: "100%",
      height: "100%",
      src: ManagementCenterUtil.getUrl(),
    });
  }

  getContentWindow(): Window {
    return (this.el && this.el.dom) ? this.el.dom.contentWindow : null;
  }

}

export default ManagementCenterFrameBase;
