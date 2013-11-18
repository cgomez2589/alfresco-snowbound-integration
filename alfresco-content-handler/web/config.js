vvConfig = {
    // Path to the AJAX servlet
    servletPath: "/VirtualViewerJavaHTML5/AjaxServlet",

    // Percentage to stop allowing users to zoom the image
    maxZoomPercent: 1000,
 
    // Wait X milliseconds before requesting the zoomed image.  This exists so
    // that if the user hits zoom several times quickly we spare the server load
    // by only requesting the final zoom level
    zoomTimeout: 500, 

    // Wait X milliseconds before requesting the adjusted images.  This
    // exists to throttle the number of image requests sent to the server.
    // If the user quickly slides the sliders back and forth this will wait
    // X seconds before requsting the updated image.
    pictureControlsTimeout: 200,

    // Wait X milliseconds before displaying the "Please wait while your
    // image is loaded." dialog
    waitDialogTimeout: 1000,

    // Set the size and color of the "handle" used to resize annotations as
    // well as indicate the "end zone" of the polygon tools.
    polygonNubSize: 10,
    polygonNubFillColor: "rgba(0,0,255,.40)",

    // Turn on scroll bars for the image display.  This disables the pan tool.
    imageScrollBars: true,

    // Whether or not to include annotations when sendDocument is called
    sendDocumentWithAnnotations: false,
    
    // maintain the same zoom, rotation, fit, flip, and other settings when
    // switching between pages
    retainViewOptionsBetweenPages: true,

    // The default zoom mode 
    defaultZoomMode: vvDefines.zoomModes.fitWindow,

    // Should the text inside of text annotations rotate along with the
    // document?
    rotateTextAnnotations: true,

    // Should we burn the annotations into the image when printing
    printBurnAnnotations: false,

    // Include the options to print only Text or Non-Text annotations
    printShowTypeToggles: false,

    // Should we burn the annotations into the image when exporting
    exportBurnAnnotations: true,

    // Create a new annotation layer for each annotation
    oneLayerPerAnnotation: false,

    // Reload the document model after a save.  Used for systems like
    // FileNet which generate a documentId on the server
    reloadDocumentOnSave: true,

    // If true, newly added text annotations will immediately enter 'edit'
    // mode with the contents highlighted.
    immediatelyEditTextAnnotations: true,

    // The following three help parameters are passed to window.open when
    // creating the help window.
    // window.open(helpURL,helpWindowName,helpWindowParams);

    // This can be (and often should be) a relative URL Path 
    helpURL: "http://virtualviewer.com/VirtualViewerJavaAJAXHelp/virtualviewer.htm",
    helpWindowName: "helpWindow",
    helpWindowParams: "scrollbars=1,width=800,height=600",

    // If the default zoom mode is 'fitLast', should the viewer respect that
    // when switching between documents
    fitLastBetweenDocuments: false,

    // Toggle the thumbnail panel
    showThumbnailPanel: true,

    // Enable/disable the pages thumbnail tab
    showPageThumbnails: true,

    // Enable/disable the documents thumbnail tab
    showDocThumbnails: false,

    // Enable/disable the search tab
    showSearch: true,

    // Multiple Document mode
    // Accepted values for multipleDocMode are: availableDocuments, viewedDocuments, specifiedDocuments
    multipleDocMode: vvDefines.multipleDocModes.availableDocuments,
	
    // Enable/Disable Page Manipulation Functionality
    pageManipulations: true,

    // Enable/Disable the "New Document" page manipulation menu 
    pageManipulationsNewDocumentMenu: true,

    // Enable/Disable Base64 encoding of annotations
    base64EncodeAnnotations: false,

    // Enable/Disable the Rubber Stamp Functionality
    enableRubberStamp: true,

    // Whether or not text searches should be case sensitive
    searchCaseSensitive: false,

    // create IDM annotations. Also requires server to be configured to save
    // IDM.
    createIDMAnnotations: false,

    // Configure the rubber stamps
    rubberStamp: [
        { textString: "Approved", 
          fontFace: "Times New Roman",
          fontSize: 30,
          fontBold: true, 
          fontItalic: true,
          fontUnderline: false,
          fontColor: "00FF00" }, 
        { textString: "Denied", 
          fontColor: "FF0000" }
    ],

    // Default appearance options for annotations
    annotationDefaults: { 
        lineColor: "FE0000",
        lineWidth: 3,

        fillColor: "FE0000",

        stickyFillColor: "FCEFA1",
        stickyMargin: 10, // also need to adjust .vvStickyNote in webviewer.css

        highlightFillColor: "FCEFA1",
        highlightOpacity: 0.4,

        textString: "Text",

        fontFace: "Arial",
        fontSize: 48,
        fontBold: false, 
        fontItalic: false, 
        fontStrike: false,    // for future use
        fontUnderline: false, // for future use
        fontColor: "000000" 
    }
};
