/* brick 'fragment-scenario' */
module.exports = function (grunt, options) {
  'use strict';

  // add templates to theme templateset
  var existingTemplates = grunt.config.get('compress.brick_templates.files');
  existingTemplates.push({
    expand: true,
    cwd: options.brickDirectory + '/templates',
    src: '**',
    dest: 'META-INF/resources/WEB-INF/templates/bricks/'
  });

  return {
    tasks: {
      compress: {
        brick_templates: {
          files: existingTemplates
        }
      },
      copy: {
        // copy templates
        brick_fragmentScenario: {
          files: [{
            expand: true,
            cwd: options.brickDirectory + '/templates',
            src: '**',
            dest: options.brickTemplatesDest
          }, {
            expand: true,
            isFile: true,
            cwd: options.brickDirectory,
            src: ['css/**', 'fonts/**', 'img/**', 'js/**', 'vendor/**', '*.properties'],
            dest: '../../target/resources/themes/<%= themeConfig.name %>'
          }]
        }
      },
      watch: {
        brick_fragmentScenario: {
          files: [options.brickDirectory + "**"],
          tasks: ['copy:brick_fragmentScenario']
        }
      }
    }
  };
};
