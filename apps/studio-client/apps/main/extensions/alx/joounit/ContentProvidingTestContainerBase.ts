import ValueExpression from "@coremedia/studio-client.client-core/data/ValueExpression";
import Container from "@jangaroo/ext-ts/container/Container";
import Config from "@jangaroo/runtime/Config";

interface ContentProvidingTestContainerBaseConfig extends Config<Container>, Partial<Pick<ContentProvidingTestContainerBase,
  "contentValueExpression"
>> {
}

class ContentProvidingTestContainerBase extends Container {
  declare Config: ContentProvidingTestContainerBaseConfig;

  contentValueExpression: ValueExpression = null;

  constructor(config: Config<Container> = null) {
    super(config);
  }

  setContent(content: any): void {
    this.contentValueExpression.setValue(content);
  }

}

export default ContentProvidingTestContainerBase;
