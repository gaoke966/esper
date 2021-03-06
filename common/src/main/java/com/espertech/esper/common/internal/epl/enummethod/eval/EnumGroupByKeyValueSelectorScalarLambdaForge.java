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
package com.espertech.esper.common.internal.epl.enummethod.eval;

import com.espertech.esper.common.internal.bytecodemodel.base.CodegenClassScope;
import com.espertech.esper.common.internal.bytecodemodel.base.CodegenMethodScope;
import com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpression;
import com.espertech.esper.common.internal.epl.enummethod.codegen.EnumForgeCodegenParams;
import com.espertech.esper.common.internal.epl.expression.core.ExprForge;
import com.espertech.esper.common.internal.event.arr.ObjectArrayEventType;

public class EnumGroupByKeyValueSelectorScalarLambdaForge extends EnumForgeBase {

    protected final ExprForge secondExpression;
    protected final ObjectArrayEventType resultEventType;

    public EnumGroupByKeyValueSelectorScalarLambdaForge(ExprForge innerExpression, int streamCountIncoming, ExprForge secondExpression, ObjectArrayEventType resultEventType) {
        super(innerExpression, streamCountIncoming);
        this.secondExpression = secondExpression;
        this.resultEventType = resultEventType;
    }

    public EnumEval getEnumEvaluator() {
        return new EnumGroupByKeyValueSelectorScalarLambdaForgeEval(this, innerExpression.getExprEvaluator(), secondExpression.getExprEvaluator());
    }

    public CodegenExpression codegen(EnumForgeCodegenParams premade, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        return EnumGroupByKeyValueSelectorScalarLambdaForgeEval.codegen(this, premade, codegenMethodScope, codegenClassScope);
    }
}
