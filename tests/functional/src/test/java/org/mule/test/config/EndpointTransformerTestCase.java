/*
 * $Id$
 * --------------------------------------------------------------------------------------
 * Copyright (c) MuleSource, Inc.  All rights reserved.  http://www.mulesource.com
 *
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.test.config;

import org.mule.tck.AbstractMuleTestCase;
import org.mule.transformers.NoActionTransformer;
import org.mule.umo.UMOException;
import org.mule.umo.endpoint.UMOImmutableEndpoint;

public class EndpointTransformerTestCase extends AbstractMuleTestCase
{

    public void testTransformerProperty() throws UMOException
    {
        managementContext.getRegistry().registerTransformer(new NoActionTransformer(), managementContext);
        UMOImmutableEndpoint endpoint = managementContext.getRegistry()
            .lookupOutboundEndpoint("test:///tmp?transformers=NoActionTransformer", managementContext);
        assertEquals("NoActionTransformer", endpoint.getTransformer().getName());
    }

    public void testResponseTransformerProperty() throws UMOException
    {
        managementContext.getRegistry().registerTransformer(new NoActionTransformer(), managementContext);
        UMOImmutableEndpoint endpoint = managementContext.getRegistry()
            .lookupOutboundEndpoint("test:///tmp?responseTransformers=NoActionTransformer", managementContext);
        assertEquals("NoActionTransformer", endpoint.getResponseTransformer().getName());
    }
}
