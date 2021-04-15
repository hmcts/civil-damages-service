package uk.gov.hmcts.reform.unspec.handler.tasks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.client.spring.annotation.ExternalTaskSubscription;
import org.camunda.bpm.client.task.ExternalTask;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;
import uk.gov.hmcts.reform.unspec.event.TakeCaseOfflineEvent;
import uk.gov.hmcts.reform.unspec.service.search.TakeCaseOfflineSearchService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
@ExternalTaskSubscription(topicName = "TAKE_CASE_OFFLINE")
public class TakeCaseOfflineHandler implements BaseExternalTaskHandler {

    private final TakeCaseOfflineSearchService caseSearchService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void handleTask(ExternalTask externalTask) {
        List<CaseDetails> cases = caseSearchService.getCases();
        log.info("Job '{}' found {} case(s)", externalTask.getTopicName(), cases.size());

        cases.forEach(caseDetails -> applicationEventPublisher.publishEvent(
            new TakeCaseOfflineEvent(caseDetails.getId())));
    }
}
