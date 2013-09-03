module.exports = function(grunt) {

  // Project configuration.
  grunt.initConfig({
    lint: {
      all: ['grunt.js', 
            'config.java.js',
            'config.net.js',
            'js/annotations.js',
            'js/vvDefines.js',
            'js/print.js',
            'js/vvAnnotationPainter.js',
            'js/vvBoundingBox.js',
            'js/vvDocViewState.js',
            'js/vvDocumentModel.js',
            'js/vvPoint.js',
            'js/vvUtility.js',
            'js/webviewer.js']
    },
    concat: {
        dist: {
            src: [
            'js/annotations.js',
            'js/3rdparty/browserdetect.js', 
            'js/3rdparty/jquery-1.7.1.min.js',
            'js/3rdparty/jquery-ui-1.8.18.custom.min.js',
            'js/3rdparty/jquery.contextmenu.r2.js',
            'js/3rdparty/jquery.localize.js',
            'js/3rdparty/json2.js',
            'js/vvAnnotationPainter.js',
            'js/vvBoundingBox.js',
            'js/vvDocViewState.js',
            'js/vvDocumentModel.js',
            'js/vvPoint.js',
            'js/vvUtility.js',
            'js/webviewer.js'],
            dest: 'dist/built.js'
        }
    },
    min: {
        dist: {
            src: ['dist/built.js'],
            dest: 'dist/built.min.js'
        }
    },
    jshint: {
      options: {
        forin: true,
        noarg: true,
        eqeqeq: true,
        loopfunc: true,
        bitwise: true,
        curly: true,
        browser: true,
        jquery: true,
        indent: 4
      }
    }
  });

  // Default task.
  grunt.registerTask('default', 'lint');
};
