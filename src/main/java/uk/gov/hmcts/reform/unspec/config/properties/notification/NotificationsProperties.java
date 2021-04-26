package uk.gov.hmcts.reform.unspec.config.properties.notification;

import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

@Validated
@Data
public class NotificationsProperties {

    @NotEmpty
    private String govNotifyApiKey;

    @NotEmpty
    private String respondentSolicitorClaimIssueEmailTemplate;

    @NotEmpty
    private String respondentSolicitorClaimDetailsEmailTemplate;

    @NotEmpty
    private String solicitorResponseToCase;

    @NotEmpty
    private String respondentSolicitorAcknowledgeClaim;

    @NotEmpty
    private String failedPayment;

    @NotEmpty
    private String applicantSolicitorEmail;

    @NotEmpty
    private String respondentSolicitorEmail;

    @NotEmpty
    private String applicantSolicitorClaimDismissedPastDismissedDeadline;

    @NotEmpty
    private String respondentSolicitorClaimDismissedPastDismissedDeadline;

    @NotEmpty
    private String applicantSolicitorClaimDismissedPastClaimNotificationDeadline;

    @NotEmpty
    private String respondentSolicitorClaimDismissedPastClaimNotificationDeadline;

    @NotEmpty
    private String applicantSolicitorClaimDismissedPastClaimDetailsNotificationDeadline;

    @NotEmpty
    private String respondentSolicitorClaimDismissedPastClaimDetailsNotificationDeadline;

    @NotEmpty
    private String claimantSolicitorCaseWillProgressOffline;

    @NotEmpty
    private String claimantSolicitorAgreedExtensionDate;

    @NotEmpty
    private String claimantSolicitorConfirmsToProceed;

    @NotEmpty
    private String claimantSolicitorConfirmsNotToProceed;

    @NotEmpty
    private String claimantSolicitorClaimContinuingOnline;

    @NotEmpty
    private String solicitorCaseTakenOffline;

    @NotEmpty
    private String solicitorLitigationFriendAdded;
}
