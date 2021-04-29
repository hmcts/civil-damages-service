package uk.gov.hmcts.reform.unspec.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ExitSurveyConfiguration {

    private final String claimantSurvey;
    private final String defendantSurvey;

    public ExitSurveyConfiguration(@Value("${exitsurvey.claimant}") String claimantSurvey,
                                   @Value("${exitsurvey.defendant}") String defendantSurvey) {
        this.claimantSurvey = claimantSurvey;
        this.defendantSurvey = defendantSurvey;
    }
}
