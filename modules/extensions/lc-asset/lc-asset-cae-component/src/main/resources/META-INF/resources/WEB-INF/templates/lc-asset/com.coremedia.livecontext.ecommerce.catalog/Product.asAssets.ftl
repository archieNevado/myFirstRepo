<#-- @ftlvariable name="self" type="com.coremedia.livecontext.ecommerce.catalog.Product" -->

<@cm.include self=self view="asDynaAssets" />
<script>
  dojo.addOnLoad(function() {
    if(typeof productDisplayJS === 'undefined') {
      return;
    }
    var changeImages = function(catEntryId, productId){
      //reload the fragment with selected product variants.
      //to this end we send the catEntryId, productId and the selected attributes name/value pairs
      //as ';'-separated string as "attributes" to the reloader
      var entitledItemId = "entitledItem_" + productId;
      var attributes = "";
      var selectedAttributes = productDisplayJS.selectedAttributesList[entitledItemId];
      for (var attribute in selectedAttributes) {
        attributes += attribute + ";" + selectedAttributes[attribute] + ";";
      }
      console.trace("selected attributes: " + attributes);
      coremedia.blueprint.$(".cm-product-assets").each(function () {
        coremedia.blueprint.basic.refreshFragment(coremedia.blueprint.$(this), undefined,
                {
                  productId: productId,
                  catEntryId: catEntryId,
                  attributes: attributes
                });
      });
    };
    dojo.topic.subscribe('DefiningAttributes_Resolved', changeImages);
    dojo.topic.subscribe('DefiningAttributes_Changed', changeImages);
  });
</script>