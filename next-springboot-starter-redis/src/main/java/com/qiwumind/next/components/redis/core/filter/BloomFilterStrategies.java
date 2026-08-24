/*
 * MIT License
 *
 * Copyright (c) 2026 qiwumind
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.  Author: liks
 * Email: 307039176@qq.com
 */

package com.qiwumind.next.components.redis.core.filter;



import com.google.common.collect.Lists;
import com.google.common.hash.Funnel;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Longs;

import java.util.List;

/**
 * Collections of strategies of generating the k * log(M) bits required for an
 * element to be mapped to a BloomFilter of M bits and k hash functions. These
 * strategies are part of the serialized form of the Bloom filters that use
 * them, thus they must be preserved as is (no updates allowed, only
 * introduction of new versions). Important: the order of the constants cannot
 * change, and they cannot be deleted - we depend on their ordinal for
 * BloomFilter serialization.
 *
 * @author Dimitris Andreou
 * @author Kurt Alfred Kluever
 */
public enum BloomFilterStrategies implements RedisBloomFilter.Strategy {
    /**
     * See "Less Hashing, Same Performance: Building a Better Bloom Filter" by
     * Adam Kirsch and Michael Mitzenmacher. The paper argues that this trick
     * doesn't significantly deteriorate the performance of a Bloom filter (yet
     * only needs two 32bit hash functions).
     */
    MURMUR128_MITZ_32() {
        @Override
        public <T> boolean put(T object, Funnel<? super T> funnel, int numHashFunctions, RedisBitmaps bits) {
            long bitSize = bits.bitSize();
            long hash64 = Hashing.murmur3_128().hashObject(object, funnel).asLong();
            int hash1 = (int) hash64;
            int hash2 = (int) (hash64 >>> 32);

            boolean bitsChanged = false;
            for (int i = 1; i <= numHashFunctions; i++) {
                int combinedHash = hash1 + (i * hash2);
                // Flip all the bits if it's negative (guaranteed positive number)
                if (combinedHash < 0) {
                    combinedHash = ~combinedHash;
                }
                bitsChanged |= bits.set(combinedHash % bitSize);
            }
            return bitsChanged;
        }

        @Override
        public <T> boolean mightContain(T object, Funnel<? super T> funnel, int numHashFunctions, RedisBitmaps bits) {
            long bitSize = bits.bitSize();
            long hash64 = Hashing.murmur3_128().hashObject(object, funnel).asLong();
            int hash1 = (int) hash64;
            int hash2 = (int) (hash64 >>> 32);

            for (int i = 1; i <= numHashFunctions; i++) {
                int combinedHash = hash1 + (i * hash2);
                // Flip all the bits if it's negative (guaranteed positive number)
                if (combinedHash < 0) {
                    combinedHash = ~combinedHash;
                }
                if (!bits.get(combinedHash % bitSize)) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public <T> void pipelinePut(List<T> objectlist, Funnel<? super T> funnel, int numHashFunctions,
                                    RedisBitmaps bits) {
            // TODO Auto-generated method stub

        }

    },
    /**
     * This strategy uses all 128 bits of {@link Hashing#murmur3_128} when
     * hashing. It looks different than the implementation in MURMUR128_MITZ_32
     * because we're avoiding the multiplication in the loop and doing a (much
     * simpler) += hash2. We're also changing the index to a positive number by
     * AND'ing with Long.MAX_VALUE instead of flipping the bits.
     */
    MURMUR128_MITZ_64() {

        @Override
        public <T> void pipelinePut(List<T> objectlist, Funnel<? super T> funnel, int numHashFunctions,
                                    RedisBitmaps bits) {
            List<long[]> offsetlists = Lists.newArrayList();
            objectlist.stream().forEach(object -> {
                long bitSize = bits.bitSize();
                byte[] bytes = Hashing.murmur3_128().hashObject(object, funnel).asBytes();
                long hash1 = lowerEight(bytes);
                long hash2 = upperEight(bytes);

                long combinedHash = hash1;
                //            for (int i = 0; i < numHashFunctions; i++) {
                //                // Make the combined hash positive and indexable
                //                bitsChanged |= bits.set((combinedHash & Long.MAX_VALUE) % bitSize);
                //                combinedHash += hash2;
                //            }
                    long[] offsets = new long[numHashFunctions];
                    for (int i = 0; i < numHashFunctions; i++) {
                        //先把所有的随机函数对应的索引位置收集到一个数组中
                        offsets[i] = (combinedHash & Long.MAX_VALUE) % bitSize;
                        combinedHash += hash2;
                    }
                    offsetlists.add(offsets);
                });
            bits.pipelineSet(offsetlists);
            bits.ensureCapacityInternal();//自动扩容

        }

        @Override
        public <T> boolean put(T object, Funnel<? super T> funnel, int numHashFunctions, RedisBitmaps bits) {
            long bitSize = bits.bitSize();
            byte[] bytes = Hashing.murmur3_128().hashObject(object, funnel).asBytes();
            long hash1 = lowerEight(bytes);
            long hash2 = upperEight(bytes);

            boolean bitsChanged = false;
            long combinedHash = hash1;
            //            for (int i = 0; i < numHashFunctions; i++) {
            //                // Make the combined hash positive and indexable
            //                bitsChanged |= bits.set((combinedHash & Long.MAX_VALUE) % bitSize);
            //                combinedHash += hash2;
            //            }

            long[] offsets = new long[numHashFunctions];
            for (int i = 0; i < numHashFunctions; i++) {
                //先把所有的随机函数对应的索引位置收集到一个数组中
                offsets[i] = (combinedHash & Long.MAX_VALUE) % bitSize;
                combinedHash += hash2;
            }
            bitsChanged = bits.set(offsets);
            bits.ensureCapacityInternal();//自动扩容

            return bitsChanged;
        }

        @Override
        public <T> boolean mightContain(T object, Funnel<? super T> funnel, int numHashFunctions, RedisBitmaps bits) {
            long bitSize = bits.bitSize();
            byte[] bytes = Hashing.murmur3_128().hashObject(object, funnel).asBytes();
            long hash1 = lowerEight(bytes);
            long hash2 = upperEight(bytes);

            long combinedHash = hash1;
            //            for (int i = 0; i < numHashFunctions; i++) {
            //                // Make the combined hash positive and indexable
            //                if (!bits.get((combinedHash & Long.MAX_VALUE) % bitSize)) {
            //                    return false;
            //                }
            //                combinedHash += hash2;
            //            }
            //            return true;
            long[] offsets = new long[numHashFunctions];
            for (int i = 0; i < numHashFunctions; i++) {
                offsets[i] = (combinedHash & Long.MAX_VALUE) % bitSize;
                combinedHash += hash2;
            }
            return bits.get(offsets);

        }

        private/* static */long lowerEight(byte[] bytes) {
            return Longs.fromBytes(bytes[7], bytes[6], bytes[5], bytes[4], bytes[3], bytes[2], bytes[1], bytes[0]);
        }

        private/* static */long upperEight(byte[] bytes) {
            return Longs
                    .fromBytes(bytes[15], bytes[14], bytes[13], bytes[12], bytes[11], bytes[10], bytes[9], bytes[8]);
        }

    };

}
