jasmine.getFixtures().fixturesPath = 'lib/js/legacy/test';

/**
 * renderFragmentHrefs Test
 */
describe("fragment hrefs", function () {

  var $ = coremedia.blueprint.$;

  beforeEach(function () {
    loadFixtures("fragment hrefs.html");
    coremedia.blueprint.basic.renderFragmentHrefs($(document.body));
  });

  it("should have replaced all occurences of $nextUrl$", function () {
    $("a[data-href]").each(function () {
      var $this = $(this);
      expect($this.attr("href").search(/\$nextUrl\$/g)).toBe(-1);
    });
  });
});
