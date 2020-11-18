package kr.or.coder.batch.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MainScheduler {

    private Logger logger = LoggerFactory.getLogger(getClass());
    
    public static void main(String[] args) {

        try {
            
            // specify the job' s details..
            JobDetail job = JobBuilder.newJob(TestJob.class)
                                      .withIdentity("testJob")
                                      .build();
    
            // specify the running period of the job
            Trigger trigger = TriggerBuilder.newTrigger()
                                            .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                                                                               .withIntervalInSeconds(30)
                                                                               .repeatForever())
                                             .build();
    
            //schedule the job
            SchedulerFactory schFactory = new StdSchedulerFactory();
            Scheduler sch = schFactory.getScheduler();
            sch.start();
            sch.scheduleJob(job, trigger);
    
        } catch (SchedulerException ex) {

            
        }
    }
}