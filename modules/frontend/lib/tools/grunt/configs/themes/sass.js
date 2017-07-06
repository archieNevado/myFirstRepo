'use strict';

/* default options for sass. adding sourcemaps to themes */
module.exports = {
  options: {
    outputStyle: 'expanded',
    sourceMap: true,
    sourceMapEmbed: true,
    sourceMapRoot: 'file://' + process.cwd() + '/../../target/resources/themes/<%= themeConfig.name %>/css'
  },
  compile: {
    files: {
      '../../target/resources/themes/<%= themeConfig.name %>/css/<%= themeConfig.name %>.css': 'src/sass/<%= themeConfig.name %>.scss',
      '../../target/resources/themes/<%= themeConfig.name %>/css/preview.css': 'src/sass/preview.scss'
    }
  }
};
