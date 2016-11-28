/*
 * Copyright 2014 - 2016 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blazebit.persistence.impl.eclipselink.function;

import com.blazebit.persistence.spi.JpqlFunction;
import org.eclipse.persistence.expressions.Expression;
import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.internal.expressions.ExpressionSQLPrinter;
import org.eclipse.persistence.internal.sessions.AbstractSession;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Vector;

/**
 *
 * @author Christian Beikov
 * @since 1.0
 */
public class JpqlFunctionExpressionOperator extends ExpressionOperator {
    
    private static final long serialVersionUID = 1L;
    
    private final JpqlFunction function;
    private final AbstractSession session;

    public JpqlFunctionExpressionOperator(JpqlFunction function, AbstractSession session) {
        this.function = function;
        this.session = session;
    }

    @Override
    public void printDuo(Expression first, Expression second, ExpressionSQLPrinter printer) {
        prepare(Arrays.asList(first, second));
        super.printDuo(first, second, printer);
    }

    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void printCollection(Vector items, ExpressionSQLPrinter printer) {
        prepare((List<Expression>) items);
        super.printCollection(items, printer);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void prepare(List<Expression> items) {
        EclipseLinkFunctionRenderContext context;
        // for eclipselink, we need to append one dummy argument for functions without any arguments
        // to make this transparent for the JpqlFunction implementation, we need to remove this dummy argument at this point
        if (function.hasArguments()) {
            context = new EclipseLinkFunctionRenderContext(items, session);
        } else {
            if (items.size() > 1) {
                throw new IllegalStateException("Expected only one dummy argument for function [" + function.getClass() + "] but found " + items.size() + " arguments.");
            }
            context = new EclipseLinkFunctionRenderContext(Collections.<Expression>emptyList(), session);
        }
        function.render(context);
        setArgumentIndices(context.getArgumentIndices());
        printsAs(new Vector(context.getChunks()));
        
        if (context.isChunkFirst()) {
            bePrefix();
        } else {
            bePostfix();
        }
    }
}
