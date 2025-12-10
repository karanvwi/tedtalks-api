package com.io.tedtalksapi.command;

public interface Command<R> {
    R execute();
}
