package com.blazebit.persistence.criteria.impl.expression.function;

import javax.persistence.criteria.Expression;

import com.blazebit.persistence.criteria.impl.BlazeCriteriaBuilderImpl;

/**
 *
 * @author Christian Beikov
 * @since 1.2.0
 */
public class SqrtFunction extends FunctionExpressionImpl<Double> {

    private static final long serialVersionUID = 1L;
    
    public static final String NAME = "SQRT";

    public SqrtFunction(BlazeCriteriaBuilderImpl criteriaBuilder, Expression<? extends Number> expression) {
        super(criteriaBuilder, Double.class, NAME, expression);
    }
}