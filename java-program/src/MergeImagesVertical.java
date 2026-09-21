import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class MergeImagesVertical {
    public static void main(String[] args) throws Exception {
        BufferedImage img1 = readImageSafely("D:\\File1.jpg");
        BufferedImage img2 = readImageSafely("D:\\File2.jpg");
        BufferedImage img3 = readImageSafely("D:\\File3.jpg");

        int spacing = 20; // space between images in pixels

        int width = Math.max(img1.getWidth(), Math.max(img2.getWidth(), img3.getWidth()));
        int height = img1.getHeight() + img2.getHeight() + img3.getHeight() + (spacing * 2);

        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = combined.getGraphics();

        // Fill background (white) so the gaps aren't black
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        int y = 0;
        g.drawImage(img1, 0, y, null);
        y += img1.getHeight() + spacing;

        g.drawImage(img2, 0, y, null);
        y += img2.getHeight() + spacing;

        g.drawImage(img3, 0, y, null);

        g.dispose();

        ImageIO.write(combined, "jpg", new File("D:\\combined.jpg"));
        System.out.println("Images merged successfully!");
    }

    private static BufferedImage readImageSafely(String path) throws Exception {
        File f = new File(path);
        if (!f.exists()) {
            throw new RuntimeException("File not found: " + f.getAbsolutePath());
        }
        BufferedImage img = ImageIO.read(f);
        if (img == null) {
            throw new RuntimeException("File exists but is not a readable image (bad format?): " + f.getAbsolutePath());
        }
        return img;
    }
}