/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.runtime.core.policy;

import static java.util.Collections.unmodifiableMap;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.mule.runtime.api.util.Preconditions.checkArgument;

import java.util.Map;

/**
 * Parametrizes a policy template
 * <p/>
 * A policy template is a Mule artifact consistent of a context with dependencies deployed inside a Mule application.
 *
 * @since 4.0
 */
public class PolicyParametrization {

  private final String id;
  private final PolicyPointcut pointcut;
  private final Map<String, Object> parameters;
  private final int priority;

  /**
   * Creates a new parametrization
   * 
   * @param id parametrization identifier. Non empty.
   * @param pointcut used to determine if the policy must be applied on a given request. Non null
   * @param priority indicates how this policy must be prioritized against other applied policies. A policy with a given priority
   *        has to be applied before polices with smaller priority and after policies with bigger priority. Must be positive
   * @param parameters parameters for the policy template on which the parametrization is based on. Non null.
   */
  public PolicyParametrization(String id, PolicyPointcut pointcut, int priority, Map<String, Object> parameters) {
    checkArgument(!isEmpty(id), "id cannot be null");
    checkArgument(pointcut != null, "pointcut cannot be null");
    checkArgument(parameters != null, "parameters cannot be null");
    checkArgument(priority > 0, "priority must be positive");

    this.id = id;
    this.pointcut = pointcut;
    this.parameters = unmodifiableMap(parameters);
    this.priority = priority;
  }

  /**
   * @return parametrization identifier
   */
  public String getId() {
    return id;
  }

  /**
   * @return pointcut to evaluate whether the policy must be applied or not.
   */
  public PolicyPointcut getPointcut() {
    return pointcut;
  }

  /**
   * @return priority of the policy parametrization
   */
  public int getPriority() {
    return priority;
  }

  /**
   * @return parameters to configure the policy template
   */
  public Map<String, Object> getParameters() {
    return parameters;
  }
}
