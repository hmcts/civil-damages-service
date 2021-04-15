package uk.gov.hmcts.reform.unspec.service.robotics.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.dq.DQ;
import uk.gov.hmcts.reform.unspec.model.dq.RequestedCourt;
import uk.gov.hmcts.reform.unspec.model.robotics.Event;
import uk.gov.hmcts.reform.unspec.model.robotics.EventDetails;
import uk.gov.hmcts.reform.unspec.model.robotics.EventHistory;
import uk.gov.hmcts.reform.unspec.service.flowstate.FlowState;
import uk.gov.hmcts.reform.unspec.service.flowstate.StateFlowEngine;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.String.format;
import static java.time.format.DateTimeFormatter.ISO_DATE;
import static java.util.Optional.ofNullable;
import static uk.gov.hmcts.reform.unspec.enums.YesOrNo.YES;
import static uk.gov.hmcts.reform.unspec.service.robotics.mapper.RoboticsDataMapper.APPLICANT_ID;
import static uk.gov.hmcts.reform.unspec.service.robotics.mapper.RoboticsDataMapper.RESPONDENT_ID;

@Component
@RequiredArgsConstructor
public class EventHistoryMapper {

    private final StateFlowEngine stateFlowEngine;

    public EventHistory buildEvents(CaseData caseData) {
        EventHistory.EventHistoryBuilder builder = EventHistory.builder()
            .directionsQuestionnaireFiled(List.of(Event.builder().build()));

        stateFlowEngine.evaluate(caseData)
            .getStateHistory()
            .forEach(state -> {
                FlowState.Main flowState = (FlowState.Main) FlowState.fromFullName(state.getName());
                switch (flowState) {
                    case PROCEEDS_OFFLINE_UNREPRESENTED_DEFENDANT:
                        buildUnrepresentedDefendant(builder, caseData);
                        break;
                    case PENDING_CLAIM_ISSUED_UNREGISTERED_DEFENDANT:
                        buildUnregisteredDefendant(builder, caseData);
                        break;
                    case AWAITING_CASE_NOTIFICATION:
                        buildClaimantHasNotifiedDefendant(builder, caseData);
                        break;
                    case CLAIM_ACKNOWLEDGED:
                        buildAcknowledgementOfServiceReceived(builder, caseData);
                        break;
                    case EXTENSION_REQUESTED:
                        buildConsentExtensionFilingDefence(builder, caseData);
                        break;
                    case RESPONDENT_FULL_ADMISSION:
                        buildRespondentFullAdmission(builder, caseData);
                        break;
                    case RESPONDENT_PART_ADMISSION:
                        buildRespondentPartAdmission(builder, caseData);
                        break;
                    case RESPONDENT_COUNTER_CLAIM:
                        buildRespondentCounterClaim(builder, caseData);
                        break;
                    case RESPONDENT_FULL_DEFENCE:
                        buildRespondentFullDefence(builder, caseData);
                        break;
                    case FULL_DEFENCE_NOT_PROCEED:
                        buildFullDefenceNotProceed(builder, caseData);
                        break;
                    case FULL_DEFENCE_PROCEED:
                        buildFullDefenceProceed(builder, caseData);
                        break;
                    default:
                        break;
                }
            });

        return builder.build();
    }

    private void buildClaimantHasNotifiedDefendant(EventHistory.EventHistoryBuilder builder, CaseData caseData) {
        builder.miscellaneous(
            Event.builder()
                .eventSequence(1)
                .eventCode("999")
                .dateReceived(caseData.getRespondent1ResponseDate().format(ISO_DATE))
                .eventDetails(EventDetails.builder()
                                  .miscText("Claimant has notified defendant")
                                  .build())
                .build());
    }

    private void buildFullDefenceProceed(EventHistory.EventHistoryBuilder builder, CaseData caseData) {
        builder.replyToDefence(List.of(
            Event.builder()
                .eventSequence(6)
                .eventCode("66")
                .dateReceived(caseData.getApplicant1ResponseDate().format(ISO_DATE))
                .litigiousPartyID(APPLICANT_ID)
                .build())
        ).directionsQuestionnaire(
            Event.builder()
                .eventSequence(7)
                .eventCode("197")
                .dateReceived(caseData.getApplicant1ResponseDate().format(ISO_DATE))
                .litigiousPartyID(APPLICANT_ID)
                .eventDetailsText(prepareEventDetailsText(caseData.getApplicant1DQ()))
                .build()
        ).miscellaneous(Event.builder()
                            .eventSequence(8)
                            .eventCode("999")
                            .dateReceived(caseData.getApplicant1ResponseDate().format(ISO_DATE))
                            .eventDetails(EventDetails.builder()
                                              .miscText("RPA Reason: Applicant proceeds")
                                              .build())
                            .build());
    }

    private String prepareEventDetailsText(DQ dq) {
        return format(
            "preferredCourtCode: %s; stayClaim: %s",
            ofNullable(dq.getRequestedCourt())
                .map(RequestedCourt::getResponseCourtCode)
                .orElse(null),
            dq.getFileDirectionQuestionnaire()
                .getOneMonthStayRequested() == YES
        );
    }

    private void buildFullDefenceNotProceed(EventHistory.EventHistoryBuilder builder, CaseData caseData) {
        builder.miscellaneous(Event.builder()
                                  .eventSequence(6)
                                  .eventCode("999")
                                  .dateReceived(caseData.getApplicant1ResponseDate().format(ISO_DATE))
                                  .eventDetails(EventDetails.builder()
                                                    .miscText("RPA Reason: Claimant intends not to proceed")
                                                    .build())
                                  .build());
    }

    private void buildRespondentFullDefence(EventHistory.EventHistoryBuilder builder, CaseData caseData) {
        builder
            .defenceFiled(
                List.of(
                    Event.builder()
                        .eventSequence(4)
                        .eventCode("50")
                        .dateReceived(caseData.getRespondent1ResponseDate().format(ISO_DATE))
                        .litigiousPartyID(RESPONDENT_ID)
                        .build()
                ))
            .clearDirectionsQuestionnaireFiled()
            .directionsQuestionnaire(
                Event.builder()
                    .eventSequence(5)
                    .eventCode("197")
                    .dateReceived(caseData.getRespondent1ResponseDate().format(ISO_DATE))
                    .litigiousPartyID(RESPONDENT_ID)
                    .eventDetailsText(prepareEventDetailsText(caseData.getRespondent1DQ()))
                    .build()
            );
    }

    private void buildUnrepresentedDefendant(EventHistory.EventHistoryBuilder builder, CaseData caseData) {
        builder.miscellaneous(
            List.of(
                Event.builder()
                    .eventSequence(1)
                    .eventCode("999")
                    .dateReceived(caseData.getSubmittedDate().toLocalDate().format(ISO_DATE))
                    .eventDetails(EventDetails.builder()
                                      .miscText("RPA Reason: Unrepresented defendant.")
                                      .build())
                    .build()
            ));
    }

    private void buildUnregisteredDefendant(EventHistory.EventHistoryBuilder builder, CaseData caseData) {
        builder.miscellaneous(
            List.of(
                Event.builder()
                    .eventSequence(1)
                    .eventCode("999")
                    .dateReceived(caseData.getSubmittedDate().toLocalDate().format(ISO_DATE))
                    .eventDetails(EventDetails.builder()
                                      .miscText("RPA Reason: Unregistered defendant solicitor firm.")
                                      .build())
                    .build()
            ));
    }

    private void buildAcknowledgementOfServiceReceived(EventHistory.EventHistoryBuilder builder, CaseData caseData) {
        LocalDateTime dateAcknowledge = caseData.getRespondent1AcknowledgeNotificationDate();
        if (dateAcknowledge == null) {
            return;
        }
        builder
            .acknowledgementOfServiceReceived(
                List.of(
                    Event.builder()
                        .eventSequence(2)
                        .eventCode("38")
                        .dateReceived(dateAcknowledge.format(ISO_DATE))
                        .litigiousPartyID("002")
                        .eventDetailsText(format(
                            "responseIntention: %s",
                            caseData.getRespondent1ClaimResponseIntentionType().getLabel()
                        ))
                        .build()
                ));
    }

    private void buildRespondentFullAdmission(EventHistory.EventHistoryBuilder builder, CaseData caseData) {
        builder.receiptOfAdmission(List.of(Event.builder()
                                               .eventSequence(4)
                                               .eventCode("40")
                                               .dateReceived(caseData.getRespondent1ResponseDate().format(ISO_DATE))
                                               .litigiousPartyID("002")
                                               .build())
        ).miscellaneous(Event.builder()
                            .eventSequence(5)
                            .eventCode("999")
                            .dateReceived(caseData.getRespondent1ResponseDate().format(ISO_DATE))
                            .eventDetails(EventDetails.builder()
                                              .miscText("RPA Reason: Defendant fully admits.")
                                              .build())
                            .build());
    }

    private void buildRespondentPartAdmission(EventHistory.EventHistoryBuilder builder, CaseData caseData) {
        builder.receiptOfPartAdmission(
            List.of(
                Event.builder()
                    .eventSequence(4)
                    .eventCode("60")
                    .dateReceived(caseData.getRespondent1ResponseDate().format(ISO_DATE))
                    .litigiousPartyID("002")
                    .build()
            )
        ).miscellaneous(Event.builder()
                            .eventSequence(5)
                            .eventCode("999")
                            .dateReceived(caseData.getRespondent1ResponseDate().format(ISO_DATE))
                            .eventDetails(EventDetails.builder()
                                              .miscText("RPA Reason: Defendant partial admission.")
                                              .build())
                            .build());
    }

    private void buildRespondentCounterClaim(EventHistory.EventHistoryBuilder builder, CaseData caseData) {
        builder.defenceAndCounterClaim(
            List.of(
                Event.builder()
                    .eventSequence(4)
                    .eventCode("52")
                    .dateReceived(caseData.getRespondent1ResponseDate().format(ISO_DATE))
                    .litigiousPartyID("002")
                    .build()
            )
        ).miscellaneous(Event.builder()
                            .eventSequence(5)
                            .eventCode("999")
                            .dateReceived(caseData.getRespondent1ResponseDate().format(ISO_DATE))
                            .eventDetails(EventDetails.builder()
                                              .miscText("RPA Reason: Defendant rejects and counter claims.")
                                              .build())
                            .build());
    }

    private void buildConsentExtensionFilingDefence(EventHistory.EventHistoryBuilder builder, CaseData caseData) {
        LocalDateTime dateReceived = caseData.getRespondent1TimeExtensionDate();
        if (dateReceived == null) {
            return;
        }
        builder.consentExtensionFilingDefence(
            List.of(
                Event.builder()
                    .eventSequence(3)
                    .eventCode("45")
                    .dateReceived(dateReceived.format(ISO_DATE))
                    .litigiousPartyID("002")
                    .eventDetailsText(format("agreedExtensionDate: %s", caseData
                        .getRespondentSolicitor1AgreedDeadlineExtension()
                        .format(ISO_DATE)))
                    .build()
            )
        );
    }
}
