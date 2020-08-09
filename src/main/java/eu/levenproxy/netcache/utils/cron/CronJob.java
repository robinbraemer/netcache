package eu.levenproxy.netcache.utils.cron;

public abstract class CronJob {

    private final String name;
    private final int tickRate;
    private final boolean active;
    private int tickCount;

    public CronJob(String name, int tickRate, boolean active) {
        this.name = name;
        this.tickRate = tickRate;
        this.active = active;
        this.tickCount = 1;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public void tick() {
        if(isActive()) {
            if (this.tickCount <= tickRate) {
                this.tickCount += 1;
            } else {
                this.tickCount = 0;
                onTickRateFired();
            }
        }
    }

    public abstract void onTickRateFired();
}
