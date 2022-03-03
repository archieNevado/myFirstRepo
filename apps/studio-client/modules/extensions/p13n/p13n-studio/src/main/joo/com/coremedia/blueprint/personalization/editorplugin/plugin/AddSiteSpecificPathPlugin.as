package com.coremedia.blueprint.personalization.editorplugin.plugin {

import com.coremedia.cap.common.SESSION;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentPropertyNames;
import com.coremedia.cms.editor.sdk.util.PathFormatter;
import com.coremedia.personalization.ui.persona.selector.PersonaSelector;
import com.coremedia.ui.data.RemoteBean;
import com.coremedia.ui.data.ValueExpression;

import ext.Component;
import ext.plugin.AbstractPlugin;

import joo.debug;

/**
 * Plugin that adds a site specific path containing a placeholder to a {@link PersonaSelector}.
 */
public class AddSiteSpecificPathPlugin extends AbstractPlugin {
  private var groupHeaderLabel:String;
  private var path:String;
  private var personaSelector:PersonaSelector = null;
  private var entityExpression:ValueExpression = null;

  private static var sitePathFormatters:Array/*<Function>*/ = [formatSitePathFromContent];

  /**
   * @cfg {String} relativePath relative path within the users home folder that will be added to a 'PersonaSelector'
   * @cfg {String} groupHeaderLabel optional suffix that will be appended to the labels of the content objects
   *  retrieved from the path
   *
   * @param config the config object
   */
  public function AddSiteSpecificPathPlugin(config:Addsitespecificpath = null) {
    groupHeaderLabel = config.groupHeaderLabel;
    entityExpression = config.activeContentValueExpression;
    path = config.path;
    super(config);
  }

  public static function addSitePathFormatter(sitePathFormatter:Function):void {
    sitePathFormatters = sitePathFormatters.concat(sitePathFormatter);
  }

  override public function init(component: Component): void {
    if (!(component is PersonaSelector)) {
      throw Error("plugin is only applicable to components of type 'PersonaSelector'");
    }
    personaSelector = component as PersonaSelector;
    personaSelector.mon(personaSelector, 'afterrender', function (): void {
      personaSelector.contentValueExpression.addChangeListener(loadPersonasFromSitePath);
    });

    personaSelector.addListener('beforedestroy', function () {
      personaSelector.contentValueExpression && personaSelector.contentValueExpression.removeChangeListener(loadPersonasFromSitePath)
    })
  }

  private function loadPersonasFromSitePath(): void {
    for each (var sitePathFormatter: Function in sitePathFormatters) {
      sitePathFormatter.call(null, path, entityExpression, doLoadPersonas);
    }
  }

  private static function formatSitePathFromContent(path:String, entityExpression:ValueExpression, callback:Function):void {
    entityExpression.loadValue(function(entity:Object):void {
      if (entity is Content) {
        entityExpression.extendBy(ContentPropertyNames.PATH).loadValue(function():void {
          var selectedSitePath:String = PathFormatter.formatSitePath(path, Content(entity));
          callback.call(null, selectedSitePath, entity);
        });
      }
    });
  }

  private function doLoadPersonas(selectedSitePath:String, currentBean:RemoteBean):void {
    if (selectedSitePath) {
      SESSION.getConnection().getContentRepository().getRoot().getChild(selectedSitePath, function (content:Content, cPath:String):void {
        if (content && personaSelector) {
          if (currentBean && personaSelector.contentValueExpression && personaSelector.contentValueExpression.getValue() && currentBean.getUri() === personaSelector.contentValueExpression.getValue().getUri()) {
            personaSelector.clearSiteSpecificPaths();
            personaSelector.addSiteSpecificPath(selectedSitePath, groupHeaderLabel);
            if (debug) {
              trace("[INFO]", "added persona lookup path", selectedSitePath, "with label", groupHeaderLabel);
            }
          }
        } else if (debug) {
          trace("[INFO]", "no lookup path for persona added. no folder available at path", selectedSitePath);
        }
      });
    } else if (debug) {
      trace("[INFO]", "no lookup path for persona added. selected site path is null");
    }
  }
}
}
