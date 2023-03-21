# Resources for the blueprint-studio-theme

This folder contains all kinds of static web-resources like images, fonts, CSS and JavaScript.

In order to actually include CSS and JavaScript files into the application you need to add them to the sencha section of the `jangaroo.config.js`, e.g.

```
module.exports = {
  ...
  sencha: {
    ...
    css: [
      {
        path: "resources/path/to/myStylesheet1.css",
      },
      {
        path: "resources/path/to/myStylesheet2.css",
      },
    ],
    js: [
      {
        path: "resources/path/to/myJavascript1.js",
      },
      {
        path: "resources/path/to/myJavascript2.js",
      },
    ],
  },
};
```
