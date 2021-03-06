/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.test.integration.message;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.mule.functional.extensions.CompatibilityFunctionalTestCase;
import org.mule.runtime.core.api.message.InternalMessage;

import org.junit.Ignore;
import org.junit.Test;

@Ignore("MULE-9637")
public class MessagePropertySessionScopesTestCase extends CompatibilityFunctionalTestCase {

  @Override
  protected String getConfigFile() {
    return "org/mule/test/integration/messaging/message-property-session-scopes-config-flow.xml";
  }

  @Test
  public void testSessionProperty() throws Exception {

    InternalMessage response = flowRunner("InService1").withPayload("Hello World").run().getMessage();
    assertNotNull(response);
    String payload = getPayloadAsString(response);
    assertNotNull(payload);
    assertEquals("java.util.Date", payload);
  }

  @Test
  public void testInvocationProperty() throws Exception {
    InternalMessage response = flowRunner("InService2").withPayload("Hello World").run().getMessage();
    // scope = "invocation" should not propagate the property on to the next service
    assertThat(response.getPayload().getValue(), is(nullValue()));
  }
}
