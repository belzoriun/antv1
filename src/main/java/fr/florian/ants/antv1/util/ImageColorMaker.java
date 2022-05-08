package fr.florian.ants.antv1.util;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Class used to manager image coloration
 */
public class ImageColorMaker {

    /**
     * Color an image replacing red values by the given color, on the given image
     * @param i The image to transform
     * @param c The color to replace red gradients with
     * @return The new image
     */
    public static Image colorAntImage(Image i, Color c)
    {
        WritableImage res = new WritableImage((int) i.getWidth(), (int) i.getHeight());
        PixelReader reader = i.getPixelReader();
        for(int x = 0; x<i.getWidth(); x++)
        {
            for(int y = 0; y<i.getHeight(); y++)
            {
                double redColor = Math.round(reader.getColor(x, y).getRed() * 100000.0) / 100000.0;
                double redColorA1 = Math.round(84/255.0 * 100000.0) / 100000.0;
                double redColorA2 = Math.round(125/255.0 * 100000.0) / 100000.0;
                double redColorA3 = Math.round(161/255.0 * 100000.0) / 100000.0;
                if(redColor == redColorA1)
                {
                    res.getPixelWriter().setColor(x, y, c.darker());
                }
                else if(redColor == redColorA2)
                {
                    res.getPixelWriter().setColor(x, y, c);
                }
                else if(redColor == redColorA3)
                {
                    res.getPixelWriter().setColor(x, y, c.brighter());
                }
                else
                {
                    res.getPixelWriter().setColor(x, y, reader.getColor(x, y));
                }
            }
        }
        return res;
    }

    /**
     * Change alpha value of non fully-transparent pixels of the given image to the given value
     * @param i The image to transform
     * @param alpha The alpha value to put
     * @return The new image
     */
    public static Image fade(Image i, double alpha)
    {
        WritableImage res = new WritableImage((int) i.getWidth(), (int) i.getHeight());
        PixelReader reader = i.getPixelReader();
        for(int x = 0; x<i.getWidth(); x++)
        {
            for(int y = 0; y<i.getHeight(); y++)
            {
                Color base = reader.getColor(x, y);
                if(base.getOpacity() > 0) {
                    Color c = new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha);
                    res.getPixelWriter().setColor(x, y, c);
                }
            }
        }
        return res;
    }
}
