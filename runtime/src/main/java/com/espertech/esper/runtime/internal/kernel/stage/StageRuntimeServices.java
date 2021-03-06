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
package com.espertech.esper.runtime.internal.kernel.stage;

import com.espertech.esper.common.internal.epl.namedwindow.consume.NamedWindowDispatchService;
import com.espertech.esper.common.internal.epl.table.core.TableExprEvaluatorContext;
import com.espertech.esper.common.internal.epl.variable.core.VariableManagementService;
import com.espertech.esper.common.internal.event.core.EventBeanTypedEventFactory;
import com.espertech.esper.common.internal.event.core.EventTypeResolvingBeanFactory;
import com.espertech.esper.common.internal.event.eventtyperepo.EventTypeRepository;
import com.espertech.esper.common.internal.settings.ExceptionHandlingService;
import com.espertech.esper.common.internal.settings.RuntimeSettingsService;
import com.espertech.esper.common.internal.statement.dispatch.DispatchService;
import com.espertech.esper.runtime.internal.kernel.thread.ThreadingService;
import com.espertech.esper.runtime.internal.statementlifesvc.StatementLifecycleService;

public class StageRuntimeServices {
    private final DispatchService dispatchService;
    private final EventBeanTypedEventFactory eventBeanTypedEventFactory;
    private final EventTypeRepository eventTypeRepositoryBus;
    private final EventTypeResolvingBeanFactory eventTypeResolvingBeanFactory;
    private final ExceptionHandlingService exceptionHandlingService;
    private final NamedWindowDispatchService namedWindowDispatchService;
    private final RuntimeSettingsService runtimeSettingsService;
    private final StatementLifecycleService statementLifecycleService;
    private final TableExprEvaluatorContext tableExprEvaluatorContext;
    private final ThreadingService threadingService;
    private final VariableManagementService variableManagementService;

    public StageRuntimeServices(DispatchService dispatchService, EventBeanTypedEventFactory eventBeanTypedEventFactory, EventTypeRepository eventTypeRepositoryBus, EventTypeResolvingBeanFactory eventTypeResolvingBeanFactory, ExceptionHandlingService exceptionHandlingService, NamedWindowDispatchService namedWindowDispatchService, RuntimeSettingsService runtimeSettingsService, StatementLifecycleService statementLifecycleService, TableExprEvaluatorContext tableExprEvaluatorContext, ThreadingService threadingService, VariableManagementService variableManagementService) {
        this.dispatchService = dispatchService;
        this.eventBeanTypedEventFactory = eventBeanTypedEventFactory;
        this.eventTypeRepositoryBus = eventTypeRepositoryBus;
        this.eventTypeResolvingBeanFactory = eventTypeResolvingBeanFactory;
        this.exceptionHandlingService = exceptionHandlingService;
        this.namedWindowDispatchService = namedWindowDispatchService;
        this.runtimeSettingsService = runtimeSettingsService;
        this.statementLifecycleService = statementLifecycleService;
        this.tableExprEvaluatorContext = tableExprEvaluatorContext;
        this.threadingService = threadingService;
        this.variableManagementService = variableManagementService;
    }

    public DispatchService getDispatchService() {
        return dispatchService;
    }

    public EventBeanTypedEventFactory getEventBeanTypedEventFactory() {
        return eventBeanTypedEventFactory;
    }

    public EventTypeRepository getEventTypeRepositoryBus() {
        return eventTypeRepositoryBus;
    }

    public EventTypeResolvingBeanFactory getEventTypeResolvingBeanFactory() {
        return eventTypeResolvingBeanFactory;
    }

    public ExceptionHandlingService getExceptionHandlingService() {
        return exceptionHandlingService;
    }

    public NamedWindowDispatchService getNamedWindowDispatchService() {
        return namedWindowDispatchService;
    }

    public RuntimeSettingsService getRuntimeSettingsService() {
        return runtimeSettingsService;
    }

    public StatementLifecycleService getStatementLifecycleService() {
        return statementLifecycleService;
    }

    public VariableManagementService getVariableManagementService() {
        return variableManagementService;
    }

    public TableExprEvaluatorContext getTableExprEvaluatorContext() {
        return tableExprEvaluatorContext;
    }

    public ThreadingService getThreadingService() {
        return threadingService;
    }
}
