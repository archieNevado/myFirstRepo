import Step from "@coremedia/studio-client.client-core-test-helper/Step";
import { AnyFunction } from "@jangaroo/runtime/types";
import Viewport from "@jangaroo/ext-ts/container/Viewport";
import TeaserOverlayContainer from "@coremedia/studio-client.main.teaser-overlay-components/TeaserOverlayContainer";
import Component from "@jangaroo/ext-ts/Component";

class IgnoreForCKEditor5Step extends Step {
  constructor(viewPortReceiver: ViewportReceiver, msg: string, condition: AnyFunction, callback?: AnyFunction, passThrough?: any) {
    const wrappedCondition: () => boolean = IgnoreForCKEditor5Step.wrapCondition(viewPortReceiver, condition);
    super(msg, wrappedCondition, callback, passThrough);
  }

  static wrapCondition(viewPortReceiver: ViewportReceiver, condition: AnyFunction): () => boolean {
    return (): boolean => {
      if (IgnoreForCKEditor5Step.isCKEditor5Active(viewPortReceiver())) {
        console.log("CKEditor 5 is active, ignore this test step.")
        return true;
      }
      return condition();
    }
  }

  static isCKEditor5Active(viewPort: Viewport): boolean {
    const teaserOverlayContainers = viewPort.queryBy((component): Component | false => {
      if (component instanceof TeaserOverlayContainer) {
        return component;
      }
      return false;
    });
    return teaserOverlayContainers.length > 0;
  }
}

export type ViewportReceiver = () => Viewport;

export default IgnoreForCKEditor5Step;
