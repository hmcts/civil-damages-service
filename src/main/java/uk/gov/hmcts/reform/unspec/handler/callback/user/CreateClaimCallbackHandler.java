package uk.gov.hmcts.reform.unspec.handler.callback.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.SubmittedCallbackResponse;
import uk.gov.hmcts.reform.ccd.model.OrganisationPolicy;
import uk.gov.hmcts.reform.idam.client.IdamClient;
import uk.gov.hmcts.reform.idam.client.models.UserDetails;
import uk.gov.hmcts.reform.prd.model.Organisation;
import uk.gov.hmcts.reform.unspec.callback.Callback;
import uk.gov.hmcts.reform.unspec.callback.CallbackHandler;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.callback.CaseEvent;
import uk.gov.hmcts.reform.unspec.config.ClaimIssueConfiguration;
import uk.gov.hmcts.reform.unspec.enums.YesOrNo;
import uk.gov.hmcts.reform.unspec.launchdarkly.FeatureToggleService;
import uk.gov.hmcts.reform.unspec.launchdarkly.OnBoardingOrganisationControlService;
import uk.gov.hmcts.reform.unspec.model.BusinessProcess;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.CorrectEmail;
import uk.gov.hmcts.reform.unspec.model.IdamUserDetails;
import uk.gov.hmcts.reform.unspec.model.Party;
import uk.gov.hmcts.reform.unspec.model.PaymentDetails;
import uk.gov.hmcts.reform.unspec.model.SolicitorReferences;
import uk.gov.hmcts.reform.unspec.model.common.DynamicList;
import uk.gov.hmcts.reform.unspec.repositories.ReferenceNumberRepository;
import uk.gov.hmcts.reform.unspec.service.FeesService;
import uk.gov.hmcts.reform.unspec.service.OrganisationService;
import uk.gov.hmcts.reform.unspec.service.Time;
import uk.gov.hmcts.reform.unspec.validation.DateOfBirthValidator;
import uk.gov.hmcts.reform.unspec.validation.OrgPolicyValidator;
import uk.gov.hmcts.reform.unspec.validation.interfaces.ParticularsOfClaimValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Optional.ofNullable;
import static uk.gov.hmcts.reform.unspec.callback.CallbackParams.Params.BEARER_TOKEN;
import static uk.gov.hmcts.reform.unspec.callback.CallbackType.ABOUT_TO_START;
import static uk.gov.hmcts.reform.unspec.callback.CallbackType.ABOUT_TO_SUBMIT;
import static uk.gov.hmcts.reform.unspec.callback.CallbackType.MID;
import static uk.gov.hmcts.reform.unspec.callback.CallbackType.SUBMITTED;
import static uk.gov.hmcts.reform.unspec.callback.CaseEvent.CREATE_CLAIM;
import static uk.gov.hmcts.reform.unspec.enums.AllocatedTrack.getAllocatedTrack;
import static uk.gov.hmcts.reform.unspec.enums.YesOrNo.NO;
import static uk.gov.hmcts.reform.unspec.enums.YesOrNo.YES;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.DATE_TIME_AT;
import static uk.gov.hmcts.reform.unspec.helpers.DateFormatHelper.formatLocalDateTime;

@Service
@RequiredArgsConstructor
public class CreateClaimCallbackHandler extends CallbackHandler implements ParticularsOfClaimValidator {

    private static final List<CaseEvent> EVENTS = Collections.singletonList(CREATE_CLAIM);
    public static final String CONFIRMATION_SUMMARY = "<br/>[Download the sealed claim form](%s)"
        + "\n\n You have until DATE to notify the defendant of the claim and claim details.";

    public static final String LIP_CONFIRMATION_BODY = "<br />To continue your claim by post you need to:"
        + "%n* [Download the sealed claim form](%s)"
        + "%n* Send the claim form, <a href=\"%s\" target=\"_blank\">a response pack</a> (PDF, 266 KB) "
        + "and any supporting documents to the defendant by %s"
        + "%n%nOnce you have served the claim send the Certificate of Service and any supporting documents to the "
        + "County Court Claims Centre.";

    public static final String UNREGISTERED_ORG_CONFIRMATION_BODY = "<br />\n\n### What you need to do\n\n"
        + "\n* Serve the claim on the defendant by Date1."
        + "\n* File the certificate of service with CCMC by Date2.";

    private final ClaimIssueConfiguration claimIssueConfiguration;
    private final ReferenceNumberRepository referenceNumberRepository;
    private final DateOfBirthValidator dateOfBirthValidator;
    private final FeesService feesService;
    private final OrganisationService organisationService;
    private final IdamClient idamClient;
    private final OrgPolicyValidator orgPolicyValidator;
    private final OnBoardingOrganisationControlService onboardingOrganisationControlService;
    private final ObjectMapper objectMapper;
    private final Time time;
    private final FeatureToggleService featureToggleService;

    @Override
    protected Map<String, Callback> callbacks() {
        return Map.of(
            callbackKey(ABOUT_TO_START), this::emptyCallbackResponse,
            callbackKey(MID, "eligibilityCheck"), this::eligibilityCheck,
            callbackKey(MID, "applicant"), this::validateDateOfBirth,
            callbackKey(MID, "fee"), this::calculateFee,
            callbackKey(MID, "idam-email"), this::getIdamEmail,
            callbackKey(MID, "particulars-of-claim"), this::validateParticularsOfClaim,
            callbackKey(MID, "appOrgPolicy"), this::validateApplicantSolicitorOrgPolicy,
            callbackKey(MID, "repOrgPolicy"), this::validateRespondentSolicitorOrgPolicy,
            callbackKey(ABOUT_TO_SUBMIT), this::submitClaim,
            callbackKey(SUBMITTED), this::buildConfirmation
        );
    }

    @Override
    public List<CaseEvent> handledEvents() {
        return EVENTS;
    }

    private CallbackResponse eligibilityCheck(CallbackParams callbackParams) {
        String userBearerToken = callbackParams.getParams().get(BEARER_TOKEN).toString();
        List<String> errors = onboardingOrganisationControlService.validateOrganisation(userBearerToken);
        return AboutToStartOrSubmitCallbackResponse.builder()
            .errors(errors)
            .build();
    }

    private CallbackResponse validateDateOfBirth(CallbackParams callbackParams) {
        Party applicant = callbackParams.getCaseData().getApplicant1();
        List<String> errors = dateOfBirthValidator.validate(applicant);

        return AboutToStartOrSubmitCallbackResponse.builder()
            .errors(errors)
            .build();
    }

    private CallbackResponse validateApplicantSolicitorOrgPolicy(CallbackParams callbackParams) {
        CaseData caseData = callbackParams.getCaseData();
        OrganisationPolicy applicant1OrganisationPolicy = caseData.getApplicant1OrganisationPolicy();
        List<String> errors = orgPolicyValidator.validate(applicant1OrganisationPolicy, YES);

        return AboutToStartOrSubmitCallbackResponse.builder()
            .errors(errors)
            .build();
    }

    private CallbackResponse validateRespondentSolicitorOrgPolicy(CallbackParams callbackParams) {
        CaseData caseData = callbackParams.getCaseData();
        OrganisationPolicy respondent1OrganisationPolicy = caseData.getRespondent1OrganisationPolicy();
        YesOrNo respondent1OrgRegistered = caseData.getRespondent1OrgRegistered();
        List<String> errors = orgPolicyValidator.validate(respondent1OrganisationPolicy, respondent1OrgRegistered);

        return AboutToStartOrSubmitCallbackResponse.builder()
            .errors(errors)
            .build();
    }

    private CallbackResponse calculateFee(CallbackParams callbackParams) {
        CaseData caseData = callbackParams.getCaseData();
        Optional<SolicitorReferences> references = ofNullable(caseData.getSolicitorReferences());
        String reference = references.map(SolicitorReferences::getApplicantSolicitor1Reference).orElse("");
        CaseData.CaseDataBuilder caseDataBuilder = caseData.toBuilder();

        if (featureToggleService.isFeatureEnabled("payment-reference")) {
            Optional<PaymentDetails> paymentDetails = ofNullable(caseData.getClaimIssuedPaymentDetails());
            String customerReference = paymentDetails.map(PaymentDetails::getCustomerReference).orElse(reference);
            PaymentDetails updatedDetails = PaymentDetails.builder().customerReference(customerReference).build();
            caseDataBuilder.claimIssuedPaymentDetails(updatedDetails);
        } else {
            String customerReference = ofNullable(caseData.getPaymentReference()).orElse(reference);
            caseDataBuilder.paymentReference(customerReference);
        }

        List<String> pbaNumbers = getPbaAccounts(callbackParams.getParams().get(BEARER_TOKEN).toString());

        caseDataBuilder.claimFee(feesService.getFeeDataByClaimValue(caseData.getClaimValue()))
            .applicantSolicitor1PbaAccounts(DynamicList.fromList(pbaNumbers))
            .applicantSolicitor1PbaAccountsIsEmpty(pbaNumbers.isEmpty() ? YES : NO);

        return AboutToStartOrSubmitCallbackResponse.builder()
            .data(caseDataBuilder.build().toMap(objectMapper))
            .build();
    }

    private CallbackResponse getIdamEmail(CallbackParams callbackParams) {
        UserDetails userDetails = idamClient.getUserDetails(callbackParams.getParams().get(BEARER_TOKEN).toString());

        CaseData.CaseDataBuilder caseDataBuilder = callbackParams.getCaseData().toBuilder()
            .applicantSolicitor1CheckEmail(CorrectEmail.builder().email(userDetails.getEmail()).build())
            .applicantSolicitor1UserDetails(IdamUserDetails.builder().build());

        return AboutToStartOrSubmitCallbackResponse.builder()
            .data(caseDataBuilder.build().toMap(objectMapper))
            .build();
    }

    private List<String> getPbaAccounts(String authToken) {
        return organisationService.findOrganisation(authToken)
            .map(Organisation::getPaymentAccount)
            .orElse(emptyList());
    }

    private CallbackResponse submitClaim(CallbackParams callbackParams) {
        CaseData caseData = callbackParams.getCaseData();
        // second idam call is workaround for null pointer when hiding field in getIdamEmail callback
        UserDetails userDetails = idamClient.getUserDetails(callbackParams.getParams().get(BEARER_TOKEN).toString());
        IdamUserDetails.IdamUserDetailsBuilder idam = IdamUserDetails.builder().id(userDetails.getId());
        CorrectEmail applicantSolicitor1CheckEmail = caseData.getApplicantSolicitor1CheckEmail();
        CaseData.CaseDataBuilder dataBuilder = caseData.toBuilder();

        if (applicantSolicitor1CheckEmail.isCorrect()) {
            dataBuilder.applicantSolicitor1UserDetails(idam.email(applicantSolicitor1CheckEmail.getEmail()).build());
        } else {
            IdamUserDetails applicantSolicitor1UserDetails = caseData.getApplicantSolicitor1UserDetails();
            dataBuilder.applicantSolicitor1UserDetails(idam.email(applicantSolicitor1UserDetails.getEmail()).build());
        }

        dataBuilder.legacyCaseReference(referenceNumberRepository.getReferenceNumber());
        dataBuilder.submittedDate(time.now());
        dataBuilder.allocatedTrack(getAllocatedTrack(caseData.getClaimValue().toPounds(), caseData.getClaimType()));
        dataBuilder.businessProcess(BusinessProcess.ready(CREATE_CLAIM));

        //set check email field to null for GDPR
        dataBuilder.applicantSolicitor1CheckEmail(CorrectEmail.builder().build());

        return AboutToStartOrSubmitCallbackResponse.builder()
            .data(dataBuilder.build().toMap(objectMapper))
            .build();
    }

    private SubmittedCallbackResponse buildConfirmation(CallbackParams callbackParams) {
        CaseData caseData = callbackParams.getCaseData();
        String claimNumber = caseData.getLegacyCaseReference();

        if (caseData.getRespondent1OrgRegistered() == NO) {
            return SubmittedCallbackResponse.builder()
                .confirmationHeader(format("# Your claim will now progress offline: %s", claimNumber))
                .confirmationBody(UNREGISTERED_ORG_CONFIRMATION_BODY)
                .build();
        }

        return SubmittedCallbackResponse.builder()
            .confirmationHeader(format("# Your claim has been issued%n## Claim number: %s", claimNumber))
            .confirmationBody(getBody(caseData.getRespondent1Represented(), caseData.getCcdCaseReference()))
            .build();
    }

    private String getBody(YesOrNo respondentRepresented, Long caseReference) {
        LocalDateTime serviceDeadline = LocalDate.now().plusDays(112).atTime(23, 59);
        String formattedServiceDeadline = formatLocalDateTime(serviceDeadline, DATE_TIME_AT);

        return format(
            respondentRepresented == YesOrNo.NO ? LIP_CONFIRMATION_BODY : CONFIRMATION_SUMMARY,
            format("/cases/case-details/%s#CaseDocuments", caseReference),
            claimIssueConfiguration.getResponsePackLink(),
            formattedServiceDeadline
        );
    }
}
