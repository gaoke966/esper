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
package com.espertech.esper.regressionlib.suite.expr.filter;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.scopetest.EPAssertionUtil;
import com.espertech.esper.common.internal.filterspec.FilterOperator;
import com.espertech.esper.common.internal.filterspec.FilterSpecParamForge;
import com.espertech.esper.common.internal.support.SupportBean;
import com.espertech.esper.common.internal.support.SupportBean_S0;
import com.espertech.esper.common.internal.support.SupportBean_S1;
import com.espertech.esper.common.internal.support.SupportBean_S2;
import com.espertech.esper.regressionlib.framework.RegressionEnvironment;
import com.espertech.esper.regressionlib.framework.RegressionExecution;
import com.espertech.esper.regressionlib.framework.RegressionPath;
import com.espertech.esper.regressionlib.support.util.SupportFilterSpecCompileHook;
import com.espertech.esper.runtime.client.DeploymentOptions;
import com.espertech.esper.runtime.internal.filtersvcimpl.FilterItem;

import java.util.ArrayList;
import java.util.Collection;

import static com.espertech.esper.common.internal.filterspec.FilterOperator.*;
import static com.espertech.esper.regressionlib.support.filter.SupportFilterHelper.assertFilterByTypeSingle;
import static com.espertech.esper.regressionlib.support.filter.SupportFilterHelper.assertFilterSingle;
import static org.junit.Assert.assertEquals;

public class ExprFilterOptimizableLimitedExpr {
    public static Collection<RegressionExecution> executions() {
        ArrayList<RegressionExecution> executions = new ArrayList<>();
        executions.add(new ExprFilterEqualsIsConstant());
        executions.add(new ExprFilterEqualsFromPatternSingle());
        executions.add(new ExprFilterEqualsFromPatternMulti());
        executions.add(new ExprFilterEqualsFromPatternConstant());
        executions.add(new ExprFilterEqualsFromPatternHalfConstant());
        executions.add(new ExprFilterEqualsFromPatternWithDotMethod());
        executions.add(new ExprFilterEqualsContextWithStart());
        executions.add(new ExprFilterEqualsSubstitutionParams());
        executions.add(new ExprFilterEqualsDeclaredExpr());
        executions.add(new ExprFilterEqualsConstantVariable());
        executions.add(new ExprFilterEqualsCoercion());
        executions.add(new ExprFilterRelOpCoercion());
        executions.add(new ExprFilterDisqualify());
        executions.add(new ExprFilterInSetOfValueWPatternWCoercion());
        executions.add(new ExprFilterInRangeWCoercion());
        executions.add(new ExprFilterOrRewrite());
        return executions;
    }

    private static class ExprFilterOrRewrite implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            String epl = "create context MyContext start SupportBean_S0 as s0;\n" +
                "@name('s0') context MyContext select * from SupportBean(theString = context.s0.p00 || context.s0.p01 or theString = context.s0.p01 || context.s0.p00);\n";
            env.compileDeploy(epl).addListener("s0");
            env.sendEventBean(new SupportBean_S0(1, "a", "b"));
            assertFilterSingle(env.statement("s0"), epl, "theString", IN_LIST_OF_VALUES);

            sendSBAssert(env, "ab", true);
            sendSBAssert(env, "ba", true);
            sendSBAssert(env, "aa", false);
            sendSBAssert(env, "aa", false);

            env.undeployAll();
        }
    }

    private static class ExprFilterInRangeWCoercion implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            String epl = "@name('s0') select * from pattern [" +
                "a=SupportBean_S0 -> b=SupportBean_S1 -> every SupportBean(longPrimitive in [a.id - 2 : b.id + 2])];\n";
            runAssertionInRange(env, epl, false);

            epl = "@name('s0') select * from pattern [" +
                "a=SupportBean_S0 -> b=SupportBean_S1 -> every SupportBean(longPrimitive not in [a.id - 2 : b.id + 2])];\n";
            runAssertionInRange(env, epl, true);
        }

        private void runAssertionInRange(RegressionEnvironment env, String epl, boolean not) {
            env.compileDeploy(epl).addListener("s0");
            env.sendEventBean(new SupportBean_S0(10));
            env.sendEventBean(new SupportBean_S1(200));

            env.milestone(0);
            assertFilterSingle(env.statement("s0"), epl, "longPrimitive", not ? NOT_RANGE_CLOSED : RANGE_CLOSED);

            sendSBAssert(env, 7, not);
            sendSBAssert(env, 8, !not);
            sendSBAssert(env, 100, !not);
            sendSBAssert(env, 202, !not);
            sendSBAssert(env, 203, not);

            env.undeployAll();
        }
    }

    private static class ExprFilterInSetOfValueWPatternWCoercion implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            String epl = "@name('s0') select * from pattern [" +
                "a=SupportBean_S0 -> b=SupportBean_S1 -> c=SupportBean_S2 -> every SupportBean(longPrimitive in (a.id, b.id, c.id))];\n";
            env.compileDeploy(epl).addListener("s0");
            env.sendEventBean(new SupportBean_S0(10));
            env.sendEventBean(new SupportBean_S1(200));
            env.sendEventBean(new SupportBean_S2(3000));

            env.milestone(0);

            assertFilterSingle(env.statement("s0"), epl, "longPrimitive", IN_LIST_OF_VALUES);

            sendSBAssert(env, 0, false);
            sendSBAssert(env, 10, true);
            sendSBAssert(env, 200, true);
            sendSBAssert(env, 3000, true);

            env.undeployAll();
        }
    }

    // test pattern
    public static class ExprFilterEqualsFromPatternWithDotMethod implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            String epl = "@name('s0') select * from pattern [a=SupportBean -> b=SupportBean(theString=a.getTheString())]";
            env.compileDeploy(epl).addListener("s0");
            env.sendEventBean(new SupportBean("E1", 1));
            assertFilterSingle(env.statement("s0"), epl, "theString", EQUAL);
            env.sendEventBean(new SupportBean("E1", 2));
            EPAssertionUtil.assertProps(env.listener("s0").assertOneGetNewAndReset(), "a.intPrimitive,b.intPrimitive".split(","), new Object[]{1, 2});
            env.undeployAll();
        }
    }

    public static class ExprFilterRelOpCoercion implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            String epl = "@name('s0') select * from SupportBean(Integer.parseInt('10') > doublePrimitive)";
            runAssertionRelOpCoercion(env, epl);

            epl = "@name('s0') select * from SupportBean(doublePrimitive < Integer.parseInt('10'))";
            runAssertionRelOpCoercion(env, epl);
        }
    }

    public static class ExprFilterEqualsCoercion implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            String epl = "@name('s0') select * from SupportBean(doublePrimitive = Integer.parseInt('10') + Long.parseLong('20'))";
            env.compileDeploy(epl).addListener("s0");
            assertFilterSingle(env.statement("s0"), epl, "doublePrimitive", EQUAL);

            sendSBAssert(env, 30d, true);
            sendSBAssert(env, 20d, false);
            sendSBAssert(env, 30d, true);

            env.undeployAll();
        }
    }

    public static class ExprFilterEqualsConstantVariable implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            String variable = "create constant variable string MYCONST = 'a';\n";
            tryDeployAndAssertionSB(env, variable + "@name('s0') select * from SupportBean(theString = MYCONST || 'x')", EQUAL);
            tryDeployAndAssertionSB(env, variable + "@name('s0') select * from SupportBean(MYCONST || 'x' = theString)", EQUAL);
        }
    }

    public static class ExprFilterEqualsDeclaredExpr implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            String epl = "@name('s0') expression AandX {'a' || 'x'}" +
                    "select * from SupportBean(AandX() = theString)";
            tryDeployAndAssertionSB(env, epl, EQUAL);
        }
    }

    public static class ExprFilterEqualsSubstitutionParams implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            String epl = "@name('s0') select * from SupportBean(theString = ?::string)";
            EPCompiled compiled = env.compile(epl);
            DeploymentOptions options = new DeploymentOptions();
            options.setStatementSubstitutionParameter(opt -> opt.setObject(1, "ax"));
            env.deploy(compiled, options).addListener("s0");
            runAssertionSB(env, epl, EQUAL);
        }
    }

    public static class ExprFilterEqualsIsConstant implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            tryDeployAndAssertionSB(env, "@name('s0') select * from SupportBean(theString = 'a' || 'x')", EQUAL);
            tryDeployAndAssertionSB(env, "@name('s0') select * from SupportBean('a' || 'x' is theString)", IS);
        }
    }

    public static class ExprFilterEqualsFromPatternSingle implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            String epl = "@name('s0') select * from pattern[every a=SupportBean_S0 -> SupportBean(a.p00 || a.p01 = theString)]";

            env.compileDeploy(epl).addListener("s0");
            env.sendEventBean(new SupportBean_S0(0, "a", "x"));
            assertFilterByTypeSingle(env.statement("s0"), "SupportBean", new FilterItem("theString", EQUAL));

            sendSBAssert(env, "a", false);
            sendSBAssert(env, "ax", true);

            env.milestone(0);

            env.sendEventBean(new SupportBean_S0(0, "b", "y"));
            sendSBAssert(env, "ax", false);
            sendSBAssert(env, "by", true);

            env.undeployAll();
        }
    }

    public static class ExprFilterEqualsFromPatternConstant implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            String epl = "@name('s0') select * from pattern[every SupportBean_S0 -> SupportBean_S1 -> SupportBean('a' || 'x' = theString)]";

            env.compileDeploy(epl).addListener("s0");
            env.sendEventBean(new SupportBean_S0(1));
            env.sendEventBean(new SupportBean_S1(2));
            assertFilterByTypeSingle(env.statement("s0"), "SupportBean", new FilterItem("theString", EQUAL));

            sendSBAssert(env, "a", false);
            sendSBAssert(env, "ax", true);

            env.undeployAll();
        }
    }

    public static class ExprFilterEqualsFromPatternHalfConstant implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            String epl = "@name('s0') select * from pattern[every s0=SupportBean_S0 -> s1=SupportBean_S1 -> SupportBean('a' || s1.p10 = theString)]";

            env.compileDeploy(epl).addListener("s0");
            env.sendEventBean(new SupportBean_S0(1));
            env.sendEventBean(new SupportBean_S1(2, "x"));
            assertFilterByTypeSingle(env.statement("s0"), "SupportBean", new FilterItem("theString", EQUAL));

            sendSBAssert(env, "a", false);
            sendSBAssert(env, "ax", true);

            env.undeployAll();
        }
    }

    public static class ExprFilterEqualsFromPatternMulti implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            String epl = "@name('s0') select * from pattern[every [2] a=SupportBean_S0 -> b=SupportBean_S1 -> SupportBean(theString = a[0].p00 || b.p10)]";

            env.compileDeploy(epl).addListener("s0");
            env.sendEventBean(new SupportBean_S0(1, "a"));
            env.sendEventBean(new SupportBean_S0(2, "b"));
            env.sendEventBean(new SupportBean_S1(2, "x"));
            assertFilterByTypeSingle(env.statement("s0"), "SupportBean", new FilterItem("theString", EQUAL));

            sendSBAssert(env, "a", false);
            sendSBAssert(env, "ax", true);

            env.sendEventBean(new SupportBean_S0(1, "z"));
            env.sendEventBean(new SupportBean_S0(2, "-"));
            env.sendEventBean(new SupportBean_S1(2, "y"));

            env.milestone(0);

            sendSBAssert(env, "ax", false);
            sendSBAssert(env, "zy", true);

            env.undeployAll();
        }
    }

    public static class ExprFilterEqualsContextWithStart implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            String epl = "create context MyContext start SupportBean_S0 as s0;\n" +
                    "@name('s0') context MyContext select * from SupportBean(theString = context.s0.p00 || context.s0.p01)";

            env.compileDeploy(epl).addListener("s0");
            env.sendEventBean(new SupportBean_S0(0, "a", "x"));
            assertFilterByTypeSingle(env.statement("s0"), "SupportBean", new FilterItem("theString", EQUAL));

            sendSBAssert(env, "a", false);
            sendSBAssert(env, "ax", true);

            env.milestone(0);

            sendSBAssert(env, "by", false);
            sendSBAssert(env, "ax", true);

            env.undeployAll();
        }
    }

    public static class ExprFilterDisqualify implements RegressionExecution {
        public void run(RegressionEnvironment env) {
            RegressionPath path = new RegressionPath();
            String objects = "@public create variable string MYVARIABLE_NONCONSTANT = 'abc';\n" +
                    "@public create table MyTable(tablecol string);\n" +
                    "@public create window MyWindow#keepall as SupportBean;\n" +
                    "@public create inlined_class \"\"\"\n" +
                    "  public class Helper {\n" +
                    "    public static String doit(Object param) {\n" +
                    "      return null;\n" +
                    "    }\n" +
                    "  }\n" +
                    "\"\"\";\n" +
                    "@public create expression MyDeclaredExpr { (select theString from MyWindow) };\n" +
                    "@public create expression MyHandThrough {v => v};\n";
            env.compile(objects, path);

            assertDisqualified(env, path, "SupportBean", "theString=Integer.toString(intPrimitive)");
            assertDisqualified(env, path, "SupportBean", "theString=MYVARIABLE_NONCONSTANT");
            assertDisqualified(env, path, "SupportBean", "theString=MyTable.tablecol");
            assertDisqualified(env, path, "SupportBean", "theString=(select theString from MyWindow)");
            assertDisqualified(env, path, "SupportBeanArrayCollMap", "id = setOfString.where(v => v=id).firstOf()");
            assertDisqualified(env, path, "SupportBean", "theString=Helper.doit(*)");
            assertDisqualified(env, path, "SupportBean", "theString=Helper.doit(me)");
            assertDisqualified(env, path, "SupportBean", "boolPrimitive=event_identity_equals(me, me)");
            assertDisqualified(env, path, "SupportBean", "theString=MyDeclaredExpr()");
            assertDisqualified(env, path, "SupportBean", "intPrimitive=theString.length()");
            assertDisqualified(env, path, "SupportBean", "intPrimitive = funcOne('hello')");
            assertDisqualified(env, path, "SupportBean", "boolPrimitive = exists(theString)");
            assertDisqualified(env, path, "SupportBean", "MyHandThrough(theString) = 'a' || 'x'");
        }
    }

    private static void tryDeployAndAssertionSB(RegressionEnvironment env, String epl, FilterOperator op) {
        env.compileDeploy(epl).addListener("s0");
        runAssertionSB(env, epl, op);
    }

    private static void runAssertionSB(RegressionEnvironment env, String epl, FilterOperator op) {
        assertFilterSingle(env.statement("s0"), epl, "theString", op);

        sendSBAssert(env, "ax", true);
        sendSBAssert(env, "a", false);

        env.milestone(0);

        sendSBAssert(env, "bx", false);
        sendSBAssert(env, "ax", true);

        env.undeployAll();
    }

    private static void assertDisqualified(RegressionEnvironment env, RegressionPath path, String typeName, String filters) {
        String hook = "@Hook(type=HookType.INTERNAL_FILTERSPEC, hook='" + SupportFilterSpecCompileHook.class.getName() + "')";
        String epl = hook + "select * from " + typeName + "(" + filters + ") as me";
        env.compile(epl, path);
        FilterSpecParamForge forge = SupportFilterSpecCompileHook.assertSingleAndReset(typeName);
        assertEquals(FilterOperator.BOOLEAN_EXPRESSION, forge.getFilterOperator());
    }

    private static void sendSBAssert(RegressionEnvironment env, String theString, boolean received) {
        env.sendEventBean(new SupportBean(theString, 0));
        assertEquals(received, env.listener("s0").getIsInvokedAndReset());
    }

    private static void sendSBAssert(RegressionEnvironment env, double doublePrimitive, boolean received) {
        SupportBean sb = new SupportBean("E", 0);
        sb.setDoublePrimitive(doublePrimitive);
        env.sendEventBean(sb);
        assertEquals(received, env.listener("s0").getIsInvokedAndReset());
    }

    private static void sendSBAssert(RegressionEnvironment env, long longPrimitive, boolean received) {
        SupportBean sb = new SupportBean("E", 0);
        sb.setLongPrimitive(longPrimitive);
        env.sendEventBean(sb);
        assertEquals(received, env.listener("s0").getIsInvokedAndReset());
    }

    private static void runAssertionRelOpCoercion(RegressionEnvironment env, String epl) {
        env.compileDeploy(epl).addListener("s0");
        assertFilterSingle(env.statement("s0"), epl, "doublePrimitive", LESS);

        sendSBAssert(env, 3d, true);
        sendSBAssert(env, 20d, false);
        sendSBAssert(env, 4d, true);

        env.undeployAll();
    }
}
