// JavaScript in node_modules is not transpiled with babel by default, change this for coremedia npm modules
module.exports = {
  "transformIgnorePatterns": [
    "/node_modules/(?!@coremedia).+\\.js$"
  ]
};
