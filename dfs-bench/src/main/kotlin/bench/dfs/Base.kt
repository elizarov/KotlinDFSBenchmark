package bench.dfs

import org.openjdk.jmh.annotations.*

@State(Scope.Benchmark)
open class Base {
    val n = 200_000
    val m = 200_000
    val f = BooleanArray(n) // visited vertices
    var cnt = 0
    val ord = IntArray(n) // recorded in post-order
    val expectedOrdHash = -446843743

    @Setup(Level.Invocation)
    fun clean() {
        cnt = 0
        f.fill(false)
    }

    @TearDown(Level.Invocation)
    fun validate() {
        check(cnt == n) { "cnt = $cnt" }
        check(ord.contentHashCode() == expectedOrdHash) { "hash = ${ord.contentHashCode()}" }
        check(f.all { it })
    }
}