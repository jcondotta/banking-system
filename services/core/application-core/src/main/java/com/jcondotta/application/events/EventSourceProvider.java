package com.jcondotta.application.events;

@FunctionalInterface
public interface EventSourceProvider {
  String get();
}