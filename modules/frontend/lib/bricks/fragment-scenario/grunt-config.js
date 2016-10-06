/* brick 'fragment-scenario' 
* containing templates which are required by the IBM fragment scenario
* */
module.exports = function (grunt, options) {
  'use strict';

  // add templates to theme templateset
  var existingTemplates = grunt.config.get('compress.templates.files');
  existingTemplates.push({
    expand: true,
    cwd: options.brickDirectory + '/templates',
    src: '**',
    dest: 'META-INF/resources/WEB-INF/templates/bricks/'
  });

  return {
    tasks: {
      compress: {
        templates: {
          files: existingTemplates
        }
      },
      copy: {
        brick_fragment_scenario: {
          files: [
            // copy templates
            {
              expand: true,
              cwd: options.brickDirectory + '/templates',
              src: '**',
              dest: options.brickTemplatesDest
            }
          ]
        }
      },
      watch: {
        brick_fragment_scenario: {
          files: [options.brickDirectory + "**"],
          tasks: ['copy:brick_fragment_scenario']
        }
      }
    }
  };
};
