package uk.gov.hmcts.reform.unspec.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.payments.client.PaymentsClient;
import uk.gov.hmcts.reform.payments.client.models.FeeDto;
import uk.gov.hmcts.reform.payments.client.models.PaymentDto;
import uk.gov.hmcts.reform.payments.client.request.CreditAccountPaymentRequest;
import uk.gov.hmcts.reform.unspec.config.PaymentsConfiguration;
import uk.gov.hmcts.reform.unspec.launchdarkly.FeatureToggleService;
import uk.gov.hmcts.reform.unspec.model.CaseData;

@Service
@RequiredArgsConstructor
public class PaymentsService {

    private final PaymentsClient paymentsClient;
    private final PaymentsConfiguration paymentsConfiguration;
    private final FeatureToggleService featureToggleService;

    public PaymentDto createCreditAccountPayment(CaseData caseData, String authToken) {
        return paymentsClient.createCreditAccountPayment(authToken, buildRequest(caseData));
    }

    private CreditAccountPaymentRequest buildRequest(CaseData caseData) {
        FeeDto claimFee = caseData.getClaimFee().toFeeDto();
        String customerReference;
        if (featureToggleService.isFeatureEnabled("payment-reference")) {
            customerReference = caseData.getClaimIssuedPaymentDetails().getCustomerReference();
        } else {
            customerReference = caseData.getPaymentReference();
        }

        return CreditAccountPaymentRequest.builder()
            .accountNumber(caseData.getApplicantSolicitor1PbaAccounts().getValue().getLabel())
            .amount(claimFee.getCalculatedAmount())
            .caseReference(caseData.getLegacyCaseReference())
            .ccdCaseNumber(caseData.getCcdCaseReference().toString())
            .customerReference(customerReference)
            .description("Claim issue payment")
            .organisationName("Test Organisation Name")
            .service(paymentsConfiguration.getService())
            .siteId(paymentsConfiguration.getSiteId())
            .fees(new FeeDto[]{claimFee})
            .build();
    }
}
