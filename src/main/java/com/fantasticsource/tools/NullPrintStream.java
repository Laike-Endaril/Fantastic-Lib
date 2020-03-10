package com.fantasticsource.tools;

import javax.annotation.Nonnull;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Locale;

public class NullPrintStream extends PrintStream
{
    public NullPrintStream()
    {
        super((OutputStream) null);
    }

    @Override
    public void flush()
    {
    }

    @Override
    public void close()
    {
    }

    @Override
    public boolean checkError()
    {
        return false;
    }

    @Override
    public void write(int b)
    {
    }

    @Override
    public void write(@Nonnull byte[] buf, int off, int len)
    {
    }

    @Override
    public void print(boolean b)
    {
    }

    @Override
    public void print(char c)
    {
    }

    @Override
    public void print(int i)
    {
    }

    @Override
    public void print(long l)
    {
    }

    @Override
    public void print(float f)
    {
    }

    @Override
    public void print(double d)
    {
    }

    @Override
    public void print(@Nonnull char[] s)
    {
    }

    @Override
    public void print(String s)
    {
    }

    @Override
    public void print(Object obj)
    {
    }

    @Override
    public void println()
    {
    }

    @Override
    public void println(boolean x)
    {
    }

    @Override
    public void println(char x)
    {
    }

    @Override
    public void println(int x)
    {
    }

    @Override
    public void println(long x)
    {
    }

    @Override
    public void println(float x)
    {
    }

    @Override
    public void println(double x)
    {
    }

    @Override
    public void println(@Nonnull char[] x)
    {
    }

    @Override
    public void println(String x)
    {
    }

    @Override
    public void println(Object x)
    {
    }

    @Override
    public PrintStream printf(@Nonnull String format, Object... args)
    {
        return this;
    }

    @Override
    public PrintStream printf(Locale l, @Nonnull String format, Object... args)
    {
        return this;
    }

    @Override
    public PrintStream format(@Nonnull String format, Object... args)
    {
        return this;
    }

    @Override
    public PrintStream format(Locale l, @Nonnull String format, Object... args)
    {
        return this;
    }

    @Override
    public PrintStream append(CharSequence csq)
    {
        return this;
    }

    @Override
    public PrintStream append(CharSequence csq, int start, int end)
    {
        return this;
    }

    @Override
    public PrintStream append(char c)
    {
        return this;
    }
}
