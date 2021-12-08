import AbstractCatalogTest from "@coremedia-blueprint/studio-client.main.ec-studio-test-helper/AbstractCatalogTest";
import Bean from "@coremedia/studio-client.client-core/data/Bean";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import editorContext from "@coremedia/studio-client.main.editor-components/sdk/editorContext";
import LivecontextCollectionViewActionsPlugin from "../src/library/LivecontextCollectionViewActionsPlugin";

class AbstractLiveContextStudioTest extends AbstractCatalogTest {
  protected preferences: Bean = null;

  override setUp(): void {
    super.setUp();

    this.preferences = beanFactory._.createLocalBean();
    editorContext._["setPreferences"](this.preferences);

    this.resetCatalogHelper();

    new LivecontextCollectionViewActionsPlugin();
  }
}

export default AbstractLiveContextStudioTest;
