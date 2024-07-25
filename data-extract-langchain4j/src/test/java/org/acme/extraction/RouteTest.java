package org.acme.extraction;

import java.io.IOException;

import com.github.dockerjava.zerodep.shaded.org.apache.commons.codec.Charsets;
import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.apache.commons.io.IOUtils.resourceToString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

@QuarkusTestResource(OllamaTestResource.class)
@QuarkusTest
public class RouteTest {

    @Test
    void customPojoExtractionServiceReturnPojo() throws IOException {

        RestAssuredConfig newConfig = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig().setParam("http.connection.timeout", 180000));

        String conversation = resourceToString("/texts/03_kate-boss-13-08-1999-satisfied.txt", Charsets.UTF_8);

        given()
                .config(newConfig)
                .contentType(ContentType.JSON)
                .body(conversation)
                .when()
                .post("/custom-pojo-extraction-service")
                .then()
                .statusCode(200)
                .body("customerSatisfied", is("true"))
                .body("customerName", is("Kate Boss"))
                .body("customerBirthday", is("13 August 1999"))
                .body("summary", not(empty()));
    }

}
