package com.company.scheduler;

import com.company.entity.OutboxEvent;
import com.company.enums.OutboxStatus;
import com.company.rabbit.RabbitPublisher;
import com.company.repository.OutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OutboxPublisherService {

    private final OutboxRepository repository;
    private final RabbitPublisher publisher;

    @Scheduled(fixedDelayString = "${publisher.fixed-delay}")
    public void publish() {
        
        List<OutboxEvent> events =
                repository.findTop100ByStatusOrderByCreatedAtAsc(
                        OutboxStatus.NEW
                );

        for (OutboxEvent event : events) {
            try {
                publisher.publish(event.getPayload());
                event.setStatus(OutboxStatus.PUBLISHED);
                event.setPublishedAt(Instant.now());

            } catch (Exception ex) {
                event.setRetryCount(
                        event.getRetryCount() + 1
                );
            }
        }

        repository.saveAll(events);
    }

}