package ua.dean.cron;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ua.dean.service.CheckService;

import java.io.IOException;

@Component
public class CheckServiceCron {

    @Autowired
    private CheckService checkService;

//    @Scheduled(cron = "* */1 * * * *")
    @Scheduled(cron = "*/10 * * * * *")
    public void check() {
        try {
            checkService.doCheck();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
