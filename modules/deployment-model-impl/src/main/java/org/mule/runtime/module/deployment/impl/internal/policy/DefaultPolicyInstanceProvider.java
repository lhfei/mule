/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */

package org.mule.runtime.module.deployment.impl.internal.policy;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.mule.runtime.core.config.bootstrap.ArtifactType.APP;
import static org.mule.runtime.module.deployment.impl.internal.artifact.ArtifactContextBuilder.newBuilder;
import org.mule.runtime.api.exception.MuleException;
import org.mule.runtime.core.api.config.MuleProperties;
import org.mule.runtime.core.api.registry.RegistrationException;
import org.mule.runtime.core.policy.Policy;
import org.mule.runtime.core.policy.PolicyInstance;
import org.mule.runtime.core.policy.PolicyParametrization;
import org.mule.runtime.core.policy.PolicyPointcut;
import org.mule.runtime.core.policy.PolicyPointcutParameters;
import org.mule.runtime.deployment.model.api.application.Application;
import org.mule.runtime.deployment.model.api.artifact.ArtifactContext;
import org.mule.runtime.deployment.model.api.policy.PolicyTemplate;
import org.mule.runtime.module.artifact.classloader.ClassLoaderRepository;
import org.mule.runtime.module.deployment.impl.internal.artifact.ArtifactContextBuilder;
import org.mule.runtime.module.service.ServiceRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of {@link PolicyInstanceProvider} that depends on a {@link PolicyTemplate} artifact.
 */
public class DefaultPolicyInstanceProvider implements PolicyInstanceProvider {

  private ArtifactContext policyContext;
  private final Application application;
  private final PolicyTemplate template;
  private final PolicyParametrization parametrization;
  private final ServiceRepository serviceRepository;
  private final ClassLoaderRepository classLoaderRepository;
  private PolicyInstance policyInstance;

  /**
   * Creates a new policy instance
   *
   * @param application application artifact owning the created policy. Non null
   * @param template policy template from which the instance will be created. Non null
   * @param parametrization parameters used to configure the created instance. Non null
   * @param serviceRepository repository of available services. Non null.
   * @param classLoaderRepository contains the registered classloaders that can be used to load serialized classes. Non null.
   */
  public DefaultPolicyInstanceProvider(Application application, PolicyTemplate template,
                                       PolicyParametrization parametrization, ServiceRepository serviceRepository,
                                       ClassLoaderRepository classLoaderRepository) {
    this.application = application;
    this.template = template;
    this.parametrization = parametrization;
    this.serviceRepository = serviceRepository;
    this.classLoaderRepository = classLoaderRepository;
  }

  private void initPolicyContext() {
    if (policyContext == null) {
      ArtifactContextBuilder artifactBuilder =
          newBuilder().setArtifactType(APP)
              .setArtifactName(parametrization.getId())
              .setConfigurationFiles(getResourcePaths(template.getDescriptor().getConfigResourceFiles()))
              .setExecutionClassloader(template.getArtifactClassLoader().getClassLoader())
              .setServiceRepository(serviceRepository)
              .setClassLoaderRepository(classLoaderRepository);

      artifactBuilder.withServiceConfigurator(customizationService -> customizationService
          .overrideDefaultServiceImpl(MuleProperties.OBJECT_POLICY_MANAGER_STATE_HANDLER,
                                      application.getMuleContext().getRegistry()
                                          .lookupObject(MuleProperties.OBJECT_POLICY_MANAGER_STATE_HANDLER)));

      try {
        policyContext = artifactBuilder.build();
        policyContext.getMuleContext().start();
      } catch (MuleException e) {
        throw new IllegalStateException("Cannot create artifact context for the policy instance", e);
      }
    }
  }

  private String[] getResourcePaths(File[] configResourceFiles) {
    List<String> paths = new ArrayList<>();
    for (File configResourceFile : configResourceFiles) {
      paths.add(configResourceFile.getAbsolutePath());
    }

    return paths.toArray(new String[0]);
  }

  @Override
  public List<Policy> findSourceParameterizedPolicies(PolicyPointcutParameters policyPointcutParameters) {

    initPolicyInstance();

    return policyInstance.getSourcePolicyChain()
        .map(operationChain -> asList(new Policy(operationChain, parametrization.getId())))
        .orElse(emptyList());
  }

  private void initPolicyInstance() {

    if (policyInstance == null) {
      synchronized (this) {
        initPolicyContext();

        try {
          policyInstance = policyContext.getMuleContext().getRegistry().lookupObject(
                                                                                     org.mule.runtime.core.policy.DefaultPolicyInstance.class);
        } catch (RegistrationException e) {
          throw new IllegalStateException(String.format("More than one %s found on context", PolicyInstanceProvider.class), e);
        }

        // TODO(pablo.kraan): lifecycle has to be manually applied because of MULE-11242
        try {
          policyInstance.initialise();
          policyInstance.start();
        } catch (Exception e) {
          throw new IllegalStateException("Unable to apply lifecycle to policy instance", e);
        }
      }
    }
  }

  @Override
  public List<Policy> findOperationParameterizedPolicies(
                                                         PolicyPointcutParameters policyPointcutParameters) {
    initPolicyInstance();

    return policyInstance.getOperationPolicyChain()
        .map(operationChain -> asList(new Policy(operationChain, parametrization.getId())))
        .orElse(emptyList());
  }

  @Override
  public PolicyPointcut getPointcut() {
    return parametrization.getPointcut();
  }

  @Override
  public void dispose() {
    if (policyContext != null) {
      try {
        policyContext.getMuleContext().stop();
      } catch (MuleException e) {
        // Ignore, try to dispose it anyway
      }
      policyContext.getMuleContext().dispose();
    }
  }
}
