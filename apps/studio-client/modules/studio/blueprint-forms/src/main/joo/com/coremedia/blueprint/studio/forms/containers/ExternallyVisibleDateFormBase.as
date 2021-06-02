package com.coremedia.blueprint.studio.forms.containers {
import com.coremedia.cms.editor.sdk.premular.PropertyFieldGroup;
import com.coremedia.ui.components.StatefulRadio;
import com.coremedia.ui.data.Bean;
import com.coremedia.ui.data.Calendar;
import com.coremedia.ui.data.PropertyChangeEvent;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.beanFactory;
import com.coremedia.ui.mixins.IHighlightableMixin;
import ext.Component;
import ext.layout.container.ContainerLayout;

/**
 * Fires after the externally display date has changed.
 */
[Event(name = "externallyDisplayedDateChanged")] // NOSONAR - no type

public class ExternallyVisibleDateFormBase extends PropertyFieldGroup implements IHighlightableMixin {

  protected static const PUBLICATION_DATE_RADIO_ITEM_ID:String = "publicationDate";
  protected static const OWN_DATE_RADIO_ITEM_ID:String = "ownDate";
  private static const FOCUS:String = 'focus';
  private static const BLUR:String = 'blur';

  internal var model:Bean;

  internal var modelValueExpression:ValueExpression;

  public function ExternallyVisibleDateFormBase(config:ExternallyVisibleDateForm = null) {
    super(config);
  }

  protected override function afterLayout(layout:ContainerLayout):void {
    super.afterLayout(layout);

    // pass the focus and blur events for PDE
    var publicationDateRatioBox:StatefulRadio = getPublicationDateRadioBox();
    if (publicationDateRatioBox) {
      mon(publicationDateRatioBox, FOCUS, function (comp:Component):void {
        if (comp.xtype === StatefulRadio.xtype) {
          fireEvent(FOCUS);
        }
      });
      mon(publicationDateRatioBox, BLUR, function (comp:Component):void {
        if (comp.xtype === StatefulRadio.xtype) {
          fireEvent(BLUR);
        }
      });
    }

    var ownDateRatioBox:StatefulRadio = getOwnDateRadioBox();
    if (ownDateRatioBox) {
      mon(ownDateRatioBox, FOCUS, function (comp:Component):void {
        if (comp.xtype === StatefulRadio.xtype) {
          fireEvent(FOCUS);
        }
      });
      mon(ownDateRatioBox, BLUR, function (comp:Component):void {
        if (comp.xtype === StatefulRadio.xtype) {
          fireEvent(BLUR);
        }
      });
    }
  }

  private function externallyDisplayDateChangeListener(event:PropertyChangeEvent):void {
    getModel().get("properties").set("innerExternallyDisplayedDate", event.newValue);
    fireEvent("externallyDisplayedDateChanged");
  }

  private function innerExternallyDisplayedDateListener(event:PropertyChangeEvent):void {
    getModel().set("externallyDisplayDate", event.newValue);
    if (event.newValue === null) {
      getModel().set("innerUseCustomExternalDisplayedDate", false);
    } else {
      getModel().set("innerUseCustomExternalDisplayedDate", true);
    }
  }

  private function innerUseCustomExternalDisplayedDateListener(event:PropertyChangeEvent):void {
    if (event.newValue === false) {
      getModel().set("archivedDisplayedDate", getModel().get("externallyDisplayDate"));
      getModel().set("externallyDisplayDate", null);
    }
    if (event.newValue === true && getModel().get("archivedDisplayedDate") ) {
      getModel().set("externallyDisplayDate", getModel().get("archivedDisplayedDate"));
    }
  }


  override protected function onDestroy():void {
    getModel().removePropertyChangeListener("externallyDisplayDate", externallyDisplayDateChangeListener);
    getModel().get("properties").removePropertyChangeListener("innerExternallyDisplayedDate", innerExternallyDisplayedDateListener);
    getModel().removePropertyChangeListener("innerUseCustomExternalDisplayedDate", innerUseCustomExternalDisplayedDateListener);

    super.onDestroy();
  }

  internal function getModel():Bean {
    if (!model) {
      model = beanFactory.createLocalBean();
      var innerModel:Bean = beanFactory.createLocalBean();
      innerModel.set("innerExternallyDisplayedDate", null);
      model.set("properties", innerModel);

      model.addPropertyChangeListener("externallyDisplayDate", externallyDisplayDateChangeListener);
      innerModel.addPropertyChangeListener("innerExternallyDisplayedDate", innerExternallyDisplayedDateListener);
      model.addPropertyChangeListener("innerUseCustomExternalDisplayedDate", innerUseCustomExternalDisplayedDateListener);
    }
    return model;
  }

  internal function getModelExpression():ValueExpression {
    if (!modelValueExpression) {
      modelValueExpression = ValueExpressionFactory.createFromValue(getModel());
    }
    return modelValueExpression;
  }

  internal static function toValue(value:String):Boolean {
    return value === 'ownDate';
  }

  public function setExternallyDisplayedDate(displayedDate: Calendar):void {
    getModel().set("externallyDisplayDate", displayedDate);
  }

  public function getExternallyDisplayedDate():Calendar {
    return getModel().get("externallyDisplayDate");
  }

  private function getPublicationDateRadioBox():StatefulRadio {
    return queryById(PUBLICATION_DATE_RADIO_ITEM_ID) as StatefulRadio;
  }

  private function getOwnDateRadioBox():StatefulRadio {
    return queryById(OWN_DATE_RADIO_ITEM_ID) as StatefulRadio;
  }

  override public function focus(selectText:* = undefined, delay:* = undefined, callback:Function = null, scope:Function = null):Component {
    // PDE: if date gets focused in preview, we will have to focus the radiobox as well
    var radio = getPublicationDateRadioBox() && getPublicationDateRadioBox().getValue() ? getPublicationDateRadioBox() : getOwnDateRadioBox();
    if (radio) {
      radio.focus(selectText, delay);
    }
  }

  /** @private */
  [Bindable]
  public native function set highlighted(newHighlighted:Boolean):void;

  /** @inheritDoc */
  [Bindable]
  public native function get highlighted():Boolean;

}
}
