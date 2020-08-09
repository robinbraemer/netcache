package eu.levenproxy.netcache.utils.benchmark;

public class Benchmark {

    private long startTime;
    private long endTime;

    public Benchmark() {
        this.startTime = -1;
        this.endTime = -1;
    }

    public Benchmark record() {
        this.startTime = System.currentTimeMillis();
        return this;
    }

    public Benchmark stop() {
        this.endTime = System.currentTimeMillis();
        return this;
    }

    public long getValue() {
        return endTime - startTime;
    }

    public String asSeconds() {
        return (endTime - startTime) / 1000 + "s";
    }

    public String asMillis() {
        return (endTime - startTime) + "ms";
    }
}
