package ch.juventus.yatzi.config;

import org.aeonbits.owner.Config;

@Config.Sources({"classpath:config/application.properties" })
public interface ApplicationConfig extends Config {

    @DefaultValue("500")
    @Key("queue.read.pause.length")
    int queuePauseLength();

}
