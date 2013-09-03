package org.alfresco.integrations.snowbound.webscripts;

import org.alfresco.integrations.snowbound.model.SnowboundContentModel;
import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import java.io.IOException;
import java.util.List;

/**
 * Author: Kyle Adams
 * Date: 8/7/13
 * Time: 11:36 AM
 */
public class BookmarkContent extends AbstractWebScript {
    private static final Log logger = LogFactory.getLog(BookmarkContent.class);

    private ServiceRegistry serviceRegistry;
    private NodeRef documentNodeRef = null;

    public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException {
        try{
            this.documentNodeRef = new NodeRef(request.getParameter("nodeRef"));
            logger.debug("NodeRef: " + this.documentNodeRef.toString());

            List<ChildAssociationRef> childAssocList = this.serviceRegistry.getNodeService().getChildAssocs(
                    this.documentNodeRef,
                    SnowboundContentModel.ASSOC_BOOKMARK,
                    RegexQNamePattern.MATCH_ALL);
            logger.debug("Child List Size: " + childAssocList.size());

            ContentReader contentReader = this.serviceRegistry.getContentService().getReader(
                    childAssocList.iterator().next().getChildRef(), ContentModel.PROP_CONTENT);
            contentReader.getContent(response.getOutputStream());
        }
        catch(Exception e){
            logger.debug("Failed to get Bookmark content: ", e);
        }
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }
}
