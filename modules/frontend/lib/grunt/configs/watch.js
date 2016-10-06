'use strict';

/* watch task for development */
module.exports = {
  options: {
    livereload: true // default port: 35729
  },
  sass: {
    options: {
      livereload: false
    },
    files: 'src/sass/**/*.scss',
    tasks: ['sass', 'postcss']
  },
  js: {
    files: 'src/js/*.js',
    tasks: ['copy:main']
  },
  ftl: {
    files: 'src/**/*.ftl',
    tasks: ['copy:templates']
  },
  bundles: {
    files: 'src/*.properties',
    tasks: ['copy:main']
  },
  css: {
    files: '../../target/resources/themes/<%= themeConfig.name %>/css/*.css',
    tasks: []
  }
};
