# Kotlin DFS Benchmark

Benchmarking various ways to run DFS on a graph in Kotlin with n=200_000 vertices and m=200_000 edges.

```text
# JMH version: 1.36
# VM version: JDK 17.0.6, Java HotSpot(TM) 64-Bit Server VM, 17.0.6+9-LTS-190
# VM invoker: C:\Java\jdk-17\bin\java.exe
# VM options: -Xss64m

Benchmark                                          Mode  Cnt   Score   Error  Units | Algo | Data Structure
            
FastListClassGraphBenchmark.testArrayStack         avgt   20  14.456 ± 0.327  ms/op |   0  | +116%
FastListClassGraphBenchmark.testDRF                avgt   20  15.625 ± 0.140  ms/op | + 8% |
FastListClassGraphBenchmark.testRecursive          avgt   20  13.677 ± 0.230  ms/op | - 5% | 
        
IntListClassGraphBenchmark.testArrayStack          avgt   20   8.854 ± 0.175  ms/op |   0  | + 32%
IntListClassGraphBenchmark.testDRF                 avgt   20  11.057 ± 0.148  ms/op | +24% |
IntListClassGraphBenchmark.testRecursive           avgt   20   8.424 ± 0.159  ms/op | - 5% |
  
IntListInterfaceImplGraphBenchmark.testArrayStack  avgt   20  10.244 ± 0.249  ms/op |   0  | + 53%
IntListInterfaceImplGraphBenchmark.testDRF         avgt   20  12.356 ± 0.193  ms/op | +21% |
IntListInterfaceImplGraphBenchmark.testRecursive   avgt   20   9.239 ± 0.104  ms/op | -10% |
  
NaiveArrayListGraphBenchmark.testArrayStack        avgt   20  15.459 ± 0.218  ms/op |   0  | +130% 
NaiveArrayListGraphBenchmark.testDRF               avgt   20  18.125 ± 0.357  ms/op | +17% |
NaiveArrayListGraphBenchmark.testRecursive         avgt   20  16.858 ± 0.323  ms/op | + 9% |
        
SpecialGraphBenchmark.testArrayStack               avgt   20   6.702 ± 0.048  ms/op |   0  |    0
SpecialGraphBenchmark.testDRF                      avgt   20  10.522 ± 0.117  ms/op | +56% |
SpecialGraphBenchmark.testRecursive                avgt   20   7.562 ± 0.063  ms/op | +12% |
```