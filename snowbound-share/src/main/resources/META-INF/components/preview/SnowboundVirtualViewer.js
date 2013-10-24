Alfresco.WebPreview.prototype.Plugins.SnowboundVirtualViewer = function(wp, attributes) {
	this.wp = wp;
	this.attributes = YAHOO.lang.merge(Alfresco.util.deepCopy(this.attributes), attributes);
	return this;
};
Alfresco.WebPreview.prototype.Plugins.SnowboundVirtualViewer.prototype = {
	attributes: {
		src: null
	},
	report: function SnowboundVirtualViewer_report() {
		return null;
	},
	display: function SnowboundVirtualViewer_display() {
        Alfresco.util.Ajax.request({
			url: Alfresco.constants.PROXY_URI + "org/alfresco/integrations/snowbound/ticket",
			successCallback: {
				fn: function SnowboundVirtualViewer_onLoadViewer(response) {
                    var name = this.wp.options.name;
					var nodeRef = this.wp.options.nodeRef.replace("://", "/");
					var ticket = response.serverResponse.responseText;
					previewHeight = this.wp.setupPreviewSize();

					var url = "http://localhost:8080/VirtualViewerJavaHTML5/index.html?" +
                        "documentId=" + nodeRef + "&amp;" +
                        "clientInstanceId=" + ticket;
					this.wp.getPreviewerElement()
						.innerHTML = '<iframe id="' + this.wp.id +'" name="Embed" src="' + url + '" scrolling="yes" marginwidth="0" marginheight="0" frameborder="0" vspace="0" hspace="0"  style="height:'
                        + (previewHeight - 10).toString() + 'px; width:100%"></iframe>';

					return this.wp.getPreviewerElement()
						.innerHTML;
				},
				scope: this
			},
			failureMessage: this.wp.msg("Failed to load Snowbound VirtualViewer!")
		});
	}
};
