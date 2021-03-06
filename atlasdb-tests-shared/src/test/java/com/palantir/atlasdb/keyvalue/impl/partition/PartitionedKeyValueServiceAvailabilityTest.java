/**
 * Copyright 2015 Palantir Technologies
 *
 * Licensed under the BSD-3 License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.palantir.atlasdb.keyvalue.impl.partition;

import org.junit.Ignore;

import com.palantir.atlasdb.keyvalue.api.KeyValueService;
import com.palantir.atlasdb.keyvalue.impl.AbstractAtlasDbKeyValueServiceTest;

/**
 * This test is to make sure that any endpoints can fail as
 * long as it is not too many of them.
 *
 * The number of endpoints that can fail when doing a
 * write operation without causing the operation to fail:
 * <code>replicationFactor - writeFactor</code>
 *
 * The number of endpoints that can fail when doing a
 * read operation without causing the operation to fail:
 * <code>replicationFactor - readFactor</code>
 *
 * @author htarasiuk
 *
 */
public class PartitionedKeyValueServiceAvailabilityTest extends AbstractAtlasDbKeyValueServiceTest {

    @Override
    protected KeyValueService getKeyValueService() {
        return FailableKeyValueServices.sampleFailingKeyValueService();
    }

    @Override
    protected boolean reverseRangesSupported() {
        return false;
    }

    @Ignore
    @Override
    public void testDelete() {
        // ignore
    }

    @Ignore
    @Override
    public void testDeleteMultipleVersions() {
        // ignore
    }

    @Ignore
    @Override
    public void shouldAllowRemovingAllCellsInDynamicColumns() {
        // ignore - this is flaking
    }

}
