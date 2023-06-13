package mchorse.bbs.utils.recording;

import mchorse.bbs.BBS;
import mchorse.bbs.audio.SoundPlayer;
import mchorse.bbs.core.Engine;
import mchorse.bbs.resources.Link;
import mchorse.bbs.ui.utils.UIUtils;
import mchorse.bbs.utils.PNGEncoder;
import mchorse.bbs.utils.resources.Pixels;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Screenshot recorder
 *
 * This class is responsible for taking a screenshot and saving it in
 * the screenshot's directory
 */
public class ScreenshotRecorder
{
    public File screenshots;
    public Engine engine;

    public boolean take;
    public boolean toBuffer;

    public ScreenshotRecorder(File screenshots, Engine engine)
    {
        this.screenshots = screenshots;
        this.screenshots.mkdirs();

        this.engine = engine;
    }

    /**
     * Mark recorder for screenshot taking
     */
    public void take(boolean toBuffer)
    {
        this.take = true;
        this.toBuffer = toBuffer;
    }

    /**
     * Record the screenshot
     */
    public void recordFrame(int width, int height)
    {
        if (this.take)
        {
            this.take = false;
            this.takeScreenshot(this.toBuffer ? null : this.getScreenshotFile(), width, height);
        }
    }

    /**
     * Take a screenshot and save it to designated file
     */
    private void takeScreenshot(File output, int width, int height)
    {
        FloatBuffer pixelData = BufferUtils.createFloatBuffer(width * height * 4);

        GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_FLOAT, pixelData);
        pixelData.rewind();

        /* Pixel data must be converted first from floats to 32 bit hex */
        int[] pixels = new int[width * height];

        for (int y = 0; y < height; ++y)
        {
            for (int x = 0; x < width; ++x)
            {
                float r = pixelData.get() * 255;
                float g = pixelData.get() * 255;
                float b = pixelData.get() * 255;
                float a = pixelData.get() * 255;
                int i = ((height - 1) - y) * width + x;

                pixels[i] = ((int) a << 24) + ((int) r << 16) + ((int) g << 8) + (int) b;
            }
        }

        ScreenshotRunner runner = new ScreenshotRunner(width, height, pixels, output);

        new Thread(runner).start();
    }

    /**
     * Get screenshot file for a screenshot
     */
    private File getScreenshotFile()
    {
        String fileName = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());

        return new File(this.screenshots, fileName + ".png");
    }

    /**
     * Screenshot runner
     * <p>
     * This dude right here is responsible for saving given RGB data to
     * a PNG file to given designated file.
     */
    public static class ScreenshotRunner implements Runnable, ClipboardOwner
    {
        public int width;
        public int height;

        public int[] data;
        public File destination;

        public ScreenshotRunner(int width, int height, int[] data, File destination)
        {
            this.width = width;
            this.height = height;
            this.data = data;
            this.destination = destination;
        }

        @Override
        public void lostOwnership(Clipboard clipboard, Transferable contents)
        {}

        @Override
        public void run()
        {
            try
            {
                if (this.destination == null)
                {
                    /* Windows only */
                    BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);

                    image.setRGB(0, 0, this.width, this.height, this.data, 0, this.width);
                    Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new TransferableImage(image), this);
                }
                else
                {
                    Pixels pixels = Pixels.fromIntArray(this.width, this.height, this.data);

                    PNGEncoder.writeToFile(pixels, this.destination);

                    pixels.delete();
                }

                SoundPlayer player = BBS.getSounds().play(Link.assets("sounds/snap.ogg"));

                if (player != null)
                {
                    player.setRelative(true);
                    player.setPosition(0, 0, 0);
                    player.setVolume(1F);
                    player.setPitch(1F);
                    player.play();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    public static class TransferableImage implements Transferable
    {
        private Image image;
        private DataFlavor flavor = DataFlavor.imageFlavor;

        public TransferableImage(Image image)
        {
            this.image = image;
        }

        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException
        {
            if (this.flavor.equals(flavor))
            {
                return this.image;
            }

            throw new UnsupportedFlavorException(flavor);
        }

        public DataFlavor[] getTransferDataFlavors()
        {
            return new DataFlavor[]{this.flavor};
        }

        public boolean isDataFlavorSupported(DataFlavor flavor)
        {
            return this.flavor.equals(flavor);
        }
    }
}