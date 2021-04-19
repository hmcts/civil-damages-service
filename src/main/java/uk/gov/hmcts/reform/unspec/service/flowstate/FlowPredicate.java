package uk.gov.hmcts.reform.unspec.service.flowstate;

import uk.gov.hmcts.reform.unspec.model.CaseData;

import java.time.LocalDateTime;
import java.util.function.Predicate;

import static uk.gov.hmcts.reform.unspec.enums.CaseState.CASE_DISMISSED;
import static uk.gov.hmcts.reform.unspec.enums.CaseState.PROCEEDS_IN_HERITAGE_SYSTEM;
import static uk.gov.hmcts.reform.unspec.enums.PaymentStatus.FAILED;
import static uk.gov.hmcts.reform.unspec.enums.RespondentResponseType.COUNTER_CLAIM;
import static uk.gov.hmcts.reform.unspec.enums.RespondentResponseType.FULL_ADMISSION;
import static uk.gov.hmcts.reform.unspec.enums.RespondentResponseType.FULL_DEFENCE;
import static uk.gov.hmcts.reform.unspec.enums.RespondentResponseType.PART_ADMISSION;
import static uk.gov.hmcts.reform.unspec.enums.YesOrNo.NO;
import static uk.gov.hmcts.reform.unspec.enums.YesOrNo.YES;

public class FlowPredicate {

    public static final Predicate<CaseData> claimSubmitted = caseData ->
        caseData.getSubmittedDate() != null;

    public static final Predicate<CaseData> respondent1NotRepresented = caseData ->
        caseData.getIssueDate() != null && caseData.getRespondent1Represented() == NO;

    public static final Predicate<CaseData> respondent1OrgNotRegistered = caseData ->
        caseData.getIssueDate() != null && caseData.getRespondent1OrgRegistered() == NO;

    public static final Predicate<CaseData> paymentFailed = caseData ->
        caseData.getPaymentDetails() != null && caseData.getPaymentDetails().getStatus() == FAILED;

    public static final Predicate<CaseData> paymentSuccessful = caseData ->
        caseData.getPaymentSuccessfulDate() != null;

    public static final Predicate<CaseData> pendingClaimIssued = caseData ->
        caseData.getIssueDate() != null
            && caseData.getRespondent1Represented() == YES
            && caseData.getRespondent1OrgRegistered() == YES;

    public static final Predicate<CaseData> claimNotified = caseData ->
        caseData.getClaimNotificationDate() != null;

    public static final Predicate<CaseData> claimDetailsNotified = caseData ->
        caseData.getClaimDetailsNotificationDate() != null;

    public static final Predicate<CaseData> fullDefence = caseData ->
        caseData.getRespondent1ResponseDate() != null && caseData.getRespondent1ClaimResponseType() == FULL_DEFENCE;

    public static final Predicate<CaseData> fullAdmission = caseData ->
        caseData.getApplicant1ResponseDate() != null
            && caseData.getRespondent1ClaimResponseType() == FULL_ADMISSION;

    public static final Predicate<CaseData> partAdmission = caseData ->
        caseData.getApplicant1ResponseDate() != null
            && caseData.getRespondent1ClaimResponseType() == PART_ADMISSION;

    public static final Predicate<CaseData> counterClaim = caseData ->
        caseData.getApplicant1ResponseDate() != null && caseData.getRespondent1ClaimResponseType() == COUNTER_CLAIM;

    public static final Predicate<CaseData> claimDetailsExtension = caseData ->
        caseData.getRespondent1TimeExtensionDate() != null;

    public static final Predicate<CaseData> claimIssued = caseData ->
        caseData.getClaimNotificationDeadline() != null;

    public static final Predicate<CaseData> offlineNotRegistered = caseData ->
        caseData.getRespondent1OrgRegistered() == NO && caseData.getTakenOfflineDate() != null;

    public static final Predicate<CaseData> offlineNotRepresented = caseData ->
        caseData.getRespondent1Represented() == NO && caseData.getTakenOfflineDate() != null;

    public static final Predicate<CaseData> respondentAcknowledgeClaim = caseData ->
        caseData.getRespondent1AcknowledgeNotificationDate() != null;

    public static final Predicate<CaseData> respondentAcknowledgeClaimExtension = caseData ->
        caseData.getRespondent1AcknowledgeNotificationDate() != null
        && caseData.getRespondent1TimeExtensionDate() != null;

    public static final Predicate<CaseData> respondentFullDefence = caseData ->
        caseData.getRespondent1ClaimResponseType() == FULL_DEFENCE
            && caseData.getCcdState() != CASE_DISMISSED;

    public static final Predicate<CaseData> respondentFullAdmission = caseData ->
        caseData.getRespondent1ClaimResponseType() == FULL_ADMISSION
            && caseData.getCcdState() != CASE_DISMISSED;

    public static final Predicate<CaseData> respondentPartAdmission = caseData ->
        caseData.getRespondent1ClaimResponseType() == PART_ADMISSION
            && caseData.getCcdState() != CASE_DISMISSED;

    public static final Predicate<CaseData> respondentCounterClaim = caseData ->
        caseData.getRespondent1ClaimResponseType() == COUNTER_CLAIM
            && caseData.getCcdState() != CASE_DISMISSED;

    public static final Predicate<CaseData> fullDefenceProceed = caseData ->
        caseData.getApplicant1ProceedWithClaim() != null
            && caseData.getApplicant1ProceedWithClaim() == YES
            && caseData.getApplicant1ResponseDate() != null;

    public static final Predicate<CaseData> fullDefenceNotProceed = caseData ->
        caseData.getApplicant1ProceedWithClaim() != null
            && caseData.getApplicant1ProceedWithClaim() == NO
            && caseData.getApplicant1ResponseDate() != null;

    public static final Predicate<CaseData> claimWithdrawn = caseData ->
        caseData.getWithdrawClaim() != null
            && caseData.getCcdState() == CASE_DISMISSED;

    public static final Predicate<CaseData> claimDiscontinued = caseData ->
        caseData.getDiscontinueClaim() != null
            && caseData.getCcdState() == CASE_DISMISSED;

    // update with dateClaimTakenOffline date when exists
    public static final Predicate<CaseData> claimTakenOffline = caseData ->
        caseData.getCcdState() == PROCEEDS_IN_HERITAGE_SYSTEM;

    public static final Predicate<CaseData> takenOfflineByStaff = caseData ->
        caseData.getTakenOfflineByStaffDate() != null;

    public static final Predicate<CaseData> takenOffline = caseData ->
        caseData.getTakenOfflineDate() != null;

    public static final Predicate<CaseData> takenOfflineAfterApplicantResponseDeadline = caseData ->
        caseData.getTakenOfflineDate() != null && caseData.getApplicant1ResponseDeadline().isAfter(LocalDateTime.now());

    public static final Predicate<CaseData> caseDismissed = caseData ->
        caseData.getClaimDismissedDate() != null;

    public static final Predicate<CaseData> caseDismissedAfterClaimDetailsNotificationDeadline = caseData ->
        caseData.getClaimDismissedDate() != null
            && caseData.getClaimDetailsNotificationDeadline().isAfter(LocalDateTime.now());

    public static final Predicate<CaseData> caseDismissedAfterClaimNotificationDeadline = caseData ->
        caseData.getClaimDismissedDate() != null
            && caseData.getClaimNotificationDeadline().isAfter(LocalDateTime.now());

    public static final Predicate<CaseData> caseDismissedAfterClaimDismissedDeadline = caseData ->
        caseData.getClaimDismissedDate() != null
            && caseData.getClaimDismissedDeadline().isAfter(LocalDateTime.now());

    public static final Predicate<CaseData> caseDismissedAfterClaimAcknowledged = caseData ->
        caseData.getClaimDismissedDate() != null && caseData.getRespondent1ClaimResponseIntentionType() != null;

    public static final Predicate<CaseData> applicantOutOfTime = caseData ->
        caseData.getTakenOfflineDate() != null && caseData.getTakenOfflineDate().isAfter(LocalDateTime.now());

    public static final Predicate<CaseData> failToNotifyClaim = caseData ->
        caseData.getClaimDismissedDate() != null
            && caseData.getClaimNotificationDeadline().isBefore(LocalDateTime.now())
            && caseData.getClaimNotificationDate() == null;

    public static final Predicate<CaseData> pastClaimDetailsNotificationDeadline = caseData ->
        caseData.getClaimDetailsNotificationDeadline() != null
            && caseData.getClaimDetailsNotificationDeadline().isBefore(LocalDateTime.now())
            && caseData.getClaimDetailsNotificationDate() == null
            && caseData.getClaimNotificationDate() != null
            && caseData.getClaimDismissedDate() != null;

    private FlowPredicate() {
        //Utility class
    }
}
