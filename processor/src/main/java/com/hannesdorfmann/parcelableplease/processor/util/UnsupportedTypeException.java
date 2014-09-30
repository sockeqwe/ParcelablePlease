package com.hannesdorfmann.parcelableplease.processor.util;

/**
 * Will be thrown if a type should be make as parcelable but is not supported yet
 *
 * @author Hannes Dorfmann
 */
public class UnsupportedTypeException extends RuntimeException {

  public UnsupportedTypeException(String message) {
    super(message);
  }
}
