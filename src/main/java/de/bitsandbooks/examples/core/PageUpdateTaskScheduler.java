package de.bitsandbooks.examples.core;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.IntervalTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

@Component
public class PageUpdateTaskScheduler implements SchedulingConfigurer {

    @Value("${updatechecker.interval}")
    private long INTERVAL;

    @Value("${updatechecker.startupdelay}")
    private long INITIAL_DELAY;

    @Autowired
    private PageUpdateCheckerTask pageUpdateCheckerTask;

    @Autowired
    @Qualifier("pageUpdateCheckerScheduler")
    private TaskScheduler pageUpdateCheckerScheduler;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(pageUpdateCheckerScheduler);

        final IntervalTask task = new IntervalTask(pageUpdateCheckerTask, INTERVAL, INITIAL_DELAY);
        taskRegistrar.addFixedRateTask(task);
    }
}
