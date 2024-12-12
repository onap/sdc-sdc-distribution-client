package org.onap.sdc;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.TestPropertySource;

@EnableAutoConfiguration
@TestPropertySource("classpath:sdc-distribution-client.properties")
public class IntegrationTest {

}
