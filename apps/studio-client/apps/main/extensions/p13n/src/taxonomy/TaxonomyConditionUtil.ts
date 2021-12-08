import IdHelper from "@coremedia/studio-client.cap-rest-client/common/IdHelper";
import Content from "@coremedia/studio-client.cap-rest-client/content/Content";
import beanFactory from "@coremedia/studio-client.client-core/data/beanFactory";
import IdUtil from "@coremedia/studio-client.client-core/util/IdUtil";
import RuleXMLCoDec from "@coremedia/studio-client.main.cap-personalization-ui/util/RuleXMLCoDec";
import { as } from "@jangaroo/runtime";
import joo from "@jangaroo/runtime/joo";
import trace from "@jangaroo/runtime/trace";

class TaxonomyConditionUtil {

  static getTaxonomyId4Chooser(propertyPrefix: string): string {
    if (propertyPrefix.indexOf("subject") !== -1 || propertyPrefix.indexOf("explicit") !== -1) {
      return "Subject";
    } else if (propertyPrefix.indexOf("location") !== -1) {
      return "Location";
    } else if (propertyPrefix.indexOf("queryLoc") !== -1) {
      return "QueryLocation";
    } else if (propertyPrefix.indexOf("queryTax") !== -1) {
      return "Query";
    }
    return propertyPrefix;
  }

  static formatPropertyValue4Store(value: string): string {
    const propertyValue = parseFloat(value);
    return "" + propertyValue / 100;
  }

  static formatPropertyValue4Textfield(value: string): string {
    const propertyValue = parseFloat(value) * 100;
    return "" + propertyValue;
  }

  static formatPropertyName(prefix: string, taxonomy: Content): string {
    let id = IdUtil.MISSING_CONTENT_ID;
    if (taxonomy) {
      id = IdHelper.parseContentId(taxonomy);
    }
    return prefix + RuleXMLCoDec.INTERNAL_CONTENT_ID_PREFIX + id;
  }

  static getTaxonomyContent(property: string): Content {
    const split = property.split(RuleXMLCoDec.INTERNAL_CONTENT_ID_PREFIX);
    if (split && split.length > 0) {
      const contentId: string = split[1];
      if (contentId.length > 0) {
        return as(beanFactory._.getRemoteBean("content/" + contentId), Content);
      }
    }
    if (joo.debug) {
      trace("unable to retrieve content for taxonomy", property);
    }
    return null;
  }
}

export default TaxonomyConditionUtil;
