package uk.gov.hmcts.reform.unspec.handler.callback.user;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.SubmittedCallbackResponse;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.config.ExitSurveyConfiguration;
import uk.gov.hmcts.reform.unspec.handler.callback.BaseCallbackHandlerTest;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDataBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.unspec.callback.CallbackType.ABOUT_TO_SUBMIT;
import static uk.gov.hmcts.reform.unspec.callback.CallbackType.SUBMITTED;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.CREATE_CLAIM;
import static uk.gov.hmcts.reform.unspec.enums.YesOrNo.NO;

@SpringBootTest(classes = {
    ResubmitClaimCallbackHandler.class,
    ExitSurveyConfiguration.class,
    JacksonAutoConfiguration.class,
    CaseDetailsConverter.class
})
class ResubmitClaimCallbackHandlerTest extends BaseCallbackHandlerTest {

    @Autowired
    ResubmitClaimCallbackHandler handler;

    @Value("${exitsurvey.claimant}")
    private String claimantSurvey;

    @Nested
    class AboutToSubmitCallback {

        @Test
        void shouldUpdateBusinessProcess_whenInvoked() {
            CaseData caseData = CaseDataBuilder.builder().atStateProceedsOfflineAdmissionOrCounterClaim().build();
            CallbackParams params = callbackParamsOf(caseData, ABOUT_TO_SUBMIT);

            var response = (AboutToStartOrSubmitCallbackResponse) handler.handle(params);

            assertThat(response.getData())
                .extracting("businessProcess")
                .extracting("camundaEvent")
                .isEqualTo(CREATE_CLAIM.name());

            assertThat(response.getData())
                .extracting("businessProcess")
                .extracting("status")
                .isEqualTo("READY");
        }
    }

    @Nested
    class SubmittedCallback {

        @Test
        void shouldReturnExpectedSubmittedCallbackResponse_whenInvoked() {
            CaseData caseData = CaseDataBuilder.builder().atStateClaimCreated().respondent1Represented(NO).build();
            CallbackParams params = callbackParamsOf(caseData, SUBMITTED);
            SubmittedCallbackResponse response = (SubmittedCallbackResponse) handler.handle(params);

            assertThat(response).usingRecursiveComparison().isEqualTo(
                SubmittedCallbackResponse.builder()
                    .confirmationHeader("# Claim pending")
                    .confirmationBody(String.format("## What happens next %n "
                                                        + "You claim will be processed. Wait for us to contact you."
                                                        + "%n%n<br/><br/>This is a new service - your <a href=\"%s\" target=\"_blank\">feedback</a> will help us to improve it.",
                                                    claimantSurvey))
                    .build());
        }
    }
}
