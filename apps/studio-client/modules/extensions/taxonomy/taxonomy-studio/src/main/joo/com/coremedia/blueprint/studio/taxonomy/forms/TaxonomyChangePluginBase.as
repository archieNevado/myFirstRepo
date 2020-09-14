package com.coremedia.blueprint.studio.taxonomy.forms {
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyUtil;
import com.coremedia.cap.common.SESSION;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.struct.Struct;
import com.coremedia.cms.editor.sdk.premular.DocumentTabPanel;
import com.coremedia.cms.editor.sdk.premular.Premular;
import com.coremedia.ui.data.PropertyChangeEvent;
import com.coremedia.ui.data.ValueExpression;
import com.coremedia.ui.data.ValueExpressionFactory;

import ext.Component;
import ext.Plugin;

public class TaxonomyChangePluginBase implements Plugin {

  [Bindable]
  public var properties:String;

  private var content:Content;
  private var updateQueue:Number = 0;

  private var propertyList:Array;
  private var listeners:Array = [];

  public function TaxonomyChangePluginBase(config:TaxonomyChangePlugin = null) {
    if (config.properties) {
      propertyList = config.properties.split(",");
    }
  }

  public function init(component:Component):void {
    var form:DocumentTabPanel = component as DocumentTabPanel;

    //do not apply this plugin when opened as regular premular
    var parent:Component = form.findParentByType(Premular.xtype);
    if(parent) {
      return;
    }

    form.bindTo.addChangeListener(contentChanged);
    this.content = form.bindTo.getValue();

    this.content.load(addListeners);
  }

  private function addListeners():void {
    if (propertyList) {
      for each(var p:String in propertyList) {
        if(p.indexOf(".") !== -1) {
          /*
           * Returns a ValueExpression that keeps a String representation
           * of the annotated link list. This is just a convenient way to
           * allow the ValueExpression to detect any changes in the link list.
           */
          var structListAsStringVE:ValueExpression = ValueExpressionFactory.createFromFunction(function ():String {
            var structValue:* = ValueExpressionFactory.create('properties.' + p, content).getValue();
            if (!structValue) {
              return undefined;
            }

            var listAsString:String = "";

            if(structValue is Array) {
              structValue.forEach(function (listEntry:Struct):String {
                listAsString = listAsString + listEntry.toJson();
              });
            }
            else {
              listAsString = "" + structValue;
            }

            return listAsString;
          });

          structListAsStringVE.addChangeListener(propertiesChanged);
          listeners.push(structListAsStringVE);
        }
        else {
          var ve:ValueExpression = ValueExpressionFactory.create('properties.' + p, content);
          ve.addChangeListener(propertiesChanged);
          listeners.push(ve);
        }
      }
    }
    else {
      this.content.addValueChangeListener(lazyChange);
    }
  }

  private function removeListeners():void {
    if (propertyList) {
      for each(var ve:ValueExpression in listeners) {
        ve.removeChangeListener(propertiesChanged);
      }
    }
    else {
      content && content.removeValueChangeListener(lazyChange);
    }
  }

  private function contentChanged(ve:ValueExpression):void {
    this.content && this.removeListeners();
    this.content = ve.getValue();
    this.content && this.content.load(addListeners);
  }

  private function propertiesChanged(ve:ValueExpression):void {
    checkUpdate();
  }

  private function lazyChange(event:PropertyChangeEvent = null):void {
    //apply only property change events
    if (event && event.property.indexOf("properties\n") !== 0) {
      return;
    }

    checkUpdate();
  }

  private function checkUpdate():void {
    if (updateQueue === 0) {
      updateQueue++;
      window.setTimeout(commitNode, 1000);
    }
    else {
      updateQueue++;
    }
  }

  private function commitNode():void {
    if (updateQueue === 1) {
      var node:TaxonomyNode = TaxonomyUtil.getLatestSelection();
      var content:Content = SESSION.getConnection().getContentRepository().getContent(node.getRef());
      content.invalidate(function ():void {
        node.commitNode(function ():void {
          content.invalidate(function ():void {
            finishedUpdate();
          });
        });
      });
    }
    else {
      finishedUpdate();
    }
  }

  private function finishedUpdate():void {
    updateQueue--;

    if (updateQueue > 0) {
      updateQueue = 0;
      lazyChange();
    }
  }


  private function onTabDestroy():void {
    content && content.removeValueChangeListener(lazyChange);
  }
}
}
