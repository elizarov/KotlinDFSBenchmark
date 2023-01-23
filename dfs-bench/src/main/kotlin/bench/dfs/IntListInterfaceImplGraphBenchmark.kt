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
open class IntListInterfaceImplGraphBenchmark : Base() {
    val g: Array<MutableList<Int>> = Array<MutableList<Int>>(n) { IntListInterfaceImpl() }
    val sv = IntArray(n)
    val sj = IntArray(n)
    var st = 0

    init {
        val rnd = Random(1)
        repeat(m) {
            val v = rnd.nextInt(n)
            val u = rnd.nextInt(n)
            g[v] += u
            g[u] += v
        }
        for (gl in g) gl.reverse() // for consistency with CompactGraphBenchmark
    }

    @Benchmark
    fun testRecursive() {
        fun dfs(v: Int) {
            f[v] = true
            for (u in g[v]) {
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
            for (u in g[v]) {
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
                val gl = g[v]
                while (sj[st] < gl.size) {
                    val u = gl[sj[st]++]
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
        sj[st] = 0
        f[v] = true
    }
}

class IntListInterfaceImpl : AbstractMutableList<Int>() {
    override var size = 0
        private set
    private var a = IntArray(4)

    override fun add(element: Int): Boolean {
        if (size >= a.size) a = a.copyOf(2 * size)
        a[size++] = element
        return true
    }

    override operator fun get(index: Int): Int {
        check(index in 0 until size)
        return a[index]
    }

    override fun set(index: Int, element: Int): Int {
        check(index in 0 until size)
        return a[index].also { a[index] = element }
    }

    override fun add(index: Int, element: Int) {
        TODO("Not yet implemented")
    }

    override fun removeAt(index: Int): Int {
        TODO("Not yet implemented")
    }
}