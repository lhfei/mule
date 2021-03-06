/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.compatibility.transport.jms;


import static org.hamcrest.core.IsCollectionContaining.hasItems;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.mule.functional.extensions.CompatibilityFunctionalTestCase;
import org.mule.runtime.core.api.client.MuleClient;
import org.mule.runtime.core.api.message.InternalMessage;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

/**
 * Tests that JMS message are correctly sent when caching elements
 */
public class JmsCachingTestCase extends CompatibilityFunctionalTestCase {

  public static final String TEST_MESSAGE_1 = "test1";
  public static final String TEST_MESSAGE_2 = "test2";

  @Override
  protected String getConfigFile() {
    return "jms-caching-config.xml";
  }

  @Test
  public void worksWithCaching() throws Exception {
    MuleClient client = muleContext.getClient();

    InternalMessage response = client.send("vm://testInput", TEST_MESSAGE_1, null).getRight();
    assertThat(TEST_MESSAGE_1, equalTo(getPayloadAsString(response)));
    response = client.send("vm://testInput", TEST_MESSAGE_2, null).getRight();
    assertThat(TEST_MESSAGE_2, equalTo(getPayloadAsString(response)));

    Set<String> responses = new HashSet<>();
    response = client.request("vm://testOut", RECEIVE_TIMEOUT).getRight().get();
    responses.add(getPayloadAsString(response));
    response = client.request("vm://testOut", RECEIVE_TIMEOUT).getRight().get();
    responses.add(getPayloadAsString(response));

    assertThat(responses, hasItems(equalTo(TEST_MESSAGE_1), equalTo(TEST_MESSAGE_2)));
  }

}
