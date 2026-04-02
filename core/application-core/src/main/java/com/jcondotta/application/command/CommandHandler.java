package com.jcondotta.application.command;

@FunctionalInterface
public interface CommandHandler<C> {

  void handle(C command);
}