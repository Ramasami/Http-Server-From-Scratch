package httpserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import httpserver.config.Configuration;
import httpserver.config.ConfigurationManager;
import httpserver.core.ServerListenerThread;

import java.io.IOException;

public class SimpleHttpServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpServer.class);

    public static void main(String[] args) {
        LOGGER.info("Server Starting");
        ConfigurationManager.getInstance().loadConfigurationFile("src/main/resources/http.conf");
        Configuration conf = ConfigurationManager.getInstance().getCurrentConfiguration();

        LOGGER.info("Using port: " + conf.getPort());
        LOGGER.info("Using webroot: " + conf.getWebroot());

        ServerListenerThread serverListenerThread = null;
        try {
            serverListenerThread = new ServerListenerThread(conf.getPort(),conf.getWebroot());
            serverListenerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
