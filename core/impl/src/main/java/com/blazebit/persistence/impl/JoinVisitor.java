/*
 * Copyright 2014 - 2017 Blazebit.
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

import com.blazebit.persistence.impl.expression.Expression;
import com.blazebit.persistence.impl.expression.FunctionExpression;
import com.blazebit.persistence.impl.expression.PathExpression;
import com.blazebit.persistence.impl.expression.SubqueryExpression;
import com.blazebit.persistence.impl.expression.TreatExpression;
import com.blazebit.persistence.impl.expression.VisitorAdapter;
import com.blazebit.persistence.impl.predicate.EqPredicate;
import com.blazebit.persistence.impl.predicate.InPredicate;
import com.blazebit.persistence.impl.predicate.IsEmptyPredicate;
import com.blazebit.persistence.impl.predicate.IsNullPredicate;
import com.blazebit.persistence.impl.predicate.MemberOfPredicate;

/**
 *
 * @author Moritz Becker
 * @since 1.0
 */
public class JoinVisitor extends VisitorAdapter {

    private final JoinManager joinManager;
    private boolean joinRequired;
    private boolean joinWithObjectLeafAllowed = true;
    private ClauseType fromClause;

    public JoinVisitor(JoinManager joinManager) {
        this.joinManager = joinManager;
        // By default we require joins
        this.joinRequired = true;
    }

    public ClauseType getFromClause() {
        return fromClause;
    }

    public void setFromClause(ClauseType fromClause) {
        this.fromClause = fromClause;
    }

    @Override
    public void visit(PathExpression expression) {
        Expression aliasedExpression;
        if ((aliasedExpression = joinManager.getJoinableSelectAlias(expression, fromClause == ClauseType.SELECT, false)) != null) {
            aliasedExpression.accept(this);
        } else {
            joinManager.implicitJoin(expression, joinWithObjectLeafAllowed, null, fromClause, false, false, joinRequired);
        }
    }

    @Override
    public void visit(TreatExpression expression) {
        throw new IllegalArgumentException("Treat should not be a root of an expression: " + expression.toString());
    }

    public boolean isJoinRequired() {
        return joinRequired;
    }
    
    public void setJoinRequired(boolean joinRequired) {
        this.joinRequired = joinRequired;
    }

    @Override
    public void visit(FunctionExpression expression) {
        // do not join outer expressions
        if (!com.blazebit.persistence.impl.util.ExpressionUtils.isOuterFunction(expression)) {
            super.visit(expression);
        }
    }

    // Added eager initialization of subqueries
    @Override
    public void visit(SubqueryExpression expression) {
        // TODO: we have to pass the fromClause into the subquery so that joins, generated by OUTERs from the subquery don't get
        // rendered if not needed
        // TODO: this is ugly
        ((AbstractCommonQueryBuilder<?, ?, ?, ?, ?>) expression.getSubquery()).applyImplicitJoins();
    }

    public boolean isJoinWithObjectLeafAllowed() {
        return joinWithObjectLeafAllowed;
    }

    public void setJoinWithObjectLeafAllowed(boolean joinWithObjectLeafAllowed) {
        this.joinWithObjectLeafAllowed = joinWithObjectLeafAllowed;
    }

    @Override
    public void visit(EqPredicate predicate) {
        boolean original = joinRequired;
        joinRequired = false;
        predicate.getLeft().accept(this);
        predicate.getRight().accept(this);
        joinRequired = original;
    }

    @Override
    public void visit(IsNullPredicate predicate) {
        boolean original = joinRequired;
        joinRequired = false;
        predicate.getExpression().accept(this);
        joinRequired = original;
    }

    @Override
    public void visit(IsEmptyPredicate predicate) {
        boolean original = joinRequired;
        joinRequired = false;
        predicate.getExpression().accept(this);
        joinRequired = original;
    }

    @Override
    public void visit(MemberOfPredicate predicate) {
        boolean original = joinRequired;
        joinRequired = false;
        predicate.getLeft().accept(this);
        predicate.getRight().accept(this);
        joinRequired = original;
    }

    @Override
    public void visit(InPredicate predicate) {
        boolean original = joinRequired;
        joinRequired = false;
        predicate.getLeft().accept(this);
        for (Expression right : predicate.getRight()) {
            right.accept(this);
        }
        joinRequired = original;
    }
}
