package eu.levenproxy.netcache.utils.benchmark;

import java.util.HashSet;

public class BenchmarkAverage {

    private HashSet<Benchmark> set;

    public BenchmarkAverage() {
        this.set = new HashSet<>();
    }

    public BenchmarkAverage add(Benchmark benchmark) {
        this.set.add(benchmark);
        return this;
    }

    public String averageAsMillis() {
        if (!this.set.isEmpty()) {
            long totalValue = this.set.stream().mapToLong(Benchmark::getValue).sum();
            return totalValue / this.set.size() + "ms";
        }
        return "-1s";
    }

    public String averageAsSeconds() {
        if (!this.set.isEmpty()) {
            long totalValue = this.set.stream().mapToLong(Benchmark::getValue).sum();
            return (totalValue / this.set.size()) / 1000 + "s";
        }
        return "-1s";
    }

}
