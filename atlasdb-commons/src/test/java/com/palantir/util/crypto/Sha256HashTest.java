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
package com.palantir.util.crypto;

import java.security.MessageDigest;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.BaseEncoding;

public class Sha256HashTest {

    @Test
    public void testSha256() throws Exception {
        MessageDigest digest = Sha256Hash.getMessageDigest();
        Assert.assertEquals(digest.getAlgorithm(), "SHA-256");
        Assert.assertNotSame(Sha256Hash.getMessageDigest(), digest);
        byte[] result = digest.digest("Hello World".getBytes(Charsets.UTF_8));
        Assert.assertEquals(
                "A591A6D40BF420404A011733CFB7B190D62C65BF0BCDA32B57B277D9AD9F146E",
                BaseEncoding.base16().encode(result));
        Assert.assertEquals(32, result.length);
    }

}
