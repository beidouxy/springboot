package com.beidou.springsecurity.config;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {

    @Value(value = "${kaptcha.borderColor}")
    private String borderColor;

    @Value(value = "${kaptcha.fontColor}")
    private String fontColor;

    @Value(value = "${kaptcha.fontSize}")
    private String fontSize;

    @Value(value = "${kaptcha.width}")
    private String width;

    @Value(value = "${kaptcha.height}")
    private String height;

    @Value(value = "${kaptcha.length}")
    private String length;

    @Value(value = "${kaptcha.font}")
    private String font;

    @Value(value = "${kaptcha.text}")
    private String text;

    @Value(value = "${kaptcha.noise}")
    private String noise;

    @Bean
    public DefaultKaptcha getDefaultKaptcha() {
        DefaultKaptcha defaultKaptcha = new DefaultKaptcha();
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border.color", borderColor);
        properties.setProperty("kaptcha.border", "no");
        properties.setProperty("kaptcha.textproducer.font.color", fontColor);
        properties.setProperty("kaptcha.image.width", width);
        properties.setProperty("kaptcha.image.height", height);
        properties.setProperty("kaptcha.textproducer.font.size", fontSize);
        properties.setProperty("kaptcha.textproducer.char.length", length);
        properties.setProperty("kaptcha.textproducer.font.names", font);
        properties.setProperty("kaptcha.textproducer.char.string", text);
        properties.setProperty("kaptcha.noise.impl", noise);
        properties.setProperty("kaptcha.obscurificator.impl", "com.beidou.springsecurity.utils.kaptcha.KaptchaGimpy");
        properties.setProperty("kaptcha.background.impl", "com.beidou.springsecurity.utils.kaptcha.KaptchaBackground");
        properties.setProperty("kaptcha.background.clear.from", "225,255,255,0");
        properties.setProperty("kaptcha.background.clear.to", "255,255,255,0");
        properties.setProperty("kaptcha.textproducer.char.space", "5");
        Config config = new Config(properties);
        defaultKaptcha.setConfig(config);
        return defaultKaptcha;
    }
}