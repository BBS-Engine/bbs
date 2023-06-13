package mchorse.bbs.utils;

import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimePrintStream extends PrintStream
{
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    public TimePrintStream(OutputStream out)
    {
        super(out);
    }

    @Override
    public void println(Object x)
    {
        super.println(this.getPrefix() + x);
    }

    @Override
    public void print(String s)
    {
        super.print(this.getPrefix() + s);
    }

    private String getPrefix()
    {
        return "[" + this.formatter.format(LocalDateTime.now()) + "] ";
    }
}