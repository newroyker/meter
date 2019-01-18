# meter
Custom metrics source example.

There are two ways to register a `Source`:
* during `start` let `MetricsSystem` use `Utils` to load the class and register a `newInstance`.
* otherwise, `registerSource` an instance to `MetricsSystem`.

# demo
`sbt run` output will contain:
```text
-- Counters --------------------------------------------------------------------
...
local-1548106767177.driver.MySource.fooCounter
             count = 100
            
-- Histograms ------------------------------------------------------------------
...
local-1548106767177.driver.MySource.fooHistory
             count = 100
               min = 1
               max = 1
              mean = 1.00
            stddev = 0.00
            median = 1.00
              75% <= 1.00
              95% <= 1.00
              98% <= 1.00
              99% <= 1.00
            99.9% <= 1.00
             
```
