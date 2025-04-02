package org.epam;

import lombok.extern.log4j.Log4j2;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

@Log4j2
public class SpringCoreTask {

    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8000);

        tomcat.getConnector();
        tomcat.enableNaming();

        String webappDirLocation = "web";
        tomcat.addWebapp("/", new File(webappDirLocation).getAbsolutePath());

        tomcat.start();
        log.info("Tomcat started on port 8000");
        tomcat.getServer().await();
    }
}