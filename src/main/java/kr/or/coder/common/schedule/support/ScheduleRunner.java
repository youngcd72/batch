package kr.or.coder.common.schedule.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationContext;

import kr.or.coder.common.schedule.service.ScheduleService;

public class ScheduleRunner implements CommandLineRunner {
    
	private static final Logger logger = LoggerFactory.getLogger(ScheduleRunner.class);
	
	@Autowired
	private ApplicationContext context;
	
	@Autowired
	private ScheduleService schedulreService;
	
	@Override
	public void run(String... args) throws Exception {

		/* Runner execute */
		schedulreService.loadQuartzScheduler(context);
	}

}
