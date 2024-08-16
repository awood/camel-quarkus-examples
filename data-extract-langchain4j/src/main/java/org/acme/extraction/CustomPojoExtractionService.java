package org.acme.extraction;

import java.time.LocalDate;

import dev.langchain4j.service.UserMessage;
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

    /**
     * See how the java method parameter named text is automatically injected as {text} in the
     * CUSTOM_POJO_EXTRACT_PROMPT.
     *
     * This is made possible as the code is compiled with -parameters argument in the maven-compiler-plugin related
     * section of the pom.xml file.
     *
     * Without -parameters, one would need to use the @V annotation like in the method signature proposed below:
     * extractFromText(@dev.langchain4j.service.V("text") String text);
     */
    @UserMessage(CUSTOM_POJO_EXTRACT_PROMPT)
    @Handler
    CustomPojo extractFromText(String text);
}
