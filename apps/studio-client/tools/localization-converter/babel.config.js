module.exports = {
  plugins: [
    ["@babel/plugin-transform-typescript", { "allowDeclareFields": true }],
  ],
  presets: [
    ["@babel/preset-env", {
      "targets": {
        "node": true
      }
    }]
  ],
};
