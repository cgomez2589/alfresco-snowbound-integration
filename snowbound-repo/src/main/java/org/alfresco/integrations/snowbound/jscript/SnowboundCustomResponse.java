package org.alfresco.integrations.snowbound.jscript;

import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.repo.jscript.ScriptUtils;
import org.alfresco.repo.jscript.app.CustomResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Author: Kyle Adams
 * Date: 10/23/13
 * Time: 5:35 PM
 */
public class SnowboundCustomResponse implements CustomResponse {
    private static Log logger = LogFactory.getLog(SnowboundCustomResponse.class);

    private static final String SNOWBOUND_MODULE = "org.alfresco.integrations.snowbound";

    private SysAdminParams sysAdminParams;
    private ScriptUtils scriptUtils;
    private String snowboundServerProtocol;
    private String snowboundServerHost;
    private int snowboundServerPort = 0;
    private String snowboundContext;

    @Override
    public Serializable populate() {
        // Check if the module is installed
        if (!scriptUtils.moduleInstalled(SNOWBOUND_MODULE)){
            logger.error(SNOWBOUND_MODULE + " module is not installed!");
            return null;
        }

        Map<String, Serializable> jsonObj = new LinkedHashMap<String, Serializable>(4);
        if (snowboundServerProtocol != null){
            jsonObj.put("protocol", snowboundServerProtocol);
        }
        if (snowboundServerHost != null){
            jsonObj.put("host", sysAdminParams.subsituteHost(snowboundServerHost));
        }
        if (snowboundServerPort != 0){
            jsonObj.put("port", snowboundServerPort);
        }
        if (snowboundContext != null){
            jsonObj.put("contextPath", snowboundContext);
        }
        return (Serializable)jsonObj;
    }

    public void setSysAdminParams(SysAdminParams sysAdminParams) {
        this.sysAdminParams = sysAdminParams;
    }

    public void setScriptUtils(ScriptUtils scriptUtils) {
        this.scriptUtils = scriptUtils;
    }

    public void setSnowboundServerProtocol(String snowboundServerProtocol) {
        this.snowboundServerProtocol = snowboundServerProtocol;
    }

    public void setSnowboundServerHost(String snowboundServerHost) {
        this.snowboundServerHost = snowboundServerHost;
    }

    public void setSnowboundServerPort(int snowboundServerPort) {
        this.snowboundServerPort = snowboundServerPort;
    }

    public void setSnowboundContext(String snowboundContext) {
        this.snowboundContext = snowboundContext;
    }
}
