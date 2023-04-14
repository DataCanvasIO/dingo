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

package io.dingodb.server.executor.service;

import com.google.auto.service.AutoService;
import io.dingodb.common.CommonId;
import io.dingodb.store.api.StoreInstance;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StoreService implements io.dingodb.store.api.StoreService {
    public static final StoreService DEFAULT_INSTANCE = new StoreService();

    @AutoService(io.dingodb.store.api.StoreServiceProvider.class)
    public static class StoreServiceProvider implements io.dingodb.store.api.StoreServiceProvider {
        @Override
        public io.dingodb.store.api.StoreService get() {
            return DEFAULT_INSTANCE;
        }
    }

    private final Map<CommonId, StoreInstance> storeInstanceMap = new ConcurrentHashMap<>();

    private StoreService() {
    }

    @Override
    public StoreInstance getInstance(@NonNull CommonId id) {
        return storeInstanceMap.get(id);
    }

    @Override
    public void deleteInstance(CommonId id) {
        storeInstanceMap.remove(id);
    }

}