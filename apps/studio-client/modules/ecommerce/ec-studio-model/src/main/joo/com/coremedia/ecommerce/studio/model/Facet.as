package com.coremedia.ecommerce.studio.model {
import com.coremedia.ui.data.impl.BeanImpl;

public class Facet extends BeanImpl {
  public function Facet(data:Object) {
    super(data);
  }

  public function getKey():String {
    return this.get('key');
  }

  public function getValues():Array {
    return this.get('values');
  }

  public function getLabel():String {
    return this.get('label');
  }

  public function isMultiSelect():Boolean {
    return this.get('multiSelect');
  }
}
}
