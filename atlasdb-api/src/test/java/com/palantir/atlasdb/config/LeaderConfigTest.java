/**
 * Copyright 2016 Palantir Technologies
 * <p>
 * Licensed under the BSD-3 License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://opensource.org/licenses/BSD-3-Clause
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.palantir.atlasdb.config;

import static java.util.Collections.emptySet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class LeaderConfigTest {
    @Test
    public void shouldBeTheLockLeaderIfLocalServerMatchesLockLeader() {
        ImmutableLeaderConfig config = ImmutableLeaderConfig.builder()
                .localServer("me")
                .addLeaders("not me", "me")
                .quorumSize(2)
                .lockCreator("me")
                .build();

        assertThat(config.whoIsTheLockLeader(), is(LockLeader.I_AM_THE_LOCK_LEADER));
    }

    @Test
    public void shouldNotBeTheLockLeaderIfLocalServerDoesNotMatchLockLeader() {
        ImmutableLeaderConfig config = ImmutableLeaderConfig.builder()
                .localServer("me")
                .addLeaders("not me", "me")
                .quorumSize(2)
                .lockCreator("not me")
                .build();

        assertThat(config.whoIsTheLockLeader(), is(LockLeader.SOMEONE_ELSE_IS_THE_LOCK_LEADER));
    }


    @Test
    public void lockLeaderDefaultsToBeTheFirstLeader() {
        ImmutableLeaderConfig config = ImmutableLeaderConfig.builder()
                .localServer("me")
                .addLeaders("not me", "me")
                .quorumSize(2)
                .build();

        assertThat(config.lockCreator(), is("not me"));
    }

    @Test(expected = IllegalStateException.class)
    public void cannotCreateALeaderConfigWithNoLeaders() {
        ImmutableLeaderConfig.builder()
                .localServer("me")
                .leaders(emptySet())
                .quorumSize(0)
                .build();
    }
}
