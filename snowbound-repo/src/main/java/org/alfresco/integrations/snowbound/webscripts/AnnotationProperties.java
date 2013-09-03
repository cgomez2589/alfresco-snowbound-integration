package org.alfresco.integrations.snowbound.webscripts;

import org.alfresco.integrations.snowbound.model.SnowboundContentModel;
import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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
public class AnnotationProperties extends AbstractWebScript {
    private static final Log logger = LogFactory.getLog(AnnotationProperties.class);

    private NodeService nodeService;
    private NodeRef documentNodeRef = null;

    public void execute(WebScriptRequest request, WebScriptResponse response) throws IOException {
        try{

            this.documentNodeRef = new NodeRef(request.getParameter("nodeRef"));
            logger.debug("NodeRef: " + this.documentNodeRef.toString());

            List<ChildAssociationRef> childAssocList = this.nodeService.getChildAssocs(
                    this.documentNodeRef,
                    SnowboundContentModel.ASSOC_ANNOTATION,
                    RegexQNamePattern.MATCH_ALL);
            logger.debug("Child List Size: " + childAssocList.size());
            JSONObject jsonObject = new JSONObject();

            JSONArray jsonArray = new JSONArray();

            for(ChildAssociationRef childAssociationRef : childAssocList){
                JSONObject innerJSONObject = new JSONObject();

                logger.debug("Child Assoc Ref: " + childAssociationRef.toString());
                NodeRef childNodeRef = childAssociationRef.getChildRef();
                innerJSONObject.put("id", childNodeRef.toString());

                String name = (String) this.nodeService.getProperty(childNodeRef, ContentModel.PROP_NAME);
                innerJSONObject.put("name", name);

                String permissionLevel = String.valueOf(this.nodeService.getProperty(childNodeRef, SnowboundContentModel.PROP_PERMISSION_LEVEL));
                innerJSONObject.put("permissionLevel", permissionLevel);

                String redactionFlag = String.valueOf(this.nodeService.getProperty(childNodeRef, SnowboundContentModel.PROP_REDACTION_FLAG));
                innerJSONObject.put("redactionFlag", redactionFlag);

                jsonArray.put(innerJSONObject);
            }
            jsonObject.put("annotations", jsonArray);

            logger.debug("JSON Array: " + jsonArray.toString());
            response.getWriter().write(jsonObject.toString());
        }
        catch(JSONException e){
            logger.debug("Failed to serialize JSON object: ", e);
        }
    }

    public void setNodeService(NodeService nodeService) {
        this.nodeService = nodeService;
    }


}
