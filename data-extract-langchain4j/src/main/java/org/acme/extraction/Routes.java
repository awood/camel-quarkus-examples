package org.acme.extraction;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.apache.camel.builder.RouteBuilder;

@ApplicationScoped
public class Routes extends RouteBuilder {

    @Inject
    CustomPojoExtractionService customPojoExtractionService;

    @Override
    public void configure() {
        from("platform-http:/custom-pojo-extraction-service?produces=application/json")
                .log("Received ${body}")
                .bean(customPojoExtractionService)
                .bean(this, "toPrettyFormat");

        fromF("mongodb:myMongoClient?database=transcripts_db&collection=transcripts_collection")
                .log("!!!!!!!!!!!!!!!! Mongo Received ${body}");
    }

    private final static String FORMAT = "{"
            + "\"customerSatisfied\": \"%s\","
            + "\"customerName\": \"%s\","
            + "\"customerBirthday\": \"%td %tB %tY\","
            + "\"summary\": \"%s\""
            + "}";

    public static String toPrettyFormat(CustomPojoExtractionService.CustomPojo extract) {
        return String.format(FORMAT, extract.customerSatisfied, extract.customerName, extract.customerBirthday,
                extract.customerBirthday, extract.customerBirthday, extract.summary);
    }
}
