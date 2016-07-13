package com.palantir.leader;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.net.HostAndPort;
import com.palantir.paxos.PaxosAcceptor;
import com.palantir.paxos.PaxosInstanceId;
import com.palantir.paxos.PaxosLearner;
import com.palantir.paxos.PaxosProposer;
import com.palantir.paxos.PaxosValue;

public class PaxosLeaderElectionServiceTest {

    private static final String PROPOSER_UUID = "ProposerUUID";
    private PaxosLeaderElectionService electionService;
    private final PaxosLearner otherLearner = mock(PaxosLearner.class);
    private final PaxosLearner knowledge = mock(PaxosLearner.class);
    private final PaxosProposer proposer = mock(PaxosProposer.class);
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private PaxosAcceptor acceptor = mock(PaxosAcceptor.class);

    @Before
    public void setup() {
        Map<PingableLeader, HostAndPort> potentialLeadersToHosts = ImmutableMap.of();
        List<PaxosAcceptor> acceptors = ImmutableList.of(acceptor);
        List<PaxosLearner> learners = ImmutableList.of(otherLearner);
        long updatePollingWainInMs = 0;
        long randomWaitBeforeProposingLeadership = 0;
        long leaderPingResponseWaitMs = 0;
        electionService = new PaxosLeaderElectionService(proposer, knowledge, potentialLeadersToHosts, acceptors, learners, executor, updatePollingWainInMs, randomWaitBeforeProposingLeadership, leaderPingResponseWaitMs);

        when(proposer.getQuorumSize()).thenReturn(1);
        when(proposer.getUUID()).thenReturn(PROPOSER_UUID);
    }


    @Test
    public void should_propose_leadership_if_no_other_leaders() throws Exception {
        // TODO this depends on the number of invocations internally and is brittle. Prefer to return _after_ proposer.propose() is called
        PaxosInstanceId key = PaxosInstanceId.fromSeq(0);
        PaxosValue valueAfter = new PaxosValue(key, "ProposerUUID".getBytes(Charsets.UTF_8));
        when(knowledge.getGreatestLearnedValue()).thenReturn(
                null, null,
                null, valueAfter);
        when(otherLearner.getGreatestLearnedValue()).thenReturn(valueAfter);

        electionService.blockOnBecomingLeader();

        verify(proposer).propose(eq(key), any(byte[].class));
    }

    @Test
    public void should_propose_leadership_with_greater_sequence_number() throws Exception {
        long seqBefore = 10;
        PaxosInstanceId keyBefore = PaxosInstanceId.fromSeq(seqBefore);
        PaxosInstanceId keyAfter = PaxosInstanceId.fromSeq(seqBefore + 1);
        PaxosValue valueBefore = new PaxosValue(keyBefore, "ProposerUUID".getBytes(Charsets.UTF_8));
        PaxosValue valueAfter = new PaxosValue(keyAfter, "ProposerUUID".getBytes(Charsets.UTF_8));
        when(knowledge.getGreatestLearnedValue()).thenReturn(
                valueBefore, valueBefore,
                valueBefore, valueAfter);
        when(otherLearner.getGreatestLearnedValue()).thenReturn(valueAfter);

        electionService.blockOnBecomingLeader();

        verify(proposer).propose(eq(keyAfter), any(byte[].class));
    }
}
