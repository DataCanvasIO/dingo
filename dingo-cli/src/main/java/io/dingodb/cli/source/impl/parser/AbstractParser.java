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

package io.dingodb.cli.source.impl.parser;

import io.dingodb.cli.source.Parser;
import io.dingodb.common.table.TableDefinition;
import io.dingodb.common.table.TupleSchema;
import io.dingodb.sdk.client.DingoClient;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class AbstractParser implements Parser {

    @Override
    public void parse(TableDefinition tableDefinition, List<Object[]> records, DingoClient dingoClient) {
        List<Object[]> result = new ArrayList<>();
        for (Object[] arr : records) {
            if (arr.length == tableDefinition.getColumns().size()) {
                try {
                    TupleSchema schema = tableDefinition.getTupleSchema();
                    Object[] record = schema.parse(arr);
                    result.add(record);
                } catch (Exception e) {
                    log.warn("Data:{} parsing failed", arr);
                }
            } else {
                log.warn("The current data is missing a field value, skip it:{} ", arr);
            }
        }
        try {
            dingoClient.insert(result);
        } catch (Exception e) {
            log.error("Error encoding record", e);
        }
    }
}