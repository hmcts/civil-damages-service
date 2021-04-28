package uk.gov.hmcts.reform.unspec.handler.callback.camunda.notification;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.reform.ccd.client.model.CallbackRequest;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.config.properties.notification.NotificationsProperties;
import uk.gov.hmcts.reform.unspec.handler.callback.BaseCallbackHandlerTest;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.sampledata.CallbackParamsBuilder;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDataBuilder;
import uk.gov.hmcts.reform.unspec.service.NotificationService;

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.unspec.callback.CallbackType.ABOUT_TO_SUBMIT;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.DATE;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.formatLocalDate;
import static uk.gov.hmcts.reform.unspec.sampledata.CaseDataBuilder.CLAIM_ISSUED_DATE;

@SpringBootTest(classes = {
    DefendantClaimDetailsNotificationHandler.class,
    JacksonAutoConfiguration.class,
})
class DefendantClaimDetailsNotificationHandlerTest extends BaseCallbackHandlerTest {

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private NotificationsProperties notificationsProperties;

    @Autowired
    private DefendantClaimDetailsNotificationHandler handler;

    @Nested
    class AboutToSubmitCallback {

        @BeforeEach
        void setup() {
            when(notificationsProperties.getRespondentSolicitorClaimDetailsEmailTemplate()).thenReturn("template-id");
            when(notificationsProperties.getApplicantSolicitorEmail()).thenReturn("claimantsolicitor@example.com");
            when(notificationsProperties.getRespondentSolicitorEmail()).thenReturn("defendantsolicitor@example.com");
        }

        @Test
        void shouldNotifyRespondentSolicitor_whenInvoked() {
            CaseData caseData = CaseDataBuilder.builder().atStateAwaitingCaseNotification().build();
            CallbackParams params = CallbackParamsBuilder.builder().of(ABOUT_TO_SUBMIT, caseData).request(
                CallbackRequest.builder().eventId("NOTIFY_RESPONDENT_SOLICITOR1_FOR_CLAIM_DETAILS").build()).build();

            handler.handle(params);

            verify(notificationService).sendMail(
                "civilunspecified@gmail.com",
                "template-id",
                getExpectedMap(),
                "claim-details-respondent-notification-000LR001"
            );
        }

        @Test
        void shouldNotifyApplicantSolicitor_whenInvokedWithCcEvent() {
            CaseData caseData = CaseDataBuilder.builder().atStateAwaitingCaseNotification().build();
            CallbackParams params = CallbackParamsBuilder.builder().of(ABOUT_TO_SUBMIT, caseData).request(
                CallbackRequest.builder().eventId("NOTIFY_RESPONDENT_SOLICITOR1_FOR_CLAIM_DETAILS_CC").build()).build();

            handler.handle(params);

            verify(notificationService).sendMail(
                "claimantsolicitor@example.com",
                "template-id",
                getExpectedMap(),
                "claim-details-respondent-notification-000LR001"
            );
        }

        private Map<String, String> getExpectedMap() {
            return Map.of(
                "claimReferenceNumber", "000LR001",
                "defendantName", "Mr. Sole Trader",
                "issuedOn", formatLocalDate(CLAIM_ISSUED_DATE, DATE)
            );
        }
    }

}
