/*
 * Copyright 2021 DataCanvas
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.dingodb.exec.operator;

import com.google.common.collect.Iterators;
import io.dingodb.common.type.TupleMapping;
import io.dingodb.exec.dag.Vertex;
import io.dingodb.exec.expr.SqlExpr;
import io.dingodb.exec.operator.data.Context;
import io.dingodb.exec.operator.params.FilterProjectParam;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Iterator;

public abstract class FilterProjectOperator extends IteratorOperator {

    @Override
    protected Iterator<Object[]> createIterator(Context context, Object[] tuple, Vertex vertex) {
        FilterProjectParam param = vertex.getParam();
        Iterator<Object[]> iterator = createSourceIterator(context, tuple, vertex);
        SqlExpr filter = param.getFilter();
        TupleMapping selection = param.getSelection();
        if (selection != null) {
            iterator = Iterators.transform(iterator, selection::revMap);
        }
        if (filter != null) {
            iterator = Iterators.filter(
                iterator,
                t -> {
                    Object v = filter.eval(t);
                    return v != null && (Boolean) v;
                }
            );
        }
        return iterator;
    }

    protected abstract @NonNull Iterator<Object[]> createSourceIterator(Context context, Object[] tuple, Vertex vertex);
}
