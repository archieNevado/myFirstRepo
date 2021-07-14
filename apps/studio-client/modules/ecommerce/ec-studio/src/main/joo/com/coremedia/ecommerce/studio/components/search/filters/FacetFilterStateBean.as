package com.coremedia.ecommerce.studio.components.search.filters {
import com.coremedia.ui.data.BeanState;
import com.coremedia.ui.data.impl.BeanImpl;

public class FacetFilterStateBean extends BeanImpl {
  public function FacetFilterStateBean(value:Object = null) {
    super(value);
  }

  public function remove(m:String):void {
    var value:Object = getValueObject();
    var oldValues:Object = Object.assign({}, value);
    delete value[m];
    firePropertyChangeEvents(oldValues, getValueObject(), BeanState.READABLE, BeanState.READABLE);
  }
}
}
