/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.file.common.api.exceptions;

import org.mule.runtime.extension.api.error.ErrorTypeDefinition;
import org.mule.runtime.extension.api.error.HasErrorType;

/**
 * @since 4.0
 */
public final class LockConcurrencyException extends RuntimeException implements HasErrorType {

  public LockConcurrencyException(String message) {
    super(message);
  }

  @Override
  public ErrorTypeDefinition getType() {
    return FileErrors.CONCURRENCY;
  }
}
