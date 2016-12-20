package com.coremedia.ecommerce.studio.components.link {
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.util.PropertyEditorUtil;
import com.coremedia.ecommerce.studio.dragdrop.CatalogLinkDropZone;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;
import com.coremedia.ui.data.util.PropertyChangeEventUtil;

/**
 * The application logic for the catalog link displayed in the catalog link property field
 */
public class CatalogLinkFieldBase extends CatalogLink {

  private static const PROPERTIES:String = 'properties';
  private static const LOCAL_SETTINGS_STRUCT_NAME:String = 'localSettings';
  private static const COMMERCE_STRUCT_NAME:String = 'commerce';
  private static const INHERIT_PROPERTY_NAME:String = 'inherit';

  private var content:Content;
  private var openLinkSources:Function;
  private var propertyExpression:ValueExpression;
  private var readOnlyExpression:ValueExpression;
  private var hideOnEmpty:Boolean;
  private var multiple:Boolean;
  private var duplicate:Boolean;

  /**
   * @param config the config object
   */
  public function CatalogLinkFieldBase(config:CatalogLinkField = null) {
    openLinkSources = config.openLinkSources;
    hideOnEmpty = config.hideOnEmpty || false;
    multiple = config.multiple || false;
    duplicate = config.duplicate || false;
    super(config);
    if (bindTo) {
      bindTo.addChangeListener(updateContent);
      updateContent();
    }
  }

  private function updateContent():void {
    setContent(bindTo.getValue());
  }

  override protected function onDestroy():void {
    if (bindTo) {
      bindTo.removeChangeListener(updateContent);
    }
    super.onDestroy();
  }

  override protected function afterRender():void {
    super.afterRender();

    if(openLinkSources) {
      getView().getEl().setStyle("cursor", "pointer");
      mon(getEl(), 'click', function ():void {
        if (getStore().data.length == 0) {
          openLinkSources();
        }
      });
    }

    var config:CatalogLinkField = CatalogLinkField(initialConfig);
    var propertyExpression:ValueExpression = getPropertyExpression(config);
    new CatalogLinkDropZone(this, this, bindTo, propertyExpression, config.catalogObjectType ? [config.catalogObjectType] : config.catalogObjectTypes,
            getReadOnlyExpression(config), multiple, false, config.createStructFunction);

    if(hideOnEmpty) {
      propertyExpression.addChangeListener(propertyChanged);
      propertyChanged(propertyExpression);
    }
  }

  private function propertyChanged(ve:ValueExpression):void {
    var value:String = ve.getValue();
    var visible:Boolean = value && value.length > 0;
    setVisible(visible);
  }

  [ProvideToExtChildren]
  public function getContent():Content {
    return content;
  }

  public function setContent(value:Content):void {
    var oldValue:Content = content;
    content = value;
    PropertyChangeEventUtil.fireEvent(this, CatalogLink.CONTENT_VARIABLE_NAME, oldValue, value);
  }


  internal function getReadOnlyExpression(config:CatalogLink):ValueExpression {
    if (!readOnlyExpression) {
      readOnlyExpression = ValueExpressionFactory.createFromFunction(getReadOnlyFunction(CatalogLinkField(config)));
    }
    return readOnlyExpression;
  }

  public static function getReadOnlyFunction(config:CatalogLinkField):Function {
    return function():Boolean {
      //is the content or read-only or are we forced to set read-only?
      var contentOrForceReadOnlyExpression:ValueExpression = PropertyEditorUtil.createReadOnlyValueExpression(config.bindTo, config.forceReadOnlyValueExpression);
      if (contentOrForceReadOnlyExpression.getValue()) {
        return true;
      }
      if (config.bindTo) {
        var inheritExpression:ValueExpression = config.bindTo.extendBy(PROPERTIES, LOCAL_SETTINGS_STRUCT_NAME, COMMERCE_STRUCT_NAME, INHERIT_PROPERTY_NAME);
        return !!inheritExpression.getValue();
      }
      return false;
    }
  }

  override protected function getPropertyExpression(config:CatalogLink):ValueExpression {
    if (!propertyExpression) {
      var catalogLinkFieldConfig:CatalogLinkField = config as CatalogLinkField;
      if (catalogLinkFieldConfig && catalogLinkFieldConfig.model) {
        propertyExpression = ValueExpressionFactory.create(config.propertyName, catalogLinkFieldConfig.model);
      } else {
        propertyExpression = super.getPropertyExpression(config);
      }
    }
    return propertyExpression;
  }
}
}
