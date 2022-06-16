/*-
 * ============LICENSE_START=======================================================
 * sdc-distribution-client
 * ================================================================================
 * Copyright (C) 2022 Nordix Foundation. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.test.core.service;

import com.github.dockerjava.api.command.InspectContainerResponse;
import lombok.SneakyThrows;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.utility.DockerImageName;

public class CustomKafkaContainer extends FixedHostPortGenericContainer<CustomKafkaContainer> {
    protected String externalZookeeperConnect;

    public CustomKafkaContainer(DockerImageName dockerImageName) {
        super(String.valueOf(dockerImageName));
        this.externalZookeeperConnect = null;
        this.withExposedPorts(9093);
        this.withEnv("KAFKA_LISTENERS", "PLAINTEXT://0.0.0.0:9093,BROKER://0.0.0.0:9092");
        this.withEnv("KAFKA_LISTENER_SECURITY_PROTOCOL_MAP", "BROKER:PLAINTEXT,PLAINTEXT:PLAINTEXT");
        this.withEnv("KAFKA_INTER_BROKER_LISTENER_NAME", "BROKER");
        this.withEnv("KAFKA_BROKER_ID", "1");
        this.withEnv("KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR", "1");
        this.withEnv("KAFKA_OFFSETS_TOPIC_NUM_PARTITIONS", "1");
        this.withEnv("KAFKA_TRANSACTION_STATE_LOG_REPLICATION_FACTOR", "1");
        this.withEnv("KAFKA_TRANSACTION_STATE_LOG_MIN_ISR", "1");
        this.withEnv("KAFKA_LOG_FLUSH_INTERVAL_MESSAGES", "9223372036854775807");
        this.withEnv("KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS", "0");
    }

    public CustomKafkaContainer withEmbeddedZookeeper() {
        this.externalZookeeperConnect = null;
        return this.self();
    }

    public String getBootstrapServers() {
        return String.format("PLAINTEXT://%s:%s", this.getHost(), this.getMappedPort(9093));
    }

    protected void configure() {
        this.withEnv("KAFKA_ADVERTISED_LISTENERS", String.format("BROKER://%s:9092", this.getNetwork() != null ? this.getNetworkAliases().get(0) : "localhost"));
        String command = "";
        if (this.externalZookeeperConnect != null) {
            this.withEnv("KAFKA_ZOOKEEPER_CONNECT", this.externalZookeeperConnect);
        } else {
            this.addExposedPort(2181);
            this.withEnv("KAFKA_ZOOKEEPER_CONNECT", "localhost:2181");
            command = command + "echo 'clientPort=2181' > zookeeper.properties\n";
            command = command + "echo 'dataDir=/var/lib/zookeeper/data' >> zookeeper.properties\n";
            command = command + "echo 'dataLogDir=/var/lib/zookeeper/log' >> zookeeper.properties\n";
            command = command + "zookeeper-server-start zookeeper.properties &\n";
        }

        command = command + "echo '' > /etc/confluent/docker/ensure \n";
        command = command + "/etc/confluent/docker/run \n";
        this.withCommand("sh", "-c", command);
    }

    @SneakyThrows
    protected void containerIsStarted(InspectContainerResponse containerInfo) {
        try {
            String brokerAdvertisedListener = this.brokerAdvertisedListener(containerInfo);
            ExecResult result = this.execInContainer("kafka-configs", "--alter", "--bootstrap-server",
                brokerAdvertisedListener, "--entity-type", "brokers", "--entity-name",
                this.getEnvMap().get("KAFKA_BROKER_ID"), "--add-config",
                "advertised.listeners=[" + String.join(",", this.getBootstrapServers(), brokerAdvertisedListener) + "]");
            if (result.getExitCode() != 0) {
                throw new IllegalStateException(result.toString());
            }
        } catch (Throwable var4) {
            throw var4;
        }
    }

    protected String brokerAdvertisedListener(InspectContainerResponse containerInfo) {
        return String.format("BROKER://%s:%s", containerInfo.getConfig().getHostName(), "9092");
    }
}
