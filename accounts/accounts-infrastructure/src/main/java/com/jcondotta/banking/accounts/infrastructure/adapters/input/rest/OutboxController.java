package com.jcondotta.banking.accounts.infrastructure.adapters.input.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/outbox")
@RequiredArgsConstructor
public class OutboxController {

    private final OutboxPollerService pollerService;

    @PostMapping("/process")
    public String process(@RequestParam(defaultValue = "10") int limit) {

        pollerService.processPendingEvents(limit);

        return "Outbox processing triggered. Limit=" + limit;
    }
}