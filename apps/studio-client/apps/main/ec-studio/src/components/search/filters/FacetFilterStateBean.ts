import BeanImpl from "@coremedia/studio-client.client-core-impl/data/impl/BeanImpl";
import BeanState from "@coremedia/studio-client.client-core/data/BeanState";

class FacetFilterStateBean extends BeanImpl {
  constructor(value: any = null) {
    super(value);
  }

  remove(m: string): void {
    const value = this.getValueObject();
    const oldValues: any = Object.assign({}, value);
    delete value[m];
    this.firePropertyChangeEvents(oldValues, this.getValueObject(), BeanState.READABLE, BeanState.READABLE);
  }
}

export default FacetFilterStateBean;
