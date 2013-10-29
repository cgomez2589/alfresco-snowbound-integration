package org.alfresco.integrations.snowbound.webscripts;

import com.google.gson.Gson;
import org.alfresco.integrations.snowbound.entity.PreferenceXML;
import org.alfresco.integrations.snowbound.model.SnowboundContentModel;
import org.alfresco.model.ContentModel;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.*;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.io.IOUtils;
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
public class GetPreferenceXML extends AbstractWebScript {
    private static final Log logger = LogFactory.getLog(GetPreferenceXML.class);

    private ServiceRegistry serviceRegistry;

    public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException {
        PreferenceXML preferenceXML = null;
        try{
            NodeService nodeService = serviceRegistry.getNodeService();

            NodeRef documentNodeRef = new NodeRef(request.getParameter("nodeRef"));
            logger.debug("NodeRef: " + documentNodeRef.toString());

            List<ChildAssociationRef> preferenceAssocList = nodeService.getChildAssocs(
                    documentNodeRef,
                    SnowboundContentModel.ASSOC_PREFERENCE,
                    RegexQNamePattern.MATCH_ALL);
            if(!preferenceAssocList.isEmpty()){
                preferenceXML = new PreferenceXML();

                NodeRef bookmarkNodeRef = preferenceAssocList.iterator().next().getChildRef();
                logger.debug("PreferenceXML NodeRef: " + bookmarkNodeRef);

                preferenceXML.setId(bookmarkNodeRef.toString());

                ContentService contentService = serviceRegistry.getContentService();
                ContentReader contentReader = contentService.getReader(bookmarkNodeRef, ContentModel.PROP_CONTENT);
                preferenceXML.setContent(IOUtils.toByteArray(contentReader.getContentInputStream()));
            }
            String jsonString = new Gson().toJson(preferenceXML);
            logger.trace("PreferenceXML JSON Object: " + jsonString);
            response.getWriter().write(jsonString);
        }
        catch(Exception e){
            logger.debug("Failed to serialize JSON object: ", e);
        }
    }

    public void setServiceRegistry(ServiceRegistry serviceRegistry) {
        this.serviceRegistry = serviceRegistry;
    }
}
