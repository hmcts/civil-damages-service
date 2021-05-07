package uk.gov.hmcts.reform.unspec.config;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotBlank;

@Data
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "exitsurvey")
public class ExitSurveyConfiguration {

    @NotBlank
    private final String applicantLink;
    @NotBlank
    private final String respondentLink;
}
