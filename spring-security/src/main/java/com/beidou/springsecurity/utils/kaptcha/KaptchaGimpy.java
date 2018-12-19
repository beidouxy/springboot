package com.beidou.springsecurity.utils.kaptcha;

import com.google.code.kaptcha.GimpyEngine;
import com.google.code.kaptcha.NoiseProducer;
import com.google.code.kaptcha.util.Configurable;
import com.jhlabs.image.RippleFilter;
import com.jhlabs.image.WaterFilter;
import org.springframework.context.annotation.Configuration;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class KaptchaGimpy extends Configurable implements GimpyEngine {

    @Override
    public BufferedImage getDistortedImage(BufferedImage baseImage) {
        NoiseProducer noiseProducer = this.getConfig().getNoiseImpl();
        BufferedImage distortedImage = new BufferedImage(baseImage.getWidth(), baseImage.getHeight(), 2);
        Graphics2D graph = (Graphics2D)distortedImage.getGraphics();

        WaterFilter waterFilter = new WaterFilter();
        waterFilter.setAmplitude(1F);
        waterFilter.setPhase(10.0F);
        waterFilter.setWavelength(0.1F);

        RippleFilter rippleFilter = new RippleFilter();
        rippleFilter.setXAmplitude(2.6F);
        rippleFilter.setYAmplitude(1.7F);
        rippleFilter.setXWavelength(15.0F);
        rippleFilter.setYWavelength(5.0F);
        rippleFilter.setWaveType(0);
        rippleFilter.setEdgeAction(1);
        BufferedImage effectImage = rippleFilter.filter(baseImage, (BufferedImage)null);
        effectImage = waterFilter.filter(effectImage, (BufferedImage)null);
        graph.drawImage(effectImage, 0, 0, (Color)null, (ImageObserver)null);
        graph.dispose();
        noiseProducer.makeNoise(distortedImage, 0.1F, 0.1F, 0.25F, 0.25F);
        noiseProducer.makeNoise(distortedImage, 0.1F, 0.25F, 0.5F, 0.9F);
        return distortedImage;
    }
}
