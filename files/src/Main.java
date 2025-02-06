import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("chose operation:");
        System.out.println("1. Hide Message");
        System.out.println("2. Extract Message");
        System.out.print("option: ");
        int option = scanner.nextInt();
        scanner.nextLine();

        if (option == 1) {
            System.out.print("Enter image name (PNG): ");
            String inputFile = scanner.nextLine();
            System.out.print("Enter message: ");
            String message = scanner.nextLine();
            hideMessage(inputFile, message);
        } else if (option == 2) {
            System.out.print("Enter image name (PNG): ");
            String inputFile = scanner.nextLine();
            extractMessage(inputFile);
        } else {
            System.out.println("failed, not valid option");
        }
    }

    public static void hideMessage(String inputFile, String message) {
        try {
            BufferedImage image = ImageIO.read(new File(inputFile));
            int width = image.getWidth();
            int height = image.getHeight();
            String binaryMessage = text2Binary(message + "#");

            int index = 0;
            for (int y = 0; y < height && index < binaryMessage.length(); y++) {
                for (int x = 0; x < width && index < binaryMessage.length(); x++) {
                    int pixel = image.getRGB(x, y);
                    int red = (pixel >> 16) & 0xFF;

                    if (binaryMessage.charAt(index) == '1') {
                        red |= 1;
                    } else {
                        red &= ~1;
                    }

                    image.setRGB(x, y, (red << 16) | ((pixel >> 8) & 0xFF) | (pixel & 0xFF));
                    index++;
                }
            }

            ImageIO.write(image, "png", new File("Stg.png"));
            System.out.println("Message hided in Stg.png");
        } catch (IOException e) {
            System.out.println("failed to read/save to Stg.png: " + e.getMessage());
        }
    }

    public static void extractMessage(String inputFile) {
        try {
            BufferedImage image = ImageIO.read(new File(inputFile));
            int width = image.getWidth();
            int height = image.getHeight();

            StringBuilder binaryMessage = new StringBuilder();
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    int pixel = image.getRGB(x, y);
                    int red = (pixel >> 16) & 0xFF;
                    binaryMessage.append(red & 1);
                }
            }

            String extractedMessage = binary2Text(binaryMessage.toString()).split("#")[0];
            System.out.println("extracted message: " + extractedMessage);
        } catch (IOException e) {
            System.out.println("failed to read image: " + e.getMessage());
        }
    }

    public static String text2Binary(String text) {
        StringBuilder binary = new StringBuilder();
        for (char c : text.toCharArray()) {
            binary.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        return binary.toString();
    }

    public static String binary2Text(String binary) {
        StringBuilder text = new StringBuilder();
        for (int i = 0; i < binary.length(); i += 8) {
            String bytebinary = binary.substring(i, Math.min(i + 8, binary.length()));
            if (bytebinary.length() == 8) {
                int ascii = Integer.parseInt(bytebinary, 2);
                if (ascii >= 32 && ascii <= 126) {
                    char c = (char) ascii;
                    text.append(c);
                }
            }
        }
        return text.toString();
    }
}