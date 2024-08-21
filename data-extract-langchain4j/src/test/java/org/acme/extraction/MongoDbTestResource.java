/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.acme.extraction;

import java.util.HashMap;
import java.util.Map;

import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.TestcontainersConfiguration;

import static org.testcontainers.utility.MountableFile.forClasspathResource;

public class MongoDbTestResource implements QuarkusTestResourceLifecycleManager {
    private static final Logger LOG = LoggerFactory.getLogger(MongoDbTestResource.class);

    private static final int MONGODB_PORT = 27017;
    private static final String MONGODB_IMAGE = "docker.io/mongo:4.4";
    private static final String MONGODB_ROOT_USERNAME = "admin";
    private static final String MONGODB_ROOT_PASSWORD = "password";

    private GenericContainer<?> container;

    @Override
    public Map<String, String> start() {
        LOG.info(TestcontainersConfiguration.getInstance().toString());

        try {
            container = new GenericContainer(MONGODB_IMAGE)
                    .withExposedPorts(MONGODB_PORT)
                    .withEnv("MONGO_INITDB_ROOT_USERNAME", MONGODB_ROOT_USERNAME)
                    .withEnv("MONGO_INITDB_ROOT_PASSWORD", MONGODB_ROOT_PASSWORD)
                    .withCopyFileToContainer(forClasspathResource("/mongodb/mongo-init.js"),
                            "/docker-entrypoint-initdb.d/mongo-init.js")
                    .withLogConsumer(new Slf4jLogConsumer(LOG).withPrefix("basicAuthContainer"))
                    .waitingFor(Wait.forLogMessage(".*Waiting for connections.*", 2));

            container.start();

            String authority = container.getHost() + ":" + container.getMappedPort(MONGODB_PORT).toString();
            String connectionString = String.format("mongodb://%s", authority);

            Map<String, String> config = new HashMap<>();
            config.put("quarkus.mongodb.hosts", authority);
            config.put("quarkus.mongodb.credentials.username", MONGODB_ROOT_USERNAME);
            config.put("quarkus.mongodb.credentials.password", MONGODB_ROOT_PASSWORD);
            config.put("quarkus.mongodb.myMongoClient.connection-string", connectionString);

            return config;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void stop() {
        try {
            if (container != null) {
                container.stop();
            }
        } catch (Exception e) {
            LOG.warn("An exception occurred while stopping the MongoDB container", e);
        }
    }
}
