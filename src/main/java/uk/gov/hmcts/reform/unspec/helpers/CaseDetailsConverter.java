package uk.gov.hmcts.reform.unspec.helpers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.unspec.enums.CaseState;
import uk.gov.hmcts.reform.unspec.model.CaseData;
import uk.gov.hmcts.reform.unspec.model.ServedDocumentFiles;
import uk.gov.hmcts.reform.unspec.model.common.Element;
import uk.gov.hmcts.reform.unspec.model.documents.Document;

import com.fasterxml.jackson.core.type.TypeReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CaseDetailsConverter {

    private final ObjectMapper objectMapper;

    public CaseDetailsConverter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    public CaseData toCaseData(CaseDetails caseDetails) {
        Map<String, Object> data = new HashMap<>(caseDetails.getData());
        data.put("ccdCaseReference", caseDetails.getId());
        if (caseDetails.getState() != null) {
            data.put("ccdState", CaseState.valueOf(caseDetails.getState()));
        }

        backwardsCompatibility(data);
        return objectMapper.convertValue(data, CaseData.class);
    }

    public CaseData toCaseData(Map<String, Object> caseDataMap) {
        return objectMapper.convertValue(caseDataMap, CaseData.class);
    }

    @SuppressWarnings("unchecked")
    private void backwardsCompatibility(Map<String, Object> data) {
        Map<String, Object> servedDocumentFilesMap = (Map<String, Object>) data.get("servedDocumentFiles");
        try {
            objectMapper.convertValue(servedDocumentFilesMap, ServedDocumentFiles.class);
        } catch (IllegalArgumentException e) {
            servedDocumentFilesMap.put(
                "particularsOfClaimDocumentBackwardsCompatibility",
                servedDocumentFilesMap.get("particularsOfClaimDocument")
            );
            servedDocumentFilesMap.remove("particularsOfClaimDocument");
        }
    }
}
