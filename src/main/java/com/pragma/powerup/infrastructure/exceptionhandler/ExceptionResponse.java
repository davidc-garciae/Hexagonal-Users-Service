package com.pragma.powerup.infrastructure.exceptionhandler;

import lombok.Getter;

@Getter
public enum ExceptionResponse {
  NO_DATA_FOUND();

  private final String message;

  ExceptionResponse() {
    this.message = "No data found for the requested petition";
  }

}
