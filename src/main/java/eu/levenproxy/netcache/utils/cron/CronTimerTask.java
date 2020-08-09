package eu.levenproxy.netcache.utils.cron;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;

public class CronTimerTask {

    private final ExecutorService executor;
    private final Thread thread;
    private final Timer timer;
    private final HashMap<String, CronJob> cronJobs;

    public CronTimerTask(ExecutorService executorService) {
        this.executor = executorService;
        this.cronJobs = new HashMap<>();
        this.timer = new Timer();
        this.thread = new Thread(() ->
                this.timer.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        executor.execute(() -> cronJobs.values().forEach(CronJob::tick));
                    }
                }, 0, 1000));
    }

    public void start() {
        this.thread.start();
    }

    public void stop() {
        this.timer.cancel();
        this.thread.interrupt();
        this.executor.shutdown();
    }

    public void registerJob(CronJob cronJob) {
        this.cronJobs.put(cronJob.getName(), cronJob);
    }

}
