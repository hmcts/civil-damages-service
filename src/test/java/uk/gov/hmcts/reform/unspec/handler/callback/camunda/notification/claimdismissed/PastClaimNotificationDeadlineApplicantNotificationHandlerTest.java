package uk.gov.hmcts.reform.unspec.handler.callback.camunda.notification.claimdismissed;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.config.properties.notification.NotificationsProperties;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.sampledata.CallbackParamsBuilder;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDataBuilder;
import uk.gov.hmcts.reform.unspec.service.NotificationService;

import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.hmcts.reform.unspec.callback.CallbackType.ABOUT_TO_SUBMIT;

@SpringBootTest(classes = {
    PastClaimNotificationDeadlineApplicantNotificationHandler.class,
    NotificationsProperties.class,
    JacksonAutoConfiguration.class
})
class PastClaimNotificationDeadlineApplicantNotificationHandlerTest {

    public static final String TEMPLATE_ID = "template-id";
    public static final String EMAIL = "applicantsolicitor@example.com";

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private NotificationsProperties notificationsProperties;

    @Autowired
    private PastClaimNotificationDeadlineApplicantNotificationHandler handler;

    @Nested
    class AboutToSubmitCallback {

        @BeforeEach
        void setup() {
            when(notificationsProperties.getApplicantSolicitorClaimDismissedPastClaimNotificationDeadline())
                .thenReturn(TEMPLATE_ID);
            when(notificationsProperties.getApplicantSolicitorEmail()).thenReturn(EMAIL);
        }

        @Test
        void shouldNotifyApplicantSolicitor_whenInvoked() {
            CaseData caseData = CaseDataBuilder.builder().atStateApplicantRespondToDefenceAndProceed().build();
            CallbackParams params = CallbackParamsBuilder.builder().of(ABOUT_TO_SUBMIT, caseData).build();

            handler.handle(params);

            verify(notificationService).sendMail(
                EMAIL,
                TEMPLATE_ID,
                getExpectedMap(),
                "claim-dismissed-past-claim-notification-deadline-applicant-notification-000LR001"
            );
        }
    }

    private Map<String, String> getExpectedMap() {
        return Map.of(
            "claimReferenceNumber", "000LR001",
            "defendantName", "Mr. Sole Trader"
        );
    }

}
