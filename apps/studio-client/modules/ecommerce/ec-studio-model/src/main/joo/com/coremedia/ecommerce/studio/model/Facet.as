package com.coremedia.ecommerce.studio.model {
import com.coremedia.ui.data.impl.BeanImpl;

/**
 * A search facet with all it's possible filter/query values.
 */
public class Facet extends BeanImpl {
  public function Facet(data:Object) {
    //ensure that the key can be stored within a struct property
    data.key = data.key.replaceAll('\.', '_');
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
