package com.coremedia.blueprint.studio.externallibrary {
import com.coremedia.ui.data.ValueExpression;

import ext.Action;
import ext.Component;
import ext.Ext;

/**
 * Moves forward or backwards in the action history.
 */
public class HistoryActionBase extends Action {

  private var historyValueExpression:ValueExpression;
  private var direction:String;
  private var cmd:Command;
  internal native function get items():Array;

  public function HistoryActionBase(config:HistoryAction = null) {
    super(config);
    direction = config.direction;
    historyValueExpression = config.historyValueExpression;
    historyValueExpression.addChangeListener(updateDisabled);

    if (!config['handler']) {
      setHandler(updateHistory, this);
    }
  }


  override public function addComponent(comp:Component):void {
    super.addComponent(comp);
    this.updateDisabled();
  }

  private function updateDisabled():void {
    this.cmd = null;
    var activeIndex:int = historyValueExpression.getValue();
    var filterToolbar:FilterToolbar = Ext.getCmp('externalLibraryFilterToolbar') as FilterToolbar;
    if (!filterToolbar.getCommandStack()) {
      //nothing, net rendered yet
    }
    else if (direction === 'forward' && filterToolbar) {
      cmd = filterToolbar.getCommandStack().getCommand((activeIndex + 1));
    }
    else if (direction === 'backward' && filterToolbar) {
      cmd = filterToolbar.getCommandStack().getCommand((activeIndex - 2));
    }

    setDisabled(!cmd);
  }

  private function updateHistory():void {
    //cmd.execute();
  }

  override public function removeComponent(comp:Component):void {
    super.removeComponent(comp);
    if (items && items.length === 0) {
      historyValueExpression && historyValueExpression.removeChangeListener(updateDisabled);
    }
  }
}
}