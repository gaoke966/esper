/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package com.espertech.esper.common.internal.event.core;

import com.espertech.esper.common.internal.bytecodemodel.base.CodegenClassScope;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMethodScope;
import com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpression;

/**
 * Factory for creating an event bean instance by writing property values to an underlying object.
 */
public interface EventBeanManufacturerForge {
    EventBeanManufacturer getManufacturer(EventBeanTypedEventFactory eventBeanTypedEventFactory) throws EventBeanManufactureException;

    CodegenExpression make(CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope);
}
