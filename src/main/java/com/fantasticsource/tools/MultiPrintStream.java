package com.fantasticsource.tools;

import javax.annotation.Nonnull;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

public class MultiPrintStream extends PrintStream
{
    public ArrayList<PrintStream> streams = new ArrayList<>();

    public MultiPrintStream(PrintStream... printStreams)
    {
        super((OutputStream) null);
        streams.addAll(Arrays.asList(printStreams));
    }

    @Override
    public void flush()
    {
        for (PrintStream stream : streams) stream.flush();
    }

    @Override
    public void close()
    {
        for (PrintStream stream : streams) stream.close();
    }

    @Override
    public boolean checkError()
    {
        boolean error = super.checkError();
        for (PrintStream stream : streams) error |= stream.checkError();
        return error;
    }

    @Override
    public void write(int b)
    {
        for (PrintStream stream : streams) stream.write(b);
    }

    @Override
    public void write(@Nonnull byte[] buf, int off, int len)
    {
        for (PrintStream stream : streams) stream.write(buf, off, len);
    }

    @Override
    public void print(boolean b)
    {
        for (PrintStream stream : streams) stream.println(b);
    }

    @Override
    public void print(char c)
    {
        for (PrintStream stream : streams) stream.println(c);
    }

    @Override
    public void print(int i)
    {
        for (PrintStream stream : streams) stream.println(i);
    }

    @Override
    public void print(long l)
    {
        for (PrintStream stream : streams) stream.println(l);
    }

    @Override
    public void print(float f)
    {
        for (PrintStream stream : streams) stream.println(f);
    }

    @Override
    public void print(double d)
    {
        for (PrintStream stream : streams) stream.println(d);
    }

    @Override
    public void print(@Nonnull char[] s)
    {
        for (PrintStream stream : streams) stream.print(s);
    }

    @Override
    public void print(String s)
    {
        for (PrintStream stream : streams) stream.print(s);
    }

    @Override
    public void print(Object obj)
    {
        for (PrintStream stream : streams) stream.print(obj);
    }

    @Override
    public void println()
    {
        for (PrintStream stream : streams) stream.println();
    }

    @Override
    public void println(boolean x)
    {
        for (PrintStream stream : streams) stream.println(x);
    }

    @Override
    public void println(char x)
    {
        for (PrintStream stream : streams) stream.println(x);
    }

    @Override
    public void println(int x)
    {
        for (PrintStream stream : streams) stream.println(x);
    }

    @Override
    public void println(long x)
    {
        for (PrintStream stream : streams) stream.println(x);
    }

    @Override
    public void println(float x)
    {
        for (PrintStream stream : streams) stream.println(x);
    }

    @Override
    public void println(double x)
    {
        for (PrintStream stream : streams) stream.println(x);
    }

    @Override
    public void println(@Nonnull char[] x)
    {
        for (PrintStream stream : streams) stream.println(x);
    }

    @Override
    public void println(String x)
    {
        for (PrintStream stream : streams) stream.println(x);
    }

    @Override
    public void println(Object x)
    {
        for (PrintStream stream : streams) stream.println(x);
    }

    @Override
    public PrintStream printf(@Nonnull String format, Object... args)
    {
        for (PrintStream stream : streams) stream.printf(format, args);
        return this;
    }

    @Override
    public PrintStream printf(Locale l, @Nonnull String format, Object... args)
    {
        for (PrintStream stream : streams) stream.printf(l, format, args);
        return this;
    }

    @Override
    public PrintStream format(@Nonnull String format, Object... args)
    {
        for (PrintStream stream : streams) stream.format(format, args);
        return this;
    }

    @Override
    public PrintStream format(Locale l, @Nonnull String format, Object... args)
    {
        for (PrintStream stream : streams) stream.format(l, format, args);
        return this;
    }

    @Override
    public PrintStream append(CharSequence csq)
    {
        for (PrintStream stream : streams) stream.append(csq);
        return this;
    }

    @Override
    public PrintStream append(CharSequence csq, int start, int end)
    {
        for (PrintStream stream : streams) stream.append(csq, start, end);
        return this;
    }

    @Override
    public PrintStream append(char c)
    {
        for (PrintStream stream : streams) stream.append(c);
        return this;
    }
}
