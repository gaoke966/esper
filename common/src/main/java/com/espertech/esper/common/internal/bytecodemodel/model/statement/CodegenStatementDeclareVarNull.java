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
package com.espertech.esper.common.internal.bytecodemodel.model.statement;

import com.espertech.esper.common.internal.bytecodemodel.model.expression.CodegenExpression;

import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import static com.espertech.esper.common.internal.bytecodemodel.core.CodeGenerationHelper.appendClassName;

public class CodegenStatementDeclareVarNull extends CodegenStatementBase {
    private final Class clazz;
    private final String var;

    public CodegenStatementDeclareVarNull(Class clazz, String var) {
        this.clazz = clazz;
        this.var = var;
    }

    public void renderStatement(StringBuilder builder, Map<Class, String> imports, boolean isInnerClass) {
        appendClassName(builder, clazz, null, imports);
        builder.append(" ").append(var).append("=null");
    }

    public void mergeClasses(Set<Class> classes) {
        classes.add(clazz);
    }

    public void traverseExpressions(Consumer<CodegenExpression> consumer) {
    }
}
