package com.jcondotta.application.command;

@FunctionalInterface
public interface CommandHandlerWithResult<C, R> {

  R handle(C command);

}