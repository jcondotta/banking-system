package com.jcondotta.banking.accounts.infrastructure;

import com.jcondotta.application.core.events.CorrelationIdProvider;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class ThreadLocalCorrelationIdProvider implements CorrelationIdProvider {

  private static final ThreadLocal<UUID> holder = new ThreadLocal<>();

  public static void set(UUID id) {
    holder.set(id);
  }

  public static void clear() {
    holder.remove();
  }

  @Override
  public UUID get() {
    return holder.get();
  }
}