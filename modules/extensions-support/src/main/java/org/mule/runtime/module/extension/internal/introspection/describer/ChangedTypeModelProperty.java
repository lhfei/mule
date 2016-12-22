/*
 * Copyright (c) MuleSoft, Inc.  All rights reserved.  http://www.mulesoft.com
 * The software in this package is published under the terms of the CPAL v1.0
 * license, a copy of which has been included with this distribution in the
 * LICENSE.txt file.
 */
package org.mule.runtime.module.extension.internal.introspection.describer;

import static org.mule.runtime.api.util.Preconditions.checkArgument;
import org.mule.runtime.api.meta.model.ModelProperty;

/**
 * {@link ModelProperty} that indicates that the original type was changed to a new one.
 *
 * @since 4.0
 */
public class ChangedTypeModelProperty implements ModelProperty {

  private Class originalClass;
  private Class newClass;

  public ChangedTypeModelProperty(Class originalClass, Class newClass) {
    checkArgument(originalClass != null, "The 'originalClass' parameter can not be null");
    checkArgument(newClass != null, "The 'newClass' parameter can not be null");
    this.originalClass = originalClass;
    this.newClass = newClass;
  }

  @Override
  public String getName() {
    return "changingType";
  }

  @Override
  public boolean isExternalizable() {
    return false;
  }

  /**
   * @return The original {@link Class}
   */
  public Class getOriginalClass() {
    return originalClass;
  }

  /**
   * @return The new {@link Class}
   */
  public Class getNewClass() {
    return newClass;
  }
}
