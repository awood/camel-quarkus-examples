package org.acme.extraction;

import java.time.LocalDate;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import io.quarkiverse.langchain4j.RegisterAiService;
import io.quarkus.runtime.annotations.RegisterForReflection;
import jakarta.enterprise.context.ApplicationScoped;
import org.apache.camel.Handler;

@RegisterAiService
@ApplicationScoped
public interface CustomPojoExtractionService {

    @RegisterForReflection
    static class CustomPojo {
        public boolean customerSatisfied;
        public String customerName;
        public LocalDate customerBirthday;
        public String summary;
    }

    static final String CUSTOM_POJO_EXTRACT_PROMPT = "Extract information about a customer from the text delimited by triple backticks: ```{text}```."
            + "The customerBirthday field should be formatted as YYYY-MM-DD."
            + "The summary field should concisely relate the customer main ask.";

    @UserMessage(CUSTOM_POJO_EXTRACT_PROMPT)
    @Handler
    // TODO: It should be possible to remove @V as this is working in data-experiments-camel-quarkus
    // The issue is still there with q-platform 3.12.0/3, 3.13.0.CR1 and maven.compiler.target/source = 21
    CustomPojo extractFromText(@V("text") String text);
    //CustomPojo extractFromText(String text);
}
