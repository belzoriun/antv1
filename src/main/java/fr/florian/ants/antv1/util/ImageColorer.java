package fr.florian.ants.antv1.util;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class ImageColorer {

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
}
