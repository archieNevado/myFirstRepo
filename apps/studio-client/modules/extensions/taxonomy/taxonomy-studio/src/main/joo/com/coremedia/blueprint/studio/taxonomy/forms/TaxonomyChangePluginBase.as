package com.coremedia.blueprint.studio.taxonomy.forms {
import com.coremedia.blueprint.studio.taxonomy.TaxonomyNode;
import com.coremedia.blueprint.studio.taxonomy.TaxonomyUtil;
import com.coremedia.cap.common.SESSION;
import com.coremedia.cap.content.Content;
import com.coremedia.cms.editor.sdk.premular.DocumentTabPanel;
import com.coremedia.ui.data.PropertyChangeEvent;
import com.coremedia.ui.data.ValueExpression;

import ext.Component;
import ext.Plugin;

public class TaxonomyChangePluginBase implements Plugin {

  private var form:DocumentTabPanel;
  private var content:Content;
  private var updateQueue:Number = 0;

  public function TaxonomyChangePluginBase(config:TaxonomyChangePlugin = null) {
  }

  public function init(component:Component):void {
    this.form = component as DocumentTabPanel;
    this.form.bindTo.addChangeListener(contentChanged);
  }

  private function contentChanged(ve:ValueExpression):void {
    content && content.removeValueChangeListener(lazyChange);

    this.content = ve.getValue();
    this.content.addValueChangeListener(lazyChange);
  }

  private function lazyChange(event:PropertyChangeEvent = null):void {
    //apply only property change events
    if (event && event.property.indexOf("properties\n") !== 0) {
      return;
    }

    if (updateQueue === 0) {
      updateQueue++;
      window.setTimeout(commitNode, 1000);
    }
    else {
      updateQueue++;
    }
  }

  private function commitNode():void {
    if(updateQueue === 1) {
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

    if(updateQueue > 0) {
      updateQueue = 0;
      lazyChange();
    }
  }


  private function onTabDestroy():void {
    content && content.removeValueChangeListener(lazyChange);
  }
}
}
