package com.alborworld.runnerapp.integration;

import java.io.IOException;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class ServerFactory {
    private static final String CONTEXT_PATH = "/";

    private final String serverUrl;
    private final String configurationContext;

    public ServerFactory(final String serverUrl, final String configurationContext) {
        this.serverUrl = serverUrl;
        this.configurationContext = configurationContext;
    }

    public Server createServer() throws IOException {
        Server server = new Server();
        server.setHandler(getServletContextHandler(getConfigurationContext(configurationContext)));
        return server;
    }

    private Handler getServletContextHandler(final WebApplicationContext context) throws IOException {
        ServletContextHandler contextHandler = new ServletContextHandler();
        contextHandler.setErrorHandler(null);
        contextHandler.setContextPath(CONTEXT_PATH);
        contextHandler.addServlet(new ServletHolder(new DispatcherServlet(context)), serverUrl);
        contextHandler.addEventListener(new ContextLoaderListener(context));
        contextHandler.setResourceBase(new ClassPathResource("webapp").getURI().toString());
        return contextHandler;
    }

    private WebApplicationContext getConfigurationContext(final String contextPath) {
        XmlWebApplicationContext context = new XmlWebApplicationContext();
        context.setConfigLocation(contextPath);
        return context;
    }
}
