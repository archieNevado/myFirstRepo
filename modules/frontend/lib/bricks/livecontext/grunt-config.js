/* brick 'livecontext' 
* containing templates which are required by the livecontext scenario
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
        brick_livecontext: {
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
        brick_livecontext: {
          files: [options.brickDirectory + "**"],
          tasks: ['copy:brick_livecontext']
        }
      }
    }
  };
};
