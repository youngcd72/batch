package kr.or.coder.common.system.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import kr.or.coder.common.schedule.service.ScheduleService;
import kr.or.coder.common.system.support.GracefulShutdownHandlerWrapper;

@Component
public class GracefulShutdownEventListener implements ApplicationListener<ContextClosedEvent> {

	private static final Logger logger = LoggerFactory.getLogger(GracefulShutdownEventListener.class);
	
    @Autowired
    private GracefulShutdownHandlerWrapper gracefulShutdownHandlerWrapper;
    
    @Autowired
    private ScheduleService schedulreService;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {

        // 이 시점부터 새로운 요청이 거부된다. 클라이언트는 503 Service Unavailable 응답을 수신한다.
        gracefulShutdownHandlerWrapper.getGracefulShutdownHandler().shutdown();
        schedulreService.stopQuartzScheduler();
    }
}
