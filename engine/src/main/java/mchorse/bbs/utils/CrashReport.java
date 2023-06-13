package mchorse.bbs.utils;

import mchorse.bbs.graphics.window.Window;
import mchorse.bbs.ui.utils.UIUtils;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CrashReport
{
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss").withZone(ZoneId.systemDefault());

    /**
     * Write given exception to a crash log file with current time name.
     */
    public static Pair<File, String> writeCrashReport(File crashes, Exception e, String header)
    {
        crashes.mkdirs();

        File file = new File(crashes, "crash-" + FORMATTER.format(Instant.now()) + ".log");
        String content = "";

        try
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);

            sw.write(header);
            sw.write("\n\n");
            e.printStackTrace(pw);

            content = sw.toString();

            IOUtils.writeText(file, content);
        }
        catch (Exception exception)
        {
            /* Hopefully that wouldn't happen... */
            exception.printStackTrace();
        }

        return new Pair(file, content);
    }

    /**
     * Show error dialogue with options to open crash folder and
     * copy to buffer.
     */
    public static void showDialogue(Pair<File, String> crash, String message)
    {
        String title = "Crash!";
        String[] buttons = { "Open crashes folder...", "Copy crash to buffer...", "Close" };
        int response = JOptionPane.showOptionDialog(null, message, title, JOptionPane.YES_NO_OPTION, 0, null, buttons, buttons[0]);

        if (response == 0)
        {
            UIUtils.openFolder(crash.a.getParentFile());
        }
        else if (response == 1)
        {
            Window.setClipboard(crash.b);
        }
    }
}