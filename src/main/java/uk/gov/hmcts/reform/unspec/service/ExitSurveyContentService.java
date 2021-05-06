package uk.gov.hmcts.reform.unspec.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.unspec.config.ExitSurveyConfiguration;

@Service
@RequiredArgsConstructor
public class ExitSurveyContentService {

    private final ExitSurveyConfiguration exitSurveyConfiguration;
    static final String feedbackLink = "%n%n<br/><br/>This is a new service - your <a href=\"%s\" target=\"_blank\">feedback</a> will help us to improve it.";

    public String applicantSurvey() {

        return String.format(feedbackLink,
                             exitSurveyConfiguration.getApplicantSurvey());
    }

    public String respondentSurvey() {
        return String.format(feedbackLink,
                              exitSurveyConfiguration.getRespondentSurvey());
    }
}
