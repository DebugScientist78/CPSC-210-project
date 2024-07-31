package ui;

import javax.swing.*;
import java.awt.Image;
import java.util.HashMap;
import java.util.Map;

/*
* A mostly static class, no behaviour for creating a AssetHolder object
* Acts as a handler for rendering board game assets.
*/

public class AssetHolder {
    private static Map<Integer, ImageIcon> TileImages;

    //EFFECTS: provides mapping for resolving game tile rendering
    public static void setup() {
        TileImages = new HashMap<>();

        TileImages.put(-1, new ImageIcon("./data/Assets/unopenTile.png"));
        TileImages.put(0, new ImageIcon("./data/Assets/zeroTile.png"));
        TileImages.put(1, new ImageIcon("./data/Assets/one.png"));
        TileImages.put(2, new ImageIcon("./data/Assets/two.png"));
        TileImages.put(3, new ImageIcon("./data/Assets/three.png"));
        TileImages.put(4, new ImageIcon("./data/Assets/four.png"));
        TileImages.put(5, new ImageIcon("./data/Assets/five.png"));
        TileImages.put(6, new ImageIcon("./data/Assets/six.png"));
        TileImages.put(7, new ImageIcon("./data/Assets/seven.png"));
        TileImages.put(9, new ImageIcon("./data/Assets/flag.png"));
        TileImages.put(10, new ImageIcon("./data/Assets/mine.png"));
    }

    //EFFECTS: scales the image size to a given new size, original sizes are 16px squares
    public static ImageIcon scaleImageIcon(ImageIcon icon, int newXsize, int newYsize) {
        Image image = icon.getImage();
        Image newImage = image.getScaledInstance(newXsize, newYsize, Image.SCALE_SMOOTH);
        return new ImageIcon(newImage);

    }

    public static Map<Integer, ImageIcon> getTileImages() {
        return TileImages;
    }
}
