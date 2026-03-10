package com.jcondotta.banking.recipients.infrastructure.bankaccount.adapters.input.rest.list_recipients;

import com.jcondotta.banking.recipients.application.bankaccount.query.model.RecipientSummary;

import java.util.List;

public record ListRecipientsResponse(List<RecipientSummary> recipients) {}