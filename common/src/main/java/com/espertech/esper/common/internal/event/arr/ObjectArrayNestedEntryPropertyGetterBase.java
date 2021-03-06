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
package com.espertech.esper.common.internal.event.arr;

import com.espertech.esper.common.client.EventBean;
import com.espertech.esper.common.client.EventType;
import com.espertech.esper.common.client.PropertyAccessException;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenClassScope;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMethod;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMethodScope;
import com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpression;
import com.espertech.esper.common.internal.event.core.BaseNestableEventUtil;
import com.espertech.esper.common.internal.event.core.EventBeanTypedEventFactory;

import static com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpressionBuilder.*;

public abstract class ObjectArrayNestedEntryPropertyGetterBase implements ObjectArrayEventPropertyGetter {

    protected final int propertyIndex;
    protected final EventType fragmentType;
    protected final EventBeanTypedEventFactory eventBeanTypedEventFactory;

    /**
     * Ctor.
     *
     * @param propertyIndex              the property to look at
     * @param eventBeanTypedEventFactory factory for event beans and event types
     * @param fragmentType               type of the entry returned
     */
    public ObjectArrayNestedEntryPropertyGetterBase(int propertyIndex, EventType fragmentType, EventBeanTypedEventFactory eventBeanTypedEventFactory) {
        this.propertyIndex = propertyIndex;
        this.fragmentType = fragmentType;
        this.eventBeanTypedEventFactory = eventBeanTypedEventFactory;
    }

    public abstract Object handleNestedValue(Object value);

    public abstract boolean handleNestedValueExists(Object value);

    public abstract Object handleNestedValueFragment(Object value);

    public abstract CodegenExpression handleNestedValueCodegen(CodegenExpression refName, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope);

    public abstract CodegenExpression handleNestedValueExistsCodegen(CodegenExpression refName, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope);

    public abstract CodegenExpression handleNestedValueFragmentCodegen(CodegenExpression refName, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope);

    public Object getObjectArray(Object[] array) throws PropertyAccessException {
        Object value = array[propertyIndex];
        if (value == null) {
            return null;
        }
        return handleNestedValue(value);
    }

    public boolean isObjectArrayExistsProperty(Object[] array) {
        Object value = array[propertyIndex];
        if (value == null) {
            return false;
        }
        return handleNestedValueExists(value);
    }

    public Object get(EventBean obj) {
        return getObjectArray(BaseNestableEventUtil.checkedCastUnderlyingObjectArray(obj));
    }

    private CodegenMethod getCodegen(CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return codegenMethodScope.makeChild(Object.class, this.getClass(), codegenClassScope).addParam(Object[].class, "array").getBlock()
                .declareVar(Object.class, "value", arrayAtIndex(ref("array"), constant(propertyIndex)))
                .ifRefNullReturnNull("value")
                .methodReturn(handleNestedValueCodegen(ref("value"), codegenMethodScope, codegenClassScope));
    }

    public boolean isExistsProperty(EventBean eventBean) {
        Object[] array = BaseNestableEventUtil.checkedCastUnderlyingObjectArray(eventBean);
        Object value = array[propertyIndex];
        if (value == null) {
            return false;
        }
        return handleNestedValueExists(value);
    }

    private CodegenMethod isExistsPropertyCodegen(CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return codegenMethodScope.makeChild(boolean.class, this.getClass(), codegenClassScope).addParam(Object[].class, "array").getBlock()
                .declareVar(Object.class, "value", arrayAtIndex(ref("array"), constant(propertyIndex)))
                .ifRefNullReturnFalse("value")
                .methodReturn(handleNestedValueExistsCodegen(ref("value"), codegenMethodScope, codegenClassScope));
    }

    public Object getFragment(EventBean obj) {
        Object[] array = BaseNestableEventUtil.checkedCastUnderlyingObjectArray(obj);
        Object value = array[propertyIndex];
        if (value == null) {
            return null;
        }
        return handleNestedValueFragment(value);
    }

    private CodegenMethod getFragmentCodegen(CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return codegenMethodScope.makeChild(Object.class, this.getClass(), codegenClassScope).addParam(Object[].class, "array").getBlock()
                .declareVar(Object.class, "value", arrayAtIndex(ref("array"), constant(propertyIndex)))
                .ifRefNullReturnFalse("value")
                .methodReturn(handleNestedValueFragmentCodegen(ref("value"), codegenMethodScope, codegenClassScope));
    }

    public CodegenExpression eventBeanGetCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return underlyingGetCodegen(castUnderlying(Object[].class, beanExpression), codegenMethodScope, codegenClassScope);
    }

    public CodegenExpression eventBeanExistsCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return underlyingExistsCodegen(castUnderlying(Object[].class, beanExpression), codegenMethodScope, codegenClassScope);
    }

    public CodegenExpression eventBeanFragmentCodegen(CodegenExpression beanExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return underlyingFragmentCodegen(castUnderlying(Object[].class, beanExpression), codegenMethodScope, codegenClassScope);
    }

    public CodegenExpression underlyingGetCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return localMethod(getCodegen(codegenMethodScope, codegenClassScope), underlyingExpression);
    }

    public CodegenExpression underlyingExistsCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return localMethod(isExistsPropertyCodegen(codegenMethodScope, codegenClassScope), underlyingExpression);
    }

    public CodegenExpression underlyingFragmentCodegen(CodegenExpression underlyingExpression, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return localMethod(getFragmentCodegen(codegenMethodScope, codegenClassScope), underlyingExpression);
    }

}
