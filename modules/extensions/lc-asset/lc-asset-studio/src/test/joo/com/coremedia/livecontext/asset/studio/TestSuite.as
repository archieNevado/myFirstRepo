package com.coremedia.livecontext.asset.studio {

import flexunit.framework.TestSuite;

//noinspection JSUnusedGlobalSymbols
public class TestSuite {
  //noinspection JSUnusedGlobalSymbols
  public static function suite():flexunit.framework.TestSuite {
    var suite:flexunit.framework.TestSuite = new flexunit.framework.TestSuite();

    suite.addTestSuite(InheritReferencesTest);
    suite.addTestSuite(SearchProductImagesTest);
    //execute livecontextAssetPlugin on current view port
    new LivecontextAssetStudioPlugin().init({});

    return suite;
  }
}
}
