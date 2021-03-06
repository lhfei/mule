/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.compatibility.core.api.transport;

import org.mule.compatibility.core.config.i18n.TransportCoreMessages;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.api.i18n.I18nMessage;

/**
 * <code>ConnectorException</code> Is thrown in the context of a Connector, usually some sort of transport level error where the
 * connection has failed. This exception maintains a reference to the connector.
 * 
 * @see Connector
 * @deprecated Transport infrastructure is deprecated.
 */
@Deprecated
public class ConnectorException extends MuleException {

  /**
   * Serial version
   */
  private static final long serialVersionUID = 4729481487016346035L;

  /**
   * The connector relevant to this exception
   */
  private transient Connector connector;

  /**
   * @param message the exception message
   * @param connector where the exception occurred or is being thrown
   */
  public ConnectorException(I18nMessage message, Connector connector) {
    super(generateMessage(message, connector));
    this.connector = connector;
  }

  /**
   * @param message the exception message
   * @param connector where the exception occurred or is being thrown
   * @param cause the exception that cause this exception to be thrown
   */
  public ConnectorException(I18nMessage message, Connector connector, Throwable cause) {
    super(generateMessage(message, connector), cause);
    this.connector = connector;
  }

  private static I18nMessage generateMessage(I18nMessage message, Connector connector) {
    I18nMessage m = TransportCoreMessages.connectorCausedError(connector);
    if (message != null) {
      message.setNextMessage(m);
    }
    return message;
  }

  public Connector getConnector() {
    return connector;
  }
}
