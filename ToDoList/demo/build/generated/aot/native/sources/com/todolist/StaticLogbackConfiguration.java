package com.todolist;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.Configurator;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.status.Status;
import java.lang.String;
import java.lang.Throwable;

public class StaticLogbackConfiguration implements Configurator {
  private Context context;

  public Configurator.ExecutionStatus configure(LoggerContext loggerContext) {
    ConsoleAppender stdout = new ConsoleAppender();
    PatternLayoutEncoder encoder = new PatternLayoutEncoder();
    encoder.setPattern("%cyan(%d{HH:mm:ss.SSS}) %gray([%thread]) %highlight(%-5level) %magenta(%logger{36}) - %msg%n");
    encoder.setContext(context);
    encoder.start();
    stdout.setEncoder(encoder);
    stdout.setContext(context);
    stdout.start();
    Logger _rootLogger = loggerContext.getLogger(Logger.ROOT_LOGGER_NAME);
    _rootLogger.setLevel(Level.INFO);
    Logger io_micronaut_context_env = loggerContext.getLogger("io.micronaut.context.env");
    io_micronaut_context_env.setLevel(Level.DEBUG);
    Logger io_micronaut_configuration_jdbc_hikari = loggerContext.getLogger("io.micronaut.configuration.jdbc.hikari");
    io_micronaut_configuration_jdbc_hikari.setLevel(Level.DEBUG);
    Logger com_zaxxer_hikari = loggerContext.getLogger("com.zaxxer.hikari");
    com_zaxxer_hikari.setLevel(Level.DEBUG);
    Logger io_micronaut_data = loggerContext.getLogger("io.micronaut.data");
    io_micronaut_data.setLevel(Level.DEBUG);
    Logger io_micronaut_configuration_hibernate_jpa = loggerContext.getLogger("io.micronaut.configuration.hibernate.jpa");
    io_micronaut_configuration_hibernate_jpa.setLevel(Level.DEBUG);
    Logger org_hibernate = loggerContext.getLogger("org.hibernate");
    org_hibernate.setLevel(Level.INFO);
    Logger org_hibernate_engine_jdbc_env_internal = loggerContext.getLogger("org.hibernate.engine.jdbc.env.internal");
    org_hibernate_engine_jdbc_env_internal.setLevel(Level.DEBUG);
    Logger org_hibernate_sql = loggerContext.getLogger("org.hibernate.SQL");
    org_hibernate_sql.setLevel(Level.DEBUG);
    Logger org_hibernate_orm_jdbc_bind = loggerContext.getLogger("org.hibernate.orm.jdbc.bind");
    org_hibernate_orm_jdbc_bind.setLevel(Level.TRACE);
    Logger org_hibernate_type_descriptor_sql = loggerContext.getLogger("org.hibernate.type.descriptor.sql");
    org_hibernate_type_descriptor_sql.setLevel(Level.TRACE);
    _rootLogger.addAppender(stdout);
    return Configurator.ExecutionStatus.DO_NOT_INVOKE_NEXT_IF_ANY;
  }

  public void setContext(Context context) {
    this.context = context;
  }

  public Context getContext() {
    return context;
  }

  public void addStatus(Status status) {
  }

  public void addInfo(String info) {
  }

  public void addInfo(String info, Throwable ex) {
  }

  public void addWarn(String warn) {
  }

  public void addWarn(String warn, Throwable ex) {
  }

  public void addError(String error) {
  }

  public void addError(String error, Throwable ex) {
  }
}
