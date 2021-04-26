package uk.gov.hmcts.reform.unspec.validation.interfaces;

import uk.gov.hmcts.reform.ccd.client.model.AboutToStartOrSubmitCallbackResponse;
import uk.gov.hmcts.reform.ccd.client.model.CallbackResponse;
import uk.gov.hmcts.reform.unspec.callback.CallbackParams;
import uk.gov.hmcts.reform.unspec.model.ServedDocumentFiles;

import static java.util.Optional.ofNullable;

public interface ParticularsOfClaimValidator {

    default CallbackResponse validateParticularsOfClaim(CallbackParams callbackParams, String eventName) {
        ServedDocumentFiles servedDocumentFiles = ofNullable(callbackParams.getCaseData().getServedDocumentFiles())
            .orElse(ServedDocumentFiles.builder().build());

        return AboutToStartOrSubmitCallbackResponse.builder()
                .errors(servedDocumentFiles.getErrors(eventName))
                .build();
    }

    default CallbackResponse validateParticularsOfClaimAddOrAmendDocuments(CallbackParams callbackParams) {
        ServedDocumentFiles servedDocumentFiles = ofNullable(callbackParams.getCaseData().getServedDocumentFiles())
            .orElse(ServedDocumentFiles.builder().build());

        return AboutToStartOrSubmitCallbackResponse.builder()
            .errors(servedDocumentFiles.getErrorsAddOrAmendDocuments())
            .build();
    }
}
