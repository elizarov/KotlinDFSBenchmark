/*
 * Copyright (c) 2014, Oracle America, Inc.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 *  * Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 *
 *  * Neither the name of Oracle nor the names of its contributors may be used
 *    to endorse or promote products derived from this software without
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF
 * THE POSSIBILITY OF SUCH DAMAGE.
 */

package bench.dfs

import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@Fork(1, jvmArgs = ["-Xss64m"])
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 10, time = 1, timeUnit = TimeUnit.SECONDS)
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
open class SpecialGraphBenchmark : Base() {
    val g = Graph(n, 2 * m)
    val sv = IntArray(n)
    val se = IntArray(n)
    var st = 0

    init {
        val rnd = Random(1)
        repeat(m) {
            val v = rnd.nextInt(n)
            val u = rnd.nextInt(n)
            g.add(v, u)
            g.add(u, v)
        }
    }

    @Benchmark
    fun testRecursive() {
        fun dfs(v: Int) {
            f[v] = true
            g.from(v) { u ->
                if (!f[u]) dfs(u)
            }
            ord[cnt++] = v
        }
        for (v in 0 until n) {
            if (!f[v]) dfs(v)
        }
    }

    @Benchmark
    fun testDRF() {
        val dfs = DeepRecursiveFunction { v: Int ->
            f[v] = true
            g.from(v) { u ->
                if (!f[u]) callRecursive(u)
            }
            ord[cnt++] = v
        }
        for (v in 0 until n) {
            if (!f[v]) dfs(v)
        }
    }

    @Benchmark
    fun testArrayStack() {
        st = -1
        fun dfs(v0: Int) {
            pushCall(v0)
            loop@ while (st >= 0) {
                val v = sv[st]
                var e = se[st]
                while (e >= 0) {
                    val u = g.eVert[e]
                    e = g.eNext[e]
                    se[st] = e
                    if (!f[u]) {
                        pushCall(u)
                        continue@loop
                    }
                }
                ord[cnt++] = v
                st--
            }
        }
        for (v in 0 until n) {
            if (!f[v]) dfs(v)
        }
    }

    private fun pushCall(v: Int) {
        st++
        sv[st] = v
        se[st] = g.vHead[v]
        f[v] = true
    }
}

class Graph(vCap: Int, eCap: Int) {
    var eCnt = 0
    var vHead = IntArray(vCap) { -1 }
    var eVert = IntArray(eCap)
    var eNext = IntArray(eCap)

    fun add(v: Int, u: Int, e: Int = eCnt++) {
        eVert[e] = u
        eNext[e] = vHead[v]
        vHead[v] = e
    }

    inline fun from(v: Int, action: (u: Int) -> Unit) {
        var e = vHead[v]
        while (e >= 0) {
            action(eVert[e])
            e = eNext[e]
        }
    }
}
