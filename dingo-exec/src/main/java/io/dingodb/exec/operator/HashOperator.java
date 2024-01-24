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

import io.dingodb.exec.dag.Vertex;
import io.dingodb.exec.operator.data.Context;
import io.dingodb.exec.operator.params.HashParam;
import org.checkerframework.checker.nullness.qual.NonNull;

public class HashOperator extends FanOutOperator {
    public static final HashOperator INSTANCE = new HashOperator();

    private HashOperator() {}

    @Override
    protected int calcOutputIndex(Context context, Object @NonNull [] tuple, Vertex vertex) {
        HashParam param = vertex.getParam();
        return param.getStrategy().selectOutput(param.getKeyMapping().revMap(tuple));
    }

}
