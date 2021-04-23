package uk.gov.hmcts.reform.unspec.service.flowstate;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.sampledata.CaseDataBuilder;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.applicantOutOfTime;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.caseDismissed;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.caseDismissedAfterClaimAcknowledged;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.caseProceedsInCaseman;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.claimDetailsNotified;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.claimDetailsNotifiedTimeExtension;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.claimIssued;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.claimNotified;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.claimSubmitted;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.claimTakenOffline;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.counterClaim;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.counterClaimAfterAcknowledge;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.counterClaimAfterNotifyDetails;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.fullAdmission;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.fullAdmissionAfterAcknowledge;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.fullAdmissionAfterNotifyDetails;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.fullDefence;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.fullDefenceAfterAcknowledge;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.fullDefenceAfterNotifyDetails;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.fullDefenceProceed;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.notificationAcknowledged;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.notificationAcknowledgedTimeExtension;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.partAdmission;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.partAdmissionAfterAcknowledge;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.partAdmissionAfterNotifyDetails;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.pastClaimDetailsNotificationDeadline;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.pastClaimNotificationDeadline;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.paymentFailed;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.paymentSuccessful;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.pendingClaimIssued;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.respondent1NotRepresented;
import static uk.gov.hmcts.reform.unspec.service.flowstate.FlowPredicate.respondent1OrgNotRegistered;

class FlowPredicateTest {

    @Nested
    class ClaimSubmittedPredicate {

        @Test
        void shouldReturnTrue_whenCaseDataAtPendingClaimIssuedState() {
            CaseData caseData = CaseDataBuilder.builder().atStatePendingClaimIssued().build();
            assertTrue(claimSubmitted.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataIsAtDraftState() {
            CaseData caseData = CaseDataBuilder.builder().atStateClaimDraft().build();
            assertFalse(claimSubmitted.test(caseData));
        }
    }

    @Nested
    class ClaimNotifiedPredicate {

        @Test
        void shouldReturnTrue_whenCaseDataAtIssuedState() {
            CaseData caseData = CaseDataBuilder.builder().atStateClaimNotified().build();
            assertTrue(claimNotified.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataIsAtDraftState() {
            CaseData caseData = CaseDataBuilder.builder().atStateClaimDraft().build();
            assertFalse(claimNotified.test(caseData));
        }
    }

    @Nested
    class ClaimDetailsNotifiedPredicate {

        @Test
        void shouldReturnTrue_whenCaseDataAtIssuedState() {
            CaseData caseData = CaseDataBuilder.builder().atStateClaimDetailsNotified().build();
            assertTrue(claimDetailsNotified.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataIsAtAwaitingCaseDetailsNotification() {
            CaseData caseData = CaseDataBuilder.builder().atStateClaimNotified().build();
            assertFalse(claimDetailsNotified.test(caseData));
        }
    }

    @Nested
    class ClaimDetailsNotifiedTimeExtensionPredicate {

        @Test
        void shouldReturnTrue_whenCaseDataAtIssuedState() {
            CaseData caseData = CaseDataBuilder.builder().atStateClaimDetailsNotifiedTimeExtension().build();
            assertTrue(claimDetailsNotifiedTimeExtension.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataIsAtAwaitingCaseDetailsNotification() {
            CaseData caseData = CaseDataBuilder.builder().atStateClaimNotified().build();
            assertFalse(claimDetailsNotifiedTimeExtension.test(caseData));
        }
    }

    @Nested
    class Respondent1NotRepresented {

        @Test
        void shouldReturnTrue_whenRespondentNotRepresented() {
            CaseData caseData = CaseDataBuilder.builder().atStatePendingClaimIssuedUnRepresentedDefendant().build();
            assertTrue(respondent1NotRepresented.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataIsAtAwaitingCaseNotificationState() {
            CaseData caseData = CaseDataBuilder.builder().atStatePendingClaimIssued().build();
            assertFalse(respondent1NotRepresented.test(caseData));
        }
    }

    @Nested
    class Respondent1NotRegistered {

        @Test
        void shouldReturnTrue_whenRespondentNotRegistered() {
            CaseData caseData = CaseDataBuilder.builder().atStatePendingClaimIssuedUnRegisteredDefendant().build();
            assertTrue(respondent1OrgNotRegistered.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataIsAtAwaitingCaseNotificationState() {
            CaseData caseData = CaseDataBuilder.builder().atStatePendingClaimIssued().build();
            assertFalse(respondent1OrgNotRegistered.test(caseData));
        }
    }

    @Nested
    class PaymentFailedPredicate {

        @Test
        void shouldReturnTrue_whenCaseDataAtIssuedState() {
            CaseData caseData = CaseDataBuilder.builder().atStatePaymentFailed().build();
            assertTrue(paymentFailed.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataIsAtDraftState() {
            CaseData caseData = CaseDataBuilder.builder().atStateClaimSubmitted().build();
            assertFalse(paymentFailed.test(caseData));
        }
    }

    @Nested
    class PaymentSuccessfulPredicate {

        @Test
        void shouldReturnTrue_whenCaseDataAtIssuedState() {
            CaseData caseData = CaseDataBuilder.builder().atStatePaymentSuccessful().build();
            assertTrue(paymentSuccessful.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataIsAtDraftState() {
            CaseData caseData = CaseDataBuilder.builder().atStateClaimSubmitted().build();
            assertFalse(paymentSuccessful.test(caseData));
        }
    }

    @Nested
    class PendingClaimIssuedPredicate {

        @Test
        void shouldReturnTrue_whenCaseDataIsAtPendingClaimIssuedState() {
            CaseData caseData = CaseDataBuilder.builder().atStatePendingClaimIssued().build();
            assertTrue(pendingClaimIssued.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataAtDraftState() {
            CaseData caseData = CaseDataBuilder.builder().atStatePaymentSuccessful().build();
            assertFalse(pendingClaimIssued.test(caseData));
        }
    }

    @Nested
    class ClaimIssuedPredicate {

        @Test
        void shouldReturnTrue_whenCaseDataIsAtClaimIssuedState() {
            CaseData caseData = CaseDataBuilder.builder().atStateClaimIssued().build();
            assertTrue(claimIssued.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataAtDraftState() {
            CaseData caseData = CaseDataBuilder.builder().atStatePaymentSuccessful().build();
            assertFalse(claimIssued.test(caseData));
        }
    }

    @Nested
    class NotificationAcknowledgedPredicate {

        @Test
        void shouldReturnTrue_whenCaseDataAtStateClaimAcknowledged() {
            CaseData caseData = CaseDataBuilder.builder().atStateNotificationAcknowledged().build();
            assertTrue(notificationAcknowledged.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataAtStateClaimCreated() {
            CaseData caseData = CaseDataBuilder.builder().atStateClaimDetailsNotified().build();
            assertFalse(notificationAcknowledged.test(caseData));
        }
    }

    @Nested
    class NotificationAcknowledgedTimeExtensionPredicate {

        @Test
        void shouldReturnTrue_whenCaseDataAtStateClaimAcknowledged() {
            CaseData caseData = CaseDataBuilder.builder().atStateNotificationAcknowledgedTimeExtension().build();
            assertTrue(notificationAcknowledgedTimeExtension.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataAtStateClaimCreated() {
            CaseData caseData = CaseDataBuilder.builder().atStateClaimDetailsNotified().build();
            assertFalse(notificationAcknowledgedTimeExtension.test(caseData));
        }
    }

    @Nested
    class RespondentFullDefencePredicate {

        @Test
        void shouldReturnTrue_whenCaseDataAtStateFullDefence() {
            CaseData caseData = CaseDataBuilder.builder().atStateRespondentFullDefence().build();
            assertTrue(fullDefence.test(caseData));
        }

        @Test
        void shouldReturnTrue_whenCaseDataAtStateFullDefenceAfterNotifyDetails() {
            CaseData caseData = CaseDataBuilder.builder().atStateRespondentFullDefenceAfterNotifyDetails().build();
            assertTrue(fullDefenceAfterNotifyDetails.test(caseData));
        }

        @Test
        void shouldReturnTrue_whenCaseDataAtStateFullDefenceAfterNotificationAcknowledgement() {
            CaseData caseData = CaseDataBuilder.builder().atStateRespondentFullDefence().build();
            assertTrue(fullDefenceAfterAcknowledge.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataAtStateClosed() {
            CaseData caseData = CaseDataBuilder.builder().atStateClaimDiscontinued().build();
            assertFalse(fullDefence.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataAtStateCaseProceedsInCaseman() {
            CaseData caseData = CaseDataBuilder.builder().atStateTakenOfflineByStaff().build();
            assertFalse(fullDefence.test(caseData));
        }
    }

    @Nested
    class RespondentFullAdmissionPredicate {

        @Test
        void shouldReturnTrue_whenCaseDataAtStateFullAdmission() {
            CaseData caseData = CaseDataBuilder.builder().atStateRespondentFullAdmission().build();
            assertTrue(fullAdmission.test(caseData));
        }

        @Test
        void shouldReturnTrue_whenCaseDataAtStateFullAdmissionAfterNotifyDetails() {
            CaseData caseData = CaseDataBuilder.builder().atStateRespondentFullAdmissionAfterNotifyDetails().build();
            assertTrue(fullAdmissionAfterNotifyDetails.test(caseData));
        }

        @Test
        void shouldReturnTrue_whenCaseDataAtStateFullAdmissionAfterAcknowledge() {
            CaseData caseData = CaseDataBuilder.builder().atStateRespondentFullAdmission().build();
            assertTrue(fullAdmissionAfterAcknowledge.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataAtStateCaseProceedsInCaseman() {
            CaseData caseData = CaseDataBuilder.builder().atStateTakenOfflineByStaff().build();
            assertFalse(fullAdmission.test(caseData));
        }
    }

    @Nested
    class RespondentPartAdmissionPredicate {

        @Test
        void shouldReturnTrue_whenCaseDataAtStatePartAdmission() {
            CaseData caseData = CaseDataBuilder.builder().atStateRespondentPartAdmission().build();
            assertTrue(partAdmission.test(caseData));
        }

        @Test
        void shouldReturnTrue_whenCaseDataAtStatePartAdmissionAfterNotifyDetails() {
            CaseData caseData = CaseDataBuilder.builder().atStateRespondentPartAdmissionAfterNotifyDetails().build();
            assertTrue(partAdmissionAfterNotifyDetails.test(caseData));
        }

        @Test
        void shouldReturnTrue_whenCaseDataAtStatePartAdmissionAfterAcknowledge() {
            CaseData caseData = CaseDataBuilder.builder().atStateRespondentPartAdmission().build();
            assertTrue(partAdmissionAfterAcknowledge.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataAtStateCaseProceedsInCaseman() {
            CaseData caseData = CaseDataBuilder.builder().atStateTakenOfflineByStaff().build();
            assertFalse(partAdmission.test(caseData));
        }
    }

    @Nested
    class RespondentCounterClaimPredicate {

        @Test
        void shouldReturnTrue_whenCaseDataAtStatePartAdmission() {
            CaseData caseData = CaseDataBuilder.builder().atStateRespondentCounterClaim().build();
            assertTrue(counterClaim.test(caseData));
        }

        @Test
        void shouldReturnTrue_whenCaseDataAtStatePartAdmissionAfterNotifyDetails() {
            CaseData caseData = CaseDataBuilder.builder().atStateRespondentCounterClaimAfterNotifyDetails().build();
            assertTrue(counterClaimAfterNotifyDetails.test(caseData));
        }

        @Test
        void shouldReturnTrue_whenCaseDataAtStatePartAdmissionAfterAcknowledge() {
            CaseData caseData = CaseDataBuilder.builder().atStateRespondentCounterClaim().build();
            assertTrue(counterClaimAfterAcknowledge.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataAtStateCaseProceedsInCaseman() {
            CaseData caseData = CaseDataBuilder.builder().atStateTakenOfflineByStaff().build();
            assertFalse(counterClaim.test(caseData));
        }
    }

    @Nested
    class ApplicantRespondToDefencePredicate {

        @Test
        void shouldReturnTrue_whenCaseDataAtStateFullDefence() {
            CaseData caseData = CaseDataBuilder.builder().atStateApplicantRespondToDefenceAndProceed().build();
            assertTrue(fullDefenceProceed.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataAtStateClaimAcknowledged() {
            CaseData caseData = CaseDataBuilder.builder().atStateRespondentFullDefence().build();
            assertFalse(fullDefenceProceed.test(caseData));
        }
    }

    @Nested
    class ClaimTakenOffline {

        @Test
        void shouldReturnTrue_whenCaseDataAtStateProceedsOffline() {
            CaseData caseData = CaseDataBuilder.builder().atStateProceedsOfflineAdmissionOrCounterClaim().build();
            assertTrue(claimTakenOffline.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataNotAtStateProceedsOffline() {
            CaseData caseData = CaseDataBuilder.builder().atStateApplicantRespondToDefenceAndProceed().build();
            assertFalse(claimTakenOffline.test(caseData));
        }
    }

    @Nested
    class ClaimProceedsInCaseman {

        @Test
        void shouldReturnTrue_whenCaseDataAtStateCaseProceedsInCaseman() {
            CaseData caseData = CaseDataBuilder.builder().atStateTakenOfflineByStaff().build();
            assertTrue(caseProceedsInCaseman.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataNotAtStateProceedsOffline() {
            CaseData caseData = CaseDataBuilder.builder().atStateApplicantRespondToDefenceAndProceed().build();
            assertFalse(caseProceedsInCaseman.test(caseData));
        }
    }

    @Nested
    class ClaimDismissed {

        @Test
        void shouldReturnTrue_whenCaseDataAtStateClaimDismissed() {
            CaseData caseData = CaseDataBuilder.builder().atStateClaimDismissed().build();
            assertTrue(caseDismissed.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataAtStateApplicantRespondToDefence() {
            CaseData caseData = CaseDataBuilder.builder().atStateApplicantRespondToDefenceAndProceed().build();
            assertFalse(caseDismissed.test(caseData));
        }
    }

    @Nested
    class ClaimDismissedAfterClaimAcknowledged {

        @Test
        void shouldReturnTrue_whenCaseDataAtStateClaimAcknowledgeWithClaimDismissedDate() {
            CaseData caseData = CaseDataBuilder.builder().atStateNotificationAcknowledged()
                .claimDismissedDate(LocalDateTime.now())
                .build();
            assertTrue(caseDismissedAfterClaimAcknowledged.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataAtStateClaimAcknowledge() {
            CaseData caseData = CaseDataBuilder.builder().atStateNotificationAcknowledged().build();
            assertFalse(caseDismissedAfterClaimAcknowledged.test(caseData));
        }
    }

    @Nested
    class ApplicantOutOfTime {

        @Test
        void shouldReturnTrue_whenCaseDataAtStateServiceAcknowledgeWithClaimDismissedDate() {
            CaseData caseData = CaseDataBuilder.builder().atStateTakenOfflinePastApplicantResponseDeadline().build();
            assertTrue(applicantOutOfTime.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataAtStateServiceAcknowledge() {
            CaseData caseData = CaseDataBuilder.builder().atStateRespondentFullDefence().build();
            assertFalse(applicantOutOfTime.test(caseData));
        }
    }

    @Nested
    class FailToNotifyClaim {

        @Test
        void shouldReturnTrue_whenCaseDataClaimDismissedPastClaimNotificationDeadline() {
            CaseData caseData = CaseDataBuilder.builder().atStateClaimDismissedPastClaimNotificationDeadline().build();
            assertTrue(pastClaimNotificationDeadline.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataAtStateAwaitingCaseNotification() {
            CaseData caseData = CaseDataBuilder.builder().atStatePendingClaimIssued().build();
            assertFalse(pastClaimNotificationDeadline.test(caseData));
        }
    }

    @Nested
    class PastClaimDetailsNotificationDeadline {

        @Test
        void shouldReturnTrue_whenCaseDataAtStateClaimDismissedPastClaimDetailsNotificationDeadline() {
            CaseData caseData = CaseDataBuilder.builder()
                .atStateClaimDismissedPastClaimDetailsNotificationDeadline()
                .build();
            assertTrue(pastClaimDetailsNotificationDeadline.test(caseData));
        }

        @Test
        void shouldReturnFalse_whenCaseDataAtStateAwaitingCaseDetailsNotification() {
            CaseData caseData = CaseDataBuilder.builder().atStateClaimNotified().build();
            assertFalse(pastClaimDetailsNotificationDeadline.test(caseData));
        }
    }
}
