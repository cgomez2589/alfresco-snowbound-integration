vvDefines = {
    productName: "VirtualViewer HTML5",
    productVersion: "3.2.4",
    tilePixelSize: 250,
    tileScaleFactor: 1,
    tilesEnabled: false,
    tileZoomThreshold: 200,
    cacheBuster: false,
    annColorBlobSize: 15,
    annColorRowSize: 8,
    maxNumberOfTabs: 10,
    autoSaveAnnotations: false,
    mostRecentlyViewedListLength: 10,
    autoLayerPrefix: "AutoLayer",
    thumbnailSize: 140,
    ie9DrawDelay: 100,
    minTextAnnDrawSize: 10,
    idmMax: 10000,
    idmDPI: 200,
    daejaDPI: 300,
    searchBatchSize: 25,
    searchTimeout: 4000,
    searchDefaultColor: "rgba(255,78,0,0.2)",
    searchSelectedColor: "rgba(255,255,0,0.2)",
    annotationTypes: {
        SANN_FILLED_RECT: "FilledRectangle",
        SANN_HIGHLIGHT_RECT: "HighlightRectangle",
        SANN_RECTANGLE: "Rectangle",
        SANN_LINE: "Line",
        SANN_ELLIPSE: "Ellipse",
        SANN_FILLED_ELLIPSE: "FilledEllipse",
        SANN_FREEHAND: "Freehand",
        SANN_BITMAP: "Bitmap",
        SANN_POSTIT: "Sticky Note",
        SANN_POLYGON: "Polygon",
        SANN_FILLED_POLYGON: "FilledPolygon",
        SANN_ARROW: "Arrow",
        SANN_EDIT: "Rubber Stamp",
        SANN_TRANSPARENT_BITMAP: "TransparentBitmap",
        SANN_BUBBLE: "Bubble",
        SANN_CLOUD_EDIT: "Cloud",
        SANN_CUSTOM_STAMP: "CustomStamp",
        SANN_CIRCLE: "Circle"
    },
    permissionLevels: {
        PERM_HIDDEN: 0,
        PERM_REDACTION: 10,
        PERM_PRINT_WATERMARK: 20,
        PERM_VIEW_WATERMARK: 30,
        PERM_VIEW: 40,
        PERM_PRINT: 50,
        PERM_CREATE: 60,
        PERM_EDIT: 70,
        PERM_DELETE: 80
    },
    dragModes: {
        pan: 0,
        zoom: 1,
        annotate: 2,
        moveAnnotation: 3,
        textAnnotationEdit: 4
    },
    zoomModes: {
        fitWindow: 0,
        fitWidth: 1,
        fitHeight: 2,
        fitLast: 3,
        fitImage: 4,
        fitCustom: 5
    },
    corners: {
        upperLeft: 0,
        upperRight: 1,
        lowerLeft: 2,
        lowerRight: 3
    },
    lineEnds: {
        start: 0,
        end: 1
    },
    multipleDocModes: {
        viewedDocuments: 0,
        specifiedDocuments: 1,
        availableDocuments: 2
    },
    fontNames: [ 
        "Helvetica", 
        "Times New Roman", 
        "Arial", 
        "Courier", 
        "Courier New" 
    ],
    fontSizes: [
        8, 9, 10, 11, 12, 14, 18, 24, 36
    ],
    annColors: [
        "FFFFFF", "000000", "C0C0C0", "008000", "FE0000", "FF7518", 
        "FFFF00", "0000FF", "808000", "808080", "FF00FF", "800080", 
        "800000", "00FF21", "FFD800", "000080", "008080", "00FFFF",
        "D2B48C", "F5F5DC", "73C2FB", "98FB98", "DCD0FF", "FDFD96"
    ],

    localizeOptions: {
        //language: "zz",
        pathPrefix: "resources/locale"
    },
    aboutDialogTextContents: "&reg;1996-2013 All Rights Reserved.  (licensing information). VirtualViewer and the VirtualViewer logo are trademarks of the Snowbound Software Corporation. All Rights Reserved. Some of the trademarks used under license from partner companies."
};







