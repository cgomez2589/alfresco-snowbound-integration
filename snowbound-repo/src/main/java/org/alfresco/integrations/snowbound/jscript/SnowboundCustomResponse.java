package org.alfresco.integrations.snowbound.jscript;

import org.alfresco.repo.jscript.app.CustomResponse;

import java.io.Serializable;

/**
 * Author: Kyle Adams
 * Date: 10/23/13
 * Time: 5:35 PM
 */
public class SnowboundCustomResponse implements CustomResponse {

    private String snowboundServerProtocol;
    private String snowboundServerHost;
    private int snowboundServerPort = 0;
    private String snowboundContext;


    @Override
    public Serializable populate() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
