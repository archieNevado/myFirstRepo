const resolveFrom = require("resolve-from");
const deepMerge = require("./deepMerge");

// provide some modules as global variable as long as the ui tests and the "cm-wcs" workspace still rely on it
// the mapping is: moduleName => globalVariableName
const modulesToExpose = {
  "jquery": "coremedia.blueprint.$",
  "@coremedia/js-basic": "coremedia.blueprint.basic"
};

const rules = [];

for (let module of Object.keys(modulesToExpose)) {
  const globalVariableName = modulesToExpose[module];
  try {
    const path = resolveFrom(process.cwd(), module);
    rules.push({
      test: path,
      loader: `expose-loader?${globalVariableName}`
    });
  } catch (e) {
    // module not found, nothing to expose
  }
}

module.exports = () => config => deepMerge(config,
        {
          module: {
            rules: rules
          }
        }
);
