self.importScripts(
  "./resources/com.coremedia.cms__studio-client.rxjs/rxjs/6.5.4/rxjs.umd.min.js",
  "./resources/com.coremedia.ui__service-agent/@coremedia/studio-apps-service-agent/0.0.33/cmApps.js"
);

class ClipboardServiceImpl {

  constructor() {
    this.items = [];
    this.subject = new rxjs.Subject();
  }

  getName() {
    return "clipboardService";
  }

  setItems(items) {
    this.items = items;
    this.subject.next(items);
    return Promise.resolve();
  }

  getItems() {
    return Promise.resolve(this.items);
  }

  observe_items() {
    var that = this;
    return new rxjs.Observable(function (subscriber) {
      var subscription = that.subject.subscribe(function (items) {
        subscriber.next(items);
      });

      return function () {
        subscription.unsubscribe();
      }
    });
  }
}

self.addEventListener("activate", function(event) {
  cmApps.serviceAgent.registerService(new ClipboardServiceImpl(), { name: "clipboardService" });
});
