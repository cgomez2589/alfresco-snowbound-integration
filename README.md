This provide an integration between the Alfresco Share collaboration UI and the Snowbound VirtualViewer

# alfresco-content-handler
Snowbound Content Handler java class that integration with Alfresco's Web Scripts (RESTful services)
VirtualViewer Reference: http://www.virtualviewer.com/VirtualViewerJavaAJAXHelp/virtualviewer.htm

# snowbound-repo
Repository Web Scripts, content model, global properties

# snowbound-share
Share presentation layer that contains configuration for the Snowbound VirtualViewer iFrame


# Installation
1. Checkout snowbound-repo, snowbound-share, and alfresco-content-handler from GitHub.
2. Change directory to snowbound-repo and snowbound-share separately.
3. Run the following Maven commands for each project:

```bash
$ mvn clean
```

```bash
$ mvn package
```

4. Copy the snowbound-repo-*.amp to the <ALF_ROOT>/amps directory.
5. Copy the snowbound-share-*.amp to the <ALF_ROOT>/amps_share directory.
6. Make sure you Alfresco intance is stopped.
7. Change directory to the <ALF_ROOT>/bin directory.
8. Run the following command:

```bash
$ ./apply_amps -verbose
```

9. Review the output and confirm that the Snowbound VirtualViewer Integration module was applied successfully.
10. Download the VirtualViewer - HTML5 Java version from: http://register.snowbound.com/VirtualViewer_eval.html
11. Copy the VirtualViewerJavaHTML5.war to the <ALF_ROOT>/tomcat/webapps directory.
12. Change directory to the alfresco-content-handler directory.
13. Confirm that the vv.deploy variable in the build.properties is pointing to the correct location in which the VirtualViewerJavaHTML5.war file resides.
14. Run the following command:

```bash
$ ant install
```
15. Start the tomcat instance
16. Upload a PDF, navigate to the document-details view, and confirm that the Snowbound VirtualViewer renders the PDF as opposed to the Alfresco flash web-preview.

