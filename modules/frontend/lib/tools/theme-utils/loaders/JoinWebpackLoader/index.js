/**
 * Based on "join-webpack-plugin" (MIT license) by Serguei Okladnikov <oklaspec@gmail.com>
 *
 * Customizations (in this file):
 * - brought to ES6
 * - fixed some inspection errors
 * - allow [N] in group and output names as described https://github.com/webpack/loader-utils#interpolatename
 * -> for this added new "regExp" configuration
 */

const JoinPlugin = require('../../plugins/JoinWebpackPlugin');

const { interpolateName, parseQuery } = require('loader-utils');

function namePreTmpl(context, name, regExp) {
  let preTmpl = name;
  const hashRegex = /\[[^\[]*hash[^\]]*]/g;
  let hashMatch = name.match(hashRegex);
  if (hashMatch) {
    if (hashMatch.length > 1) {
      throw Error('only one hash supported');
    }
    hashMatch = hashMatch[0];
    preTmpl = name.replace(hashRegex, 'THE::HASH');
  }
  preTmpl = interpolateName(context, preTmpl, { content: '', regExp: regExp });
  if (hashMatch) {
    preTmpl = preTmpl.replace(/THE::HASH/g, hashMatch);
  }
  return preTmpl;
}

module.exports = function(source) {
  const query = parseQuery(this.query);

  const plugins = this.options.plugins.filter(
    plugin => plugin instanceof JoinPlugin
  );
  let plugin = plugins.filter(plugin => plugin.id === query.id);
  plugin =
    plugin.length > 0
      ? plugin[0]
      : plugins.length > 0 && query.id === null ? plugins[0] : null;

  if (!plugin) {
    throw new Error('associated webpack plugin not found');
  }

  let name = query.name || plugin.options.name;
  let groupName = query.group || plugin.options.group;
  const regExp = query.regExp || plugin.options.regExp;

  if (groupName !== null) {
    groupName = interpolateName(this, groupName, {
      content: '',
      regExp: regExp,
    });
  }

  plugin.group(groupName).filetmpl = namePreTmpl(this, name, regExp);

  name = plugin.addSource(groupName, source, this.resourcePath, this._module);

  return (
    'module.exports = __webpack_public_path__ + ' + JSON.stringify(name) + ';'
  );
};

module.exports.raw = true;
