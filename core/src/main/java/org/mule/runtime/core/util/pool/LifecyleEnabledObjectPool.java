/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.core.util.pool;

import org.mule.runtime.api.lifecycle.Startable;
import org.mule.runtime.api.lifecycle.Stoppable;

/**
 * An ObjectPool that allows Start and Stop life-cycle to be propagated pooled object.
 */
public interface LifecyleEnabledObjectPool extends ObjectPool, Startable, Stoppable {

}
