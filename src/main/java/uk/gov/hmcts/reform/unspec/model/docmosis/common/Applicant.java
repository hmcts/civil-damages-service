package uk.gov.hmcts.reform.unspec.model.docmosis.common;

import lombok.Builder;
import lombok.Data;
import uk.gov.hmcts.reform.unspec.model.Address;

@Data
@Builder(toBuilder = true)
public class Applicant {

    private final String name;
    private final Address primaryAddress;
    private final String litigationFriendName;
}
