package com.example.filmappjavabackend.services;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
public class ImageResizer {

    /**
     * Resizes an image to a absolute width and height (the image may not be
     * proportional)
     *
     * @param scaledWidth     absolute width in pixels
     * @param scaledHeight    absolute height in pixels
     * @throws IOException
     */
    public static BufferedImage resize(byte[] fileData, int scaledWidth, int scaledHeight)
            throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(fileData);
        BufferedImage inputImage = ImageIO.read(in);

        // creates output image
        BufferedImage outputImage = new BufferedImage(scaledWidth,
                scaledHeight, inputImage.getType());

        // scales the input image to the output image
        Graphics2D g2d = outputImage.createGraphics();
        g2d.drawImage(inputImage, 0, 0, scaledWidth, scaledHeight, null);
        g2d.dispose();

        return outputImage;
    }

    public static BufferedImage resize(byte[] fileData, int width) throws IOException {
        ByteArrayInputStream in = new ByteArrayInputStream(fileData);
        BufferedImage inputImage = ImageIO.read(in);
        
        // float ratio = (float) inputImage.getWidth() / (float) inputImage.getHeight();
        // int scaledHeight = (int) ((float) width / ratio);
        
        float ratio = 9.0f / 16.0f;
        int scaledWidth = (int) (width);
        int scaledHeight = (int) ((float) width * ratio);
        return resize(fileData, scaledWidth, scaledHeight);
    }
}