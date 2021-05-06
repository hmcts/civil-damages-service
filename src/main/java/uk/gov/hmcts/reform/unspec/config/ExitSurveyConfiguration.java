package uk.gov.hmcts.reform.unspec.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ExitSurveyConfiguration {

    private final String applicantSurvey;
    private final String respondentSurvey;

    public ExitSurveyConfiguration(@Value("${exitsurvey.claimant}") String applicantSurvey,
                                   @Value("${exitsurvey.defendant}") String respondentSurvey) {
        this.applicantSurvey = applicantSurvey;
        this.respondentSurvey = respondentSurvey;
    }

}
