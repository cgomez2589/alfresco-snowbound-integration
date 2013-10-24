This provide an integration between the Alfresco Share collaboration UI and the Snowbound VirtualViewer

# alfresco-content-handler
Snowbound Content Handler java class that integration with Alfresco's Web Scripts (RESTful services)

VirtualViewer Reference: http://www.virtualviewer.com/VirtualViewerJavaAJAXHelp/virtualviewer.htm

# snowbound-repo
Repository Web Scripts, content model, global properties

# snowbound-share
Share presentation layer that contains configuration for the Snowbound VirtualViewer iFrame


# Installation
* Checkout snowbound-repo, snowbound-share, and alfresco-content-handler from GitHub.
* Change directory to snowbound-repo and snowbound-share separately.
* Run the following Maven commands for each project:

```bash
$ mvn clean
```

```bash
$ mvn package
```
* Copy the snowbound-repo-*.amp to the <ALF_ROOT>/amps directory.
* Copy the snowbound-share-*.amp to the <ALF_ROOT>/amps_share directory.
* Make sure you Alfresco intance is stopped.
* Change directory to the <ALF_ROOT>/bin directory.
* Run the following command:

```bash
$ ./apply_amps -verbose
```
* Review the output and confirm that the Snowbound VirtualViewer Integration module was applied successfully.
* Download the VirtualViewer - HTML5 Java version from: http://register.snowbound.com/VirtualViewer_eval.html
* Copy the VirtualViewerJavaHTML5.war to the <ALF_ROOT>/tomcat/webapps directory.
* Change directory to the alfresco-content-handler directory.
* Confirm that the vv.deploy variable in the build.properties is pointing to the correct location in which the VirtualViewerJavaHTML5.war file resides.
* Run the following command:

```bash
$ ant install
```
* Start the tomcat instance
* Upload a PDF, navigate to the document-details view, and confirm that the Snowbound VirtualViewer renders the PDF as opposed to the Alfresco flash web-preview.


# Mime Type Current Configured
* application/pdf
* image/tiff
* image/bmp
* image/gif
* image/jpeg
* application/msword
* application/vnd.openxmlformats-officedocument.wordprocessingml.document
* application/vnd.ms-excel
* application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
* application/vnd.ms-powerpoint
* application/vnd.openxmlformats-officedocument.presentationml.presentation
* application/vnd.ms-outlook
* image/vnd.dwg

For the full list of supported file types, refer to Appendix D in the VirtualViewer documentation: http://www.virtualviewer.com/VirtualViewerJavaAJAXHelp/virtualviewer.htm
