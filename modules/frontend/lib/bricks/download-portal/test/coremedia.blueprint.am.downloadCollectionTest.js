var CBADC = coremedia.blueprint.am.downloadCollection;

describe("Test initialization of Download Collection", function () {

  afterEach(function () {
    localStorage.removeItem(CBADC.DOWNLOAD_COLLECTION_PROPERTY);
  });

  it("There should be an empty download collection", function () {
    expect(CBADC.getDownloadCollection()).toBeNull();

    CBADC.initDownloadCollection();

    expect(CBADC.getDownloadCollection()).toBeDefined();
  });
});

describe("Test count of Download Collection", function () {

  beforeEach(function () {
    CBADC.initDownloadCollection();
  });

  afterEach(function () {
    localStorage.removeItem(CBADC.DOWNLOAD_COLLECTION_PROPERTY);
  });

  it("The download collection size should be 0", function () {
    expect(CBADC.getDownloadCollectionCount()).toEqual(0);
  });

  it("The download collection size should be 3", function () {
    var collection = localStorage.getItem(CBADC.DOWNLOAD_COLLECTION_PROPERTY);
    collection = {1: ["web", "original"], 2: ["web"]};
    localStorage.setItem(CBADC.DOWNLOAD_COLLECTION_PROPERTY, JSON.stringify(collection));
    expect(CBADC.getDownloadCollectionCount()).toEqual(3);
  });
});

describe("Test rendition name is in download collection", function () {

  beforeEach(function () {
    CBADC.initDownloadCollection();
    var collection = localStorage.getItem(CBADC.DOWNLOAD_COLLECTION_PROPERTY);
    collection = {1: ["web", "original"], 2: ["web"]};
    localStorage.setItem(CBADC.DOWNLOAD_COLLECTION_PROPERTY, JSON.stringify(collection));
  });

  afterEach(function () {
    localStorage.removeItem(CBADC.DOWNLOAD_COLLECTION_PROPERTY);
  });

  it("The download collection (does not) contains rendition", function () {
    expect(CBADC.isInDownloadCollection(2, "web")).toBeTruthy();
    expect(CBADC.isInDownloadCollection(2, "original")).toBeFalsy();
  });
});

describe("Test get/save/add/remove from/to download collection", function () {

  beforeEach(function () {
    CBADC.initDownloadCollection();
    var collection = localStorage.getItem(CBADC.DOWNLOAD_COLLECTION_PROPERTY);
    collection = {1: ["web", "original"], 2: ["web"]};
    localStorage.setItem(CBADC.DOWNLOAD_COLLECTION_PROPERTY, JSON.stringify(collection));
  });

  afterEach(function () {
    localStorage.removeItem(CBADC.DOWNLOAD_COLLECTION_PROPERTY);
  });

  it("The download collection returns its contents", function () {
    expect(CBADC.getDownloadCollection()).toEqual({1: ['web', 'original'], 2: ['web']});
  });

  it("After clear the download collection should be an empty object", function () {
    CBADC.clearDownloadCollection();
    expect(CBADC.getDownloadCollection()).toEqual({});
  });

  it("Save should result in an updated download collection ", function () {
    CBADC.saveDownloadCollection({3: ["web"], 4: ["web", "original"]});
    expect(CBADC.getDownloadCollection()).toEqual({3: ["web"], 4: ["web", "original"]});
  });
});

describe("Test initialization of default rendition selection", function () {

  beforeEach(function () {
    CBADC.clearDefaultRenditionSelection();
  });

  afterEach(function () {
    localStorage.removeItem(CBADC.RENDITION_SELECTION_PROPERTY);
  });

  it("There should be an empty default rendition selection", function () {
    expect(CBADC.getRenditionSelection()).toEqual([]);
  });

  it("Update default rendition selection", function () {
    CBADC.addDefaultRenditionSelection("web");

    expect(CBADC.getRenditionSelection()).toEqual(["web"]);

    CBADC.addDefaultRenditionSelection("original");

    expect(CBADC.getRenditionSelection()).toEqual(["web", "original"]);

    CBADC.addDefaultRenditionSelection("web");

    expect(CBADC.getRenditionSelection()).toEqual(["web", "original"]);
  });

  it("Clear default rendition selection", function () {
    CBADC.addDefaultRenditionSelection("web");

    expect(CBADC.getRenditionSelection()).toEqual(["web"]);

    CBADC.clearDefaultRenditionSelection();

    expect(CBADC.getRenditionSelection()).toEqual([]);
  });
});

describe("Add and remove renditions to/from download collection", function () {

  beforeEach(function () {
    CBADC.initDownloadCollection();
  });

  afterEach(function () {
    localStorage.removeItem(CBADC.DOWNLOAD_COLLECTION_PROPERTY);
  });

  it("Add/remove rendition to download collection", function () {
    CBADC.addRenditionToDownloadCollection(1, "web");
    expect(CBADC.isInDownloadCollection(1, "web")).toBeTruthy();
    expect(CBADC.isInDownloadCollection(2, "original")).toBeFalsy();

    CBADC.removeRenditionFromDownloadCollection(1, "web");
    expect(CBADC.isInDownloadCollection(1, "web")).toBeFalsy();
    expect(CBADC.isInDownloadCollection(2, "original")).toBeFalsy();
  });
});