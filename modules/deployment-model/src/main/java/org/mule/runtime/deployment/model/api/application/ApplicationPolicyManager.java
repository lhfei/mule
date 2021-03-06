/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.runtime.deployment.model.api.application;

import org.mule.runtime.core.policy.PolicyParametrization;
import org.mule.runtime.deployment.model.api.policy.PolicyTemplateDescriptor;

/**
 * Manages the policies that must be applied to a given application
 */
public interface ApplicationPolicyManager {

  /**
   * Add a new policy
   *
   * @param policyTemplateDescriptor describes how to create the policy template. Non null
   * @param parametrization parametrization used to instantiate the policy. Non null
   */
  void addPolicy(PolicyTemplateDescriptor policyTemplateDescriptor, PolicyParametrization parametrization);
}
