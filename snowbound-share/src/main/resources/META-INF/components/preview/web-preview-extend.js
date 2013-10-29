(function()
{
	if (typeof SnowboundVirtualViewer == "undefined" || !SnowboundVirtualViewer)
	{
		SnowboundVirtualViewer = {};
	}
	if (typeof SnowboundVirtualViewer.ViewerExtension == "undefined" || !SnowboundVirtualViewer.ViewerExtension)
	{
		SnowboundVirtualViewer.ViewerExtension = {};
	}

	SnowboundVirtualViewer.ViewerExtension.prototype =
	{
	   /**
	    * Minimum height of the viewer in pixels
	    */
	   viewerMinHeight: 400,
	   
	   /**
	    * Default options which control which elements space is allowed for
	    */
	   viewerSizeOptsDefault:
	   {
	      commentsList: true,
	      commentContent: true,
	      siteNavigation: true,
	      nodeHeader: true
	   },
	   
		/**
		 * Set up the available preview size
		 * 
		 * @method setupPreviewSize
		 * @param {object} opts  Object containing a set of boolean properties indicating which elements to allow vertical space for. Default is all if not specified.
		 * @return {integer} size in pixels that preview div is set to
		 * @public
		 */
		setupPreviewSize : function WP_setupPreviewSize(opts)
		{
		   opts = YAHOO.lang.merge(this.viewerSizeOptsDefault, opts || {});
			var sourceYuiEl = new YAHOO.util.Element(this.widgets.previewerElement), previewHeight, docHeight = YAHOO.util.Dom.getDocumentHeight(), clientHeight = YAHOO.util.Dom
					.getClientHeight(), elementPresent;
			// Take the smaller of the two
			previewHeight = (docHeight < clientHeight) ? docHeight : clientHeight;

			// see if the comments are loaded
			elementPresent = YAHOO.util.Dom.getElementsByClassName("comments-list");
			if (elementPresent.length > 0 && opts.commentsList)
			{
				// there is a comment section, subtract space for that
				previewHeight = previewHeight - 93; // WA reduce from 108 to 93, includes 'Comments' heading, Add Comment button and HR.
			}
			elementPresent = YAHOO.util.Dom.getElementsByClassName("comment-content");
			if (elementPresent.length > 0 && opts.commentContent)
			{
				// there is a comment, at least allow for some of it to display
				previewHeight = previewHeight - 110;
			}
			elementPresent = YAHOO.util.Dom.getElementsByClassName("site-navigation");
			if (elementPresent.length > 0 && opts.siteNavigation)
			{
				// there is a navigation section, subtract space for that
				previewHeight = previewHeight - 125;
			}
			elementPresent = YAHOO.util.Dom.getElementsByClassName("node-header");
			if (elementPresent.length > 0 && opts.nodeHeader)
			{
				// there is a node header section, subtract space for that
				previewHeight = previewHeight - 110;
			}
			
			previewHeight = Math.max(previewHeight, this.viewerMinHeight);
			
			sourceYuiEl.setStyle("height", previewHeight + "px");

			return previewHeight;

		}

	}

	YAHOO.lang.augmentProto(Alfresco.WebPreview, SnowboundVirtualViewer.ViewerExtension);
})();