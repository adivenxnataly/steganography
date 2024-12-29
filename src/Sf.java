import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.*;

public class Sf {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Pilih operasi:");
        System.out.println("1. Sembunyikan pesan");
        System.out.println("2. Ekstrak pesan");
        System.out.print("pilih: ");
        int pilihan = scanner.nextInt();
        scanner.nextLine();

        if (pilihan == 1) {
            System.out.print("Masukkan nama file gambar (PNG): ");
            String inputFile = scanner.nextLine();
            System.out.print("Masukkan pesan: ");
            String message = scanner.nextLine();
            sembunyikanPesan(inputFile, message);
        } else if (pilihan == 2) {
            System.out.print("Masukkan nama file gambar (PNG): ");
            String inputFile = scanner.nextLine();
            ekstrakPesan(inputFile);
        } else {
            System.out.println("Pilihan tidak valid!");
        }
    }

    public static void sembunyikanPesan(String inputFile, String message) {
        try {
            BufferedImage image = ImageIO.read(new File(inputFile));
            int width = image.getWidth();
            int height = image.getHeight();
            String binaryMessage = teksKeBiner(message + "#");

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

            ImageIO.write(image, "png", new File("output.png"));
            System.out.println("Pesan disembunyikan dalam output.png");
        } catch (IOException e) {
            System.out.println("Gagal membaca/menyimpan gambar: " + e.getMessage());
        }
    }

    public static void ekstrakPesan(String inputFile) {
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

            String extractedMessage = binerKeTeks(binaryMessage.toString()).split("#")[0];
            System.out.println("Pesan yang diekstrak: " + extractedMessage);
        } catch (IOException e) {
            System.out.println("Gagal membaca gambar: " + e.getMessage());
        }
    }

    public static String teksKeBiner(String teks) {
        StringBuilder biner = new StringBuilder();
        for (char c : teks.toCharArray()) {
            biner.append(String.format("%8s", Integer.toBinaryString(c)).replace(' ', '0'));
        }
        return biner.toString();
    }

    public static String binerKeTeks(String biner) {
        StringBuilder teks = new StringBuilder();
        for (int i = 0; i < biner.length(); i += 8) {
            String byteBiner = biner.substring(i, Math.min(i + 8, biner.length()));
            if (byteBiner.length() == 8) {
                int ascii = Integer.parseInt(byteBiner, 2);
                if (ascii >= 32 && ascii <= 126) {
                    char c = (char) ascii;
                    teks.append(c);
                }
            }
        }
        return teks.toString();
    }
}
