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

package io.dingodb.exec.fun.time;

import io.dingodb.exec.utils.DingoDateTimeUtils;
import io.dingodb.expr.core.TypeCode;
import io.dingodb.expr.runtime.RtExpr;
import io.dingodb.expr.runtime.op.RtFun;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;

@Slf4j
public class CurrentTimestampFun extends RtFun {
    public static final String NAME = "current_timestamp";
    private static final long serialVersionUID = -6116182891807472846L;

    public CurrentTimestampFun(RtExpr[] paras) {
        super(paras);
    }

    @Override
    protected Object fun(Object @NonNull [] values) {
        return DingoDateTimeUtils.currentTimestamp();
    }

    @Override
    public int typeCode() {
        return TypeCode.TIMESTAMP;
    }
}