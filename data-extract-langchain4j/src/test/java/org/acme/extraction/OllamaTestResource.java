package org.acme.extraction;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;

public class OllamaTestResource implements QuarkusTestResourceLifecycleManager {

    private static final Logger LOG = LoggerFactory.getLogger(OllamaTestResource.class);
    private static final String OLLAMA_IMAGE = "langchain4j/ollama-codellama:latest";
    private static final int OLLAMA_SERVER_PORT = 11434;

    private GenericContainer<?> ollamaContainer;

    @Override
    public Map<String, String> start() {
        Map<String, String> properties = new HashMap<>();

        LOG.info("Starting Ollama container resource");
        ollamaContainer = new GenericContainer<>(OLLAMA_IMAGE)
                .withExposedPorts(OLLAMA_SERVER_PORT)
                .withLogConsumer(new Slf4jLogConsumer(LOG).withPrefix("basicAuthContainer"))
                .waitingFor(Wait.forLogMessage(".* msg=\"inference compute\" .*", 1));

        ollamaContainer.start();

        String baseUrl = String.format("http://%s:%s", ollamaContainer.getHost(),
                ollamaContainer.getMappedPort(OLLAMA_SERVER_PORT));
        properties.put("quarkus.langchain4j.ollama.base-url", baseUrl);

        return properties;
    }

    @Override
    public void stop() {
        try {
            if (ollamaContainer != null) {
                ollamaContainer.stop();
            }
        } catch (Exception ex) {
            LOG.error("An issue occurred while stopping " + ollamaContainer.getNetworkAliases(), ex);
        }
    }

}
