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

package io.dingodb.exec.transaction.visitor;

import io.dingodb.common.Location;
import io.dingodb.exec.base.IdGenerator;
import io.dingodb.exec.base.Job;
import io.dingodb.exec.base.Output;
import io.dingodb.exec.impl.IdGeneratorImpl;
import io.dingodb.exec.transaction.base.ITransaction;
import io.dingodb.exec.transaction.visitor.data.CommitLeaf;
import io.dingodb.exec.transaction.visitor.data.Composite;
import io.dingodb.exec.transaction.visitor.data.Element;
import io.dingodb.exec.transaction.visitor.data.ElementName;
import io.dingodb.exec.transaction.visitor.data.Leaf;
import io.dingodb.exec.transaction.visitor.data.PreWriteLeaf;
import io.dingodb.exec.transaction.visitor.data.RollBackLeaf;
import io.dingodb.exec.transaction.visitor.data.RootLeaf;
import io.dingodb.exec.transaction.visitor.data.ScanCacheLeaf;
import io.dingodb.exec.transaction.visitor.data.StreamConverterLeaf;
import io.dingodb.exec.transaction.visitor.data.TransactionElements;
import io.dingodb.exec.transaction.visitor.function.DingoCommitVisitFun;
import io.dingodb.exec.transaction.visitor.function.DingoPreWriteVisitFun;
import io.dingodb.exec.transaction.visitor.function.DingoRollBackVisitFun;
import io.dingodb.exec.transaction.visitor.function.DingoScanCacheVisitFun;
import io.dingodb.exec.transaction.visitor.function.DingoStreamConverterVisitFun;
import io.dingodb.exec.transaction.visitor.function.DingoTransactionRootVisitFun;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collection;
import java.util.Collections;

@Slf4j
public class DingoTransactionRenderJob implements Visitor<Collection<Output>> {

    private final IdGenerator idGenerator;
    private final Location currentLocation;
    @Getter
    private final Job job;
    private final ITransaction transaction;

    public DingoTransactionRenderJob(Job job, IdGenerator idGenerator, Location currentLocation, ITransaction transaction) {
        this.job = job;
        this.idGenerator = idGenerator;
        this.currentLocation = currentLocation;
        this.transaction = transaction;
    }

    public static void renderPreWriteJob(Job job, Location currentLocation, ITransaction transaction, boolean checkRoot) {
        IdGenerator idGenerator = new IdGeneratorImpl(job.getJobId().seq);
        DingoTransactionRenderJob visitor = new DingoTransactionRenderJob(job, idGenerator, currentLocation, transaction);
        Element element;
        if (transaction.getChannelMap().size() > 0) {
            element = TransactionElements.getElement(ElementName.MULTI_TRANSACTION_PRE_WRITE);
        } else {
            element = TransactionElements.getElement(ElementName.SINGLE_TRANSACTION_PRE_WRITE);
        }
        Collection<Output> outputs = element.accept(visitor);
        if (checkRoot && outputs.size() > 0) {
            throw new IllegalStateException("There root of plan must be `DingoRoot`.");
        }
//        if (log.isDebugEnabled()) {
        log.info("job = {}", job);
//        }
    }

    public static void renderCommitJob(Job job, Location currentLocation, ITransaction transaction, boolean checkRoot) {
        IdGenerator idGenerator = new IdGeneratorImpl(job.getJobId().seq);
        DingoTransactionRenderJob visitor = new DingoTransactionRenderJob(job, idGenerator, currentLocation, transaction);
        Element element;
        if (transaction.getChannelMap().size() > 0) {
            element = TransactionElements.getElement(ElementName.MULTI_TRANSACTION_COMMIT);
        } else {
            element = TransactionElements.getElement(ElementName.SINGLE_TRANSACTION_COMMIT);
        }
        Collection<Output> outputs = element.accept(visitor);
        if (checkRoot && outputs.size() > 0) {
            throw new IllegalStateException("There root of plan must be `DingoRoot`.");
        }
        if (log.isDebugEnabled()) {
            log.info("job = {}", job);
        }
    }

    public static void renderRollBackJob(Job job, Location currentLocation, ITransaction transaction, boolean checkRoot) {
        IdGenerator idGenerator = new IdGeneratorImpl(job.getJobId().seq);
        DingoTransactionRenderJob visitor = new DingoTransactionRenderJob(job, idGenerator, currentLocation, transaction);
        Element element;
        if (transaction.getChannelMap().size() > 0) {
            element = TransactionElements.getElement(ElementName.MULTI_TRANSACTION_ROLLBACK);
        } else {
            element = TransactionElements.getElement(ElementName.SINGLE_TRANSACTION_ROLLBACK);
        }
        Collection<Output> outputs = element.accept(visitor);
        if (checkRoot && outputs.size() > 0) {
            throw new IllegalStateException("There root of plan must be `DingoRoot`.");
        }
        if (log.isDebugEnabled()) {
            log.info("job = {}", job);
        }
    }

    @Override
    public Collection<Output> visit(Leaf leaf) {
        return Collections.emptyList();
    }

    @Override
    public Collection<Output> visit(RootLeaf rootLeaf) {
        return DingoTransactionRootVisitFun.visit(job, idGenerator, currentLocation, transaction, this, rootLeaf);
    }

    @Override
    public Collection<Output> visit(@NonNull ScanCacheLeaf scanCacheLeaf) {
        return DingoScanCacheVisitFun.visit(job, idGenerator, currentLocation, transaction, this, scanCacheLeaf);
    }

    @Override
    public Collection<Output> visit(PreWriteLeaf preWriteLeaf) {
        return DingoPreWriteVisitFun.visit(job, idGenerator, currentLocation, transaction, this, preWriteLeaf);
    }

    @Override
    public Collection<Output> visit(StreamConverterLeaf streamConverterLeaf) {
        return DingoStreamConverterVisitFun.visit(job, idGenerator, currentLocation, transaction, this, streamConverterLeaf);
    }

    @Override
    public Collection<Output> visit(CommitLeaf commitLeaf) {
        return DingoCommitVisitFun.visit(job, idGenerator, currentLocation, transaction, this, commitLeaf);
    }

    @Override
    public Collection<Output> visit(RollBackLeaf rollBackLeaf) {
        return DingoRollBackVisitFun.visit(job, idGenerator, currentLocation, transaction, this, rollBackLeaf);
    }

    @Override
    public Collection<Output> visit(Composite composite) {
        return null;
    }

}
