package org.epam;

import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class SpringCoreTask {

    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8000);

        tomcat.getConnector();

        String webappDirLocation = "web";
        tomcat.addWebapp("", new File(webappDirLocation).getAbsolutePath());

        tomcat.start();
        System.out.println("Tomcat started on port 8000");
        tomcat.getServer().await();
    }
}