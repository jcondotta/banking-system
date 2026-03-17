package com.jcondotta.application.core.command;

@FunctionalInterface
public interface CommandHandler<C> {

  void handle(C command);
}