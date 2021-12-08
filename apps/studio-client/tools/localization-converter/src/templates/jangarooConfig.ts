export default (autoLoad:boolean = false) => `const { jangarooConfig } = require("@jangaroo/core");

module.exports = jangarooConfig({
  type: "code",
  ${autoLoad ? `autoLoad: [
    "./src/init",
  ],` : ``}
});
`;
