package com.beidou.springsecurity.utils.kaptcha;

import com.google.code.kaptcha.BackgroundProducer;
import com.google.code.kaptcha.util.Configurable;

import java.awt.*;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class KaptchaBackground extends Configurable implements BackgroundProducer {

    @Override
    public BufferedImage addBackground(BufferedImage baseImage) {
        Color colorFrom = this.getConfig().getBackgroundColorFrom();
        Color colorTo = this.getConfig().getBackgroundColorTo();
        int width = baseImage.getWidth();
        int height = baseImage.getHeight();
        BufferedImage imageWithBackground = new BufferedImage(width, height, Transparency.TRANSLUCENT);
        Graphics2D graph = (Graphics2D)imageWithBackground.getGraphics();
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        hints.add(new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY));
        hints.add(new RenderingHints(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY));
        hints.add(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        graph.setRenderingHints(hints);
        GradientPaint paint = new GradientPaint(0.0F, 0.0F, colorFrom, (float)width, (float)height, colorTo);
        graph.setPaint(paint);
        graph.fill(new Double(0.0D, 0.0D, (double)width, (double)height));
        graph.drawImage(baseImage, 0, 0, (ImageObserver)null);
        return imageWithBackground;
    }
}

