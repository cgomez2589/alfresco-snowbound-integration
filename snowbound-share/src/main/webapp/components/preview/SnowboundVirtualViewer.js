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
		// Load available thumbnail definitions, i.e. which thumbnails have been generated already
        var alf_url = window.location.protocol + "//" + window.location.host + "${url.context}";

        Alfresco.util.Ajax.request({
			url: Alfresco.constants.PROXY_URI + "org/alfresco/integrations/org.alfresco.integrations.snowbound/ticket",
			successCallback: {
				fn: function SnowboundVirtualViewer_onLoadViewer(response) {
                    var name = this.wp.options.name;
					var nodeRef = this.wp.options.nodeRef.replace("://", "/");
					var ticket = response.serverResponse.responseText;
					previewHeight = this.wp.setupPreviewSize();

					var url = "http://localhost:8081/VirtualViewerJavaAJAX/ajaxClient.html?" +
                        "documentId=" + nodeRef + "&amp;" +
                        "clientInstanceId=" +
                        "ticket:" + ticket +
                        "name:" + name;
					this.wp.getPreviewerElement()
						.innerHTML = '<iframe id="Embed" name="Embed" src=' + url + ' scrolling="yes" marginwidth="0" marginheight="0" frameborder="0" vspace="0" hspace="0"  style="height:' + (previewHeight - 10)
						.toString() + 'px;"></iframe>';

					return this.wp.getPreviewerElement()
						.innerHTML;
				},
				scope: this
			},
			failureMessage: this.wp.msg("Failed to load Snowbound VirtualViewer!")
		});

	}
};
