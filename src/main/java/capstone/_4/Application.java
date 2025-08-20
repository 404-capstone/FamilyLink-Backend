package capstone._4;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;


@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableScheduling
@EnableAsync
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean(name= "taskExecutor")
	public Executor taskExecutor() {
		ThreadPoolTaskExecutor ex= new ThreadPoolTaskExecutor();
		ex.setCorePoolSize(4);
		ex.setMaxPoolSize(8);
		ex.setQueueCapacity(200);
		ex.setThreadNamePrefix("async-");
		ex.initialize();
		return ex;
	}

	@Bean
	public ThreadPoolTaskScheduler taskScheduler() {
		ThreadPoolTaskScheduler sch = new ThreadPoolTaskScheduler();
		sch.setPoolSize(2);
		sch.setThreadNamePrefix("sched-");
		return sch;
	}
}

