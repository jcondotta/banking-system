package com.jcondotta.application.core.events;

@FunctionalInterface
public interface EventSourceProvider {
  String get();
}