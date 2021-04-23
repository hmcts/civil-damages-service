package uk.gov.hmcts.reform.unspec.handler.callback.camunda.caseevents;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CallbackType;
import uk.gov.hmcts.reform.unspec.handler.callback.BaseCallbackHandlerTest;
import uk.gov.hmcts.reform.unspec.helpers.CaseDetailsConverter;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDataBuilder;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {
    ProceedOfflineCallbackHandler.class,
    JacksonAutoConfiguration.class,
    CaseDetailsConverter.class
})
class ProceedOfflineCallbackHandlerTest extends BaseCallbackHandlerTest {

    @Autowired
    ProceedOfflineCallbackHandler proceedOfflineCallbackHandler;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCaptureTakenOfflineDate() {
        CaseData caseData = CaseDataBuilder.builder().atStatePendingClaimIssuedUnRepresentedDefendant().build();
        CallbackParams params = callbackParamsOf(caseData, CallbackType.ABOUT_TO_SUBMIT);

        var response = (AboutToStartOrSubmitCallbackResponse) proceedOfflineCallbackHandler.handle(params);

        assertThat(response.getData()).extracting("takenOfflineDate").isNotNull();
    }
}
