package uk.gov.hmcts.reform.unspec.service.flowstate;

import static org.springframework.util.StringUtils.hasLength;

public interface FlowState {

    String fullName();

    static FlowState fromFullName(String fullName) {
        if (!hasLength(fullName)) {
            throw new IllegalArgumentException("Invalid full name:" + fullName);
        }
        int lastIndexOfDot = fullName.lastIndexOf('.');
        String flowStateName = fullName.substring(lastIndexOfDot + 1);
        String flowName = fullName.substring(0, lastIndexOfDot);
        if (flowName.equals("MAIN")) {
            return Main.valueOf(flowStateName);
        } else {
            throw new IllegalArgumentException("Invalid flow name:" + flowName);
        }
    }

    enum Main implements FlowState {
        DRAFT,
        CLAIM_SUBMITTED,
        CLAIM_ISSUED_PAYMENT_SUCCESSFUL,
        PENDING_CLAIM_ISSUED,
        PENDING_CLAIM_ISSUED_UNREGISTERED_DEFENDANT,
        PENDING_CLAIM_ISSUED_UNREPRESENTED_DEFENDANT,
        CLAIM_ISSUED_PAYMENT_FAILED,
        CLAIM_ISSUED,
        TAKEN_OFFLINE_UNREPRESENTED_DEFENDANT,
        TAKEN_OFFLINE_UNREGISTERED_DEFENDANT,
        AWAITING_CASE_DETAILS_NOTIFICATION,
        CLAIM_NOTIFIED,
        CLAIM_DETAILS_NOTIFIED,
        CLAIM_DETAILS_NOTIFIED_TIME_EXTENSION,
        AWAITING_CASE_NOTIFICATION,
        AWAITING_RESPONDENT_ACKNOWLEDGEMENT,
        EXTENSION_REQUESTED,
        CLAIM_ACKNOWLEDGED,
        NOTIFICATION_ACKNOWLEDGED,
        NOTIFICATION_ACKNOWLEDGED_TIME_EXTENSION,
        AWAITING_APPLICANT_EXTENSION,
        FULL_DEFENCE,
        PROCEEDS_IN_HERITAGE_SYSTEM,
        FULL_DEFENCE_PROCEED,
        FULL_DEFENCE_NOT_PROCEED,
        FULL_ADMISSION,
        PART_ADMISSION,
        COUNTER_CLAIM,
        TAKEN_OFFLINE_PAST_APPLICANT_RESPONSE_DEADLINE,
        CASE_DISMISSED,
        CASE_DISMISSED_PAST_CLAIM_DISMISSED_DEADLINE,
        RESPONDENT_FULL_DEFENCE,
        RESPONDENT_FULL_ADMISSION,
        RESPONDENT_PART_ADMISSION,
        RESPONDENT_COUNTER_CLAIM,
        CLAIM_WITHDRAWN,
        CLAIM_DISCONTINUED,
        CASE_PROCEEDS_IN_CASEMAN,
        PROCEEDS_OFFLINE_ADMIT_OR_COUNTER_CLAIM,
        CLAIM_DISMISSED_DEFENDANT_OUT_OF_TIME,
        CLAIM_DISMISSED_PAST_CLAIM_NOTIFICATION_DEADLINE,
        CLAIM_DISMISSED_PAST_CLAIM_DETAILS_NOTIFICATION_DEADLINE;

        public static final String FLOW_NAME = "MAIN";

        @Override
        public String fullName() {
            return FLOW_NAME + "." + name();
        }
    }
}
