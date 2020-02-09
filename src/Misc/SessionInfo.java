package Misc;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class SessionInfo {

    private static String username;

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        SessionInfo.username = username;
    }

    public static BufferedImage BytetoBuff(byte[] img) {
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(img);
            BufferedImage image = ImageIO.read(bis);
            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
