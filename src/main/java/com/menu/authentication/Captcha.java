package com.menu.authentication;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Captcha {
    static final int height = 30, width = 90;
    static private final int maxCaptchaAttempts = 3;

    public static boolean checkCaptcha(Scanner scanner) {
        System.out.println("Confirm you're not a robot");
        String randomText = generatePassword(5);
        System.out.println(Captcha.textToASCII(randomText));
        String command;
        int counter = maxCaptchaAttempts;

        while (counter > 0) {
            counter--;
            command = scanner.nextLine().trim();
            if (command.equals(randomText)) {
                return true;
            } else if (command.equals("quit")) {
                return false;
            } else {
                System.out.println("Remaining chances: " + counter);
                randomText = generatePassword(5);
                System.out.println(Captcha.textToASCII(randomText));
            }
        }
        return false;
    }

    //Creates a strong password
    public static String generatePassword(int PASSWORD_LENGTH) {
        final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String LOWER = UPPER.toLowerCase();
        final String DIGITS = "0123456789";
        final String SPECIAL = "!@#$%^&*()-_=+[]{}|;:,.<>?";
        final String ALL_CHARS = UPPER + LOWER + DIGITS + SPECIAL;
        SecureRandom random = new SecureRandom();
        List<Character> chars = new ArrayList<>();

        // Add characters from each character set
        chars.add(UPPER.charAt(random.nextInt(UPPER.length())));
        chars.add(LOWER.charAt(random.nextInt(LOWER.length())));
        chars.add(DIGITS.charAt(random.nextInt(DIGITS.length())));
        chars.add(SPECIAL.charAt(random.nextInt(SPECIAL.length())));

        // Fill remaining characters randomly
        for (int i = 4; i < PASSWORD_LENGTH; i++) {
            chars.add(ALL_CHARS.charAt(random.nextInt(ALL_CHARS.length())));
        }

        // Shuffle characters and create password string
        Collections.shuffle(chars);
        StringBuilder password = new StringBuilder();
        for (char c : chars) {
            password.append(c);
        }
        return password.toString();
    }

    public static String textToASCII(String txt){
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = buffImg.createGraphics();
        g2d.setFont(new Font("Courier", Font.BOLD, 25));
        g2d.setColor(Color.WHITE);
        g2d.drawString(txt.substring(0, 5), 2, 22);
        g2d.dispose();
        buffImg = blur(buffImg);
        /*
        In case you want to save the .png file, a path should be specified.
        File file = new File(Path);
        ImageIO.write(buffImg, "png", file);
        */
        return imageToText(buffImg);
    }

    private static BufferedImage blur(BufferedImage img) {
        BufferedImage blurImg = new BufferedImage(img.getWidth() - 2, img.getHeight() - 2,
                BufferedImage.TYPE_BYTE_GRAY);
        int pix;
        for (int y = 0; y < blurImg.getHeight(); y++) {
            for (int x = 0; x < blurImg.getWidth(); x++) {
                pix = (4 * (img.getRGB(x + 1, y + 1) & 0xFF)
                        + 2 * (img.getRGB(x + 1, y) & 0xFF)
                        + 2 * (img.getRGB(x + 1, y + 2) & 0xFF)
                        + 2 * (img.getRGB(x, y + 1) & 0xFF)
                        + 2 * (img.getRGB(x + 2, y + 1) & 0xFF)
                        + (img.getRGB(x, y) & 0xFF)
                        + (img.getRGB(x, y + 2) & 0xFF)
                        + (img.getRGB(x + 2, y) & 0xFF)
                        + (img.getRGB(x + 2, y + 2) & 0xFF)) / 16;
                int p = (255 << 24) | (pix << 16) | (pix << 8) | pix;
                blurImg.setRGB(x, y, p);
            }
        }
        return blurImg;
    }

    private static String imageToText(BufferedImage img) {
        int pix, ind;
        double red, blue, green;
        StringBuilder result = new StringBuilder();
        StringBuilder row = new StringBuilder();
        String order = "Ñ@#W$9876543210?!abc;:+=-,._    ";
        order = new StringBuilder(order).reverse().toString();
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                pix = img.getRGB(x, y);
                red = (pix >> 16) & 0xFF;
                green = (pix >> 8) & 0xFF;
                blue = (pix) & 0xFF;
                ind = (int) ((red + blue + green) / (3 * 255) * order.length());
                row.append(order.charAt(Math.min(ind, order.length() - 1)));
            }
            if (!row.toString().trim().isEmpty())
                result.append(row).append("\n");
            row = new StringBuilder();
        }
        return result.toString();
    }
}
