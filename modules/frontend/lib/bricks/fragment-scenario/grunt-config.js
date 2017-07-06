/* brick 'fragment-scenario' */
module.exports = function (grunt, options) {
  'use strict';

  // add templates to theme templateset
  var existingTemplates = options.brickCompressFiles;
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
        brick_fragment_scenario_js: {
          expand: true,
          cwd: options.brickDirectory,
          src: 'js/*.js',
          dest: '../../target/resources/themes/<%= themeConfig.name %>/'
        },
        brick_fragment_scenario_templates: {
          expand: true,
          cwd: options.brickDirectory + '/templates',
          src: '**',
          dest: options.brickTemplatesDest
        }
      },
      watch: {
        brick_fragment_scenario_js: {
          files: options.brickDirectory + "/js/**",
          tasks: ['copy:brick_fragment_scenario_js']
        },
        brick_fragment_scenario_templates: {
          files: options.brickDirectory + "/templates/**",
          tasks: ['copy:brick_fragment_scenario_templates', 'compress:brick_templates']
        },
        brick_fragment_scenario_sass: {
          options: {
            spawn: true
          },
          files: options.brickDirectory + '/sass/**/*.scss',
          tasks: ['sass']
        }
      }
    }
  };
};
