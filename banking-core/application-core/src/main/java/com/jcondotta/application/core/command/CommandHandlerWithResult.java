package com.jcondotta.application.core.command;

@FunctionalInterface
public interface CommandHandlerWithResult<C, R> {

  R handle(C command);

}