/*
 * Copyright 2014 Blazebit.
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
package com.blazebit.persistence.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Tuple;

import com.blazebit.persistence.LeafOngoingSetOperationSubqueryBuilder;
import com.blazebit.persistence.StartOngoingSetOperationSubqueryBuilder;
import com.blazebit.persistence.SubqueryBuilder;
import com.blazebit.persistence.impl.expression.Expression;
import com.blazebit.persistence.impl.expression.ExpressionFactory;
import com.blazebit.persistence.spi.DbmsStatementType;
import com.blazebit.persistence.spi.SetOperationType;

/**
 *
 * @author Christian Beikov
 * @author Moritz Becker
 * @since 1.0
 */
public class SubqueryBuilderImpl<T> extends AbstractCommonQueryBuilder<Tuple, SubqueryBuilder<T>, LeafOngoingSetOperationSubqueryBuilder<T>, StartOngoingSetOperationSubqueryBuilder<T, LeafOngoingSetOperationSubqueryBuilder<T>>, FinalSetOperationSubqueryBuilderImpl<T>> implements SubqueryBuilder<T>, SubqueryInternalBuilder<T> {

    private final T result;
    private final SubqueryBuilderListener<T> listener;

    public SubqueryBuilderImpl(MainQuery mainQuery, AliasManager aliasManager, JoinManager parentJoinManager, ExpressionFactory expressionFactory, T result, SubqueryBuilderListener<T> listener) {
        super(mainQuery, false, DbmsStatementType.SELECT, Tuple.class, null, aliasManager, parentJoinManager, expressionFactory, null);
        this.result = result;
        this.listener = listener;
    }

    @Override
    public List<Expression> getSelectExpressions() {
        List<Expression> selectExpressions = new ArrayList<Expression>(selectManager.getSelectInfos().size());

        for (SelectInfo info : selectManager.getSelectInfos()) {
            selectExpressions.add(info.getExpression());
        }

        return selectExpressions;
    }

    @Override
    public T end() {
        listener.onBuilderEnded(this);
        return result;
    }

    public T getResult() {
        return result;
    }
    
    @Override
    protected FinalSetOperationSubqueryBuilderImpl<T> createFinalSetOperationBuilder(SetOperationType operator, boolean nested) {
        return new FinalSetOperationSubqueryBuilderImpl<T>(mainQuery, result, operator, nested, listener, this);
    }

    @Override
    protected LeafOngoingSetOperationSubqueryBuilder<T> createSetOperand(FinalSetOperationSubqueryBuilderImpl<T> finalSetOperationBuilder) {
        return new LeafOngoingSetOperationSubqueryBuilderImpl<T>(mainQuery, finalSetOperationBuilder);
    }

    @Override
    protected StartOngoingSetOperationSubqueryBuilder<T, LeafOngoingSetOperationSubqueryBuilder<T>> createSubquerySetOperand(FinalSetOperationSubqueryBuilderImpl<T> finalSetOperationBuilder, FinalSetOperationSubqueryBuilderImpl<T> resultFinalSetOperationBuilder) {
        LeafOngoingSetOperationSubqueryBuilderImpl<T> leafCb = new LeafOngoingSetOperationSubqueryBuilderImpl<T>(mainQuery, resultFinalSetOperationBuilder);
        return new OngoingSetOperationSubqueryBuilderImpl<T, LeafOngoingSetOperationSubqueryBuilder<T>>(mainQuery, finalSetOperationBuilder, leafCb);
    }
}
