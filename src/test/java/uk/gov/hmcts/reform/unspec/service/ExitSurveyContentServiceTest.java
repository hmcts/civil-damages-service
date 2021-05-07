package uk.gov.hmcts.reform.unspec.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.unspec.config.ExitSurveyConfiguration;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = {ExitSurveyContentService.class, ExitSurveyConfiguration.class},
    properties = {
        "exit-survey.applicant-link: http://applicant.com",
        "exit-survey.respondent-link: http://respondent.com"
    })
class ExitSurveyContentServiceTest {

    @Autowired
    ExitSurveyContentService service;

    @Test
    void shouldReturnApplicantSurveyContents_whenInvoked() {
        //TODO:
        service.applicantSurvey();
    }

    @Test
    void shouldReturnRespondentSurveyContents_whenInvoked() {
        //TODO:
    }
}
