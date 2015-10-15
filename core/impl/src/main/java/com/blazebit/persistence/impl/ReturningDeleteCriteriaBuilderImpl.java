/*
 * Copyright 2015 Blazebit.
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

import com.blazebit.persistence.ReturningDeleteCriteriaBuilder;

/**
 *
 * @param <T> The query result type
 * @author Christian Beikov
 * @since 1.1.0
 */
public class ReturningDeleteCriteriaBuilderImpl<T, Y> extends BaseDeleteCriteriaBuilderImpl<T, ReturningDeleteCriteriaBuilder<T, Y>, Y> implements ReturningDeleteCriteriaBuilder<T, Y> {

    public ReturningDeleteCriteriaBuilderImpl(MainQuery mainQuery, Class<T> clazz, String alias, Class<?> cteClass, Y result, CTEBuilderListener listener) {
        super(mainQuery, false, clazz, alias, cteClass, result, listener);
    }

    @Override
    protected void getCteQueryString1(StringBuilder sbSelectFrom) {
        super.getCteQueryString1(sbSelectFrom);
        applyJpaReturning(sbSelectFrom);
    }

}
