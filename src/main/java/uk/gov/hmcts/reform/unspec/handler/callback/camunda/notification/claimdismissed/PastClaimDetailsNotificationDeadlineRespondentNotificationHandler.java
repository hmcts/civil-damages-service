package uk.gov.hmcts.reform.unspec.handler.callback.camunda.notification.claimdismissed;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;
import uk.gov.hmcts.reform.unspec.callback.Callback;
import uk.gov.hmcts.reform.unspec.callback.CallbackHandler;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;
import uk.gov.hmcts.reform.unspec.config.properties.notification.NotificationsProperties;
import uk.gov.hmcts.reform.unspec.handler.callback.camunda.notification.NotificationData;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.service.NotificationService;

import java.util.List;
import java.util.Map;

import static uk.gov.hmcts.reform.unspec.callback.CallbackType.ABOUT_TO_SUBMIT;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.NOTIFY_RESPONDENT_SOLICITOR1_PAST_CLAIM_DETAILS_NOTIFICATION_DEADLINE;

@Service
@RequiredArgsConstructor
public class PastClaimDetailsNotificationDeadlineRespondentNotificationHandler extends CallbackHandler
    implements NotificationData {

    private static final List<CaseEvent> EVENTS = List.of(
            NOTIFY_RESPONDENT_SOLICITOR1_PAST_CLAIM_DETAILS_NOTIFICATION_DEADLINE);
    public static final String TASK_ID = "ClaimDismissedPastClaimDetailsNotificationDeadlineNotifyRespondentSolicitor1";
    private static final String REFERENCE_TEMPLATE =
        "claim-dismissed-past-claim-details-notification-deadline-respondent-notification-%s";

    private final NotificationService notificationService;
    private final NotificationsProperties notificationsProperties;

    @Override
    protected Map<String, Callback> callbacks() {
        return Map.of(
            callbackKey(ABOUT_TO_SUBMIT),
            this::notifyRespondentSolicitorOfClaimDismissedPastClaimDetailsNotificationDeadline
        );
    }

    @Override
    public String camundaActivityId() {
        return TASK_ID;
    }

    @Override
    public List<CaseEvent> handledEvents() {
        return EVENTS;
    }

    private CallbackResponse notifyRespondentSolicitorOfClaimDismissedPastClaimDetailsNotificationDeadline(
        CallbackParams callbackParams) {
        CaseData caseData = callbackParams.getCaseData();

        notificationService.sendMail(
            notificationsProperties.getRespondentSolicitorEmail(),
            notificationsProperties.getRespondentSolicitorClaimDismissedPastClaimDetailsNotificationDeadline(),
            addProperties(caseData),
            String.format(REFERENCE_TEMPLATE, caseData.getLegacyCaseReference())
        );

        return AboutToStartOrSubmitCallbackResponse.builder().build();
    }

    @Override
    public Map<String, String> addProperties(CaseData caseData) {
        return Map.of(
            CLAIM_REFERENCE_NUMBER, caseData.getLegacyCaseReference()
        );
    }
}
