package io.logmatic;

import java.util.Date;

import io.logmatic.asynclogger.Logger;
import io.logmatic.asynclogger.LoggerBuilder;


/**
 * Created by gpolaert on 4/13/16.
 */
public class Demo {


    public static void main(String[] args) throws InterruptedException {

        long pong = new Date().getTime();

        Re r = new Re();


        Thread t = new Thread(r);
        t.start();
        Thread.sleep(100);
        r.log.destroy();

        long ping = new Date().getTime();

        long ratio = (60000 * r.nbe / (ping - pong))/ 1000;

        System.out.println("Ratio: " + ratio + "/min");
    }
}


class Re implements Runnable {


    public Logger log = new LoggerBuilder()
            .init("iGNxbieiR8uKGMJogoZYWA")
            .build();

    public Integer nbe = 0;


    @Override
    public void run() {
        log.disableLegacyLogger();
        log.addField("version", 1.337);


        for (int i =0; i < 10;i++) {
            log.d("my-tag", "hello from Android-v2 from http (" + nbe++ + ")");
        }
    }


};
