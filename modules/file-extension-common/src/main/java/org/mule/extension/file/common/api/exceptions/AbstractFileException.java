/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.extension.file.common.api.exceptions;

import org.mule.runtime.extension.api.error.HasErrorType;

abstract class AbstractFileException extends RuntimeException implements HasErrorType {

  protected AbstractFileException(String message) {
    super(message);
  }

  protected AbstractFileException(String message, Throwable cause) {
    super(message, cause);
  }
}
