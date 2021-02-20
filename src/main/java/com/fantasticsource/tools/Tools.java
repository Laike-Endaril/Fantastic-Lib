package com.fantasticsource.tools;

import com.fantasticsource.lwjgl.Quaternion;
import sun.misc.Cleaner;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.InvalidMarkException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;

@SuppressWarnings("unused")
public class Tools
{
    protected static PrintStream out = null, err = null;


    public static Quaternion rotatedQuaternion(Quaternion v, Quaternion axis, double theta)
    {
        double sinThetaDiv2 = TrigLookupTable.TRIG_TABLE_1024.sin(theta * 0.5);
        double cosThetaDiv2 = TrigLookupTable.TRIG_TABLE_1024.cos(theta * 0.5);
        Quaternion q = new Quaternion((float) (sinThetaDiv2 * axis.x), (float) (sinThetaDiv2 * axis.y), (float) (sinThetaDiv2 * axis.z), (float) cosThetaDiv2);
        Quaternion qConjugate = new Quaternion((float) -(sinThetaDiv2 * axis.x), (float) -(sinThetaDiv2 * axis.y), (float) -(sinThetaDiv2 * axis.z), (float) cosThetaDiv2);
        return Quaternion.mul(Quaternion.mul(q, v, null), qConjugate, null);
    }


    public static void heapdump()
    {
        new Heapdump();
    }


    //Thanks to Boann at https://stackoverflow.com/questions/3422673/how-to-evaluate-a-math-expression-given-in-string-form
    //For the original source
    public static double calc(final String str)
    {
        return new Object()
        {
            int pos = -1, ch;

            void nextChar()
            {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean eat(int charToEat)
            {
                while (ch == ' ') nextChar();
                if (ch == charToEat)
                {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse()
            {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            // Grammar:
            // expression = term | expression `+` term | expression `-` term
            // term = factor | term `*` factor | term `/` factor
            // factor = `+` factor | `-` factor | `(` expression `)`
            //        | number | functionName factor | factor `^` factor

            double parseExpression()
            {
                double x = parseTerm();
                for (; ; )
                {
                    if (eat('+')) x += parseTerm(); // addition
                    else if (eat('-')) x -= parseTerm(); // subtraction
                    else return x;
                }
            }

            double parseTerm()
            {
                double x = parseFactor();
                for (; ; )
                {
                    if (eat('*')) x *= parseFactor(); // multiplication
                    else if (eat('/')) x /= parseFactor(); // division
                    else return x;
                }
            }

            double parseFactor()
            {
                if (eat('+')) return parseFactor(); // unary plus
                if (eat('-')) return -parseFactor(); // unary minus

                double x;
                int startPos = this.pos;
                if (eat('('))
                { // parentheses
                    x = parseExpression();
                    eat(')');
                }
                else if ((ch >= '0' && ch <= '9') || ch == '.')
                { // numbers
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                }
                else if (ch >= 'a' && ch <= 'z')
                { // functions
                    while (ch >= 'a' && ch <= 'z') nextChar();
                    String func = str.substring(startPos, this.pos);
                    x = parseFactor();
                    switch (func)
                    {
                        case "sqrt":
                            x = Math.sqrt(x);
                            break;
                        case "sin":
                            x = TrigLookupTable.TRIG_TABLE_1024.sin(degtorad(x));
                            break;
                        case "cos":
                            x = TrigLookupTable.TRIG_TABLE_1024.cos(degtorad(x));
                            break;
                        case "tan":
                            x = TrigLookupTable.TRIG_TABLE_1024.tan(degtorad(x));
                            break;
                        default:
                            throw new RuntimeException("Unknown function: " + func);
                    }
                }
                else
                {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }

                if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

                return x;
            }
        }.parse();
    }


    public static BufferedReader getJarResourceReader(Class classInJar, String resourcePathAndName)
    {
        return new BufferedReader(new InputStreamReader(getJarResourceStream(classInJar, resourcePathAndName)));
    }

    public static InputStream getJarResourceStream(Class classInJar, String resourcePathAndName)
    {
        return classInJar.getClassLoader().getResourceAsStream(resourcePathAndName);
    }


    public static void copyFile(File source, File destination) throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader(source));
        BufferedWriter writer = new BufferedWriter(new FileWriter(destination));
        String line = reader.readLine();
        while (line != null)
        {
            writer.write(line + "\r\n");
            line = reader.readLine();
        }
        writer.close();
        reader.close();
    }


    public static String caselessRegex(String regex)
    {
        StringBuilder result = new StringBuilder();
        for (char c : regex.toLowerCase().toCharArray())
        {
            if (c >= 'a' && c <= 'z')
            {
                result.append("[").append(c).append(c - 32).append("]");
            }
            else result.append(c);
        }
        return result.toString();
    }


    public static boolean areRelated(Class c1, Class c2)
    {
        return isA(c1, c2) || isA(c2, c1);
    }

    public static boolean isA(Class c1, Class c2)
    {
        return c1 != null && c2 != null && c2.isAssignableFrom(c1);
    }


    public static long getLong(int upper, int lower)
    {
        return (((long) upper) << 32) | (lower & 0xffffffffL);
    }


    public static <T> boolean contains(T[] array, T object)
    {
        for (T t : array) if (t.equals(object)) return true;
        return false;
    }

    public static <T> int indexOf(T[] array, T object)
    {
        int i = 0;
        for (T t : array)
        {
            if (t.equals(object)) return i;
            i++;
        }
        return -1;
    }


    public static void disableSystemOut()
    {
        if (System.out == null || System.out instanceof NullPrintStream) return;

        out = System.out;
        ReflectionTool.set(System.class, "out", null, null);
    }

    public static void enableSystemOut()
    {
        if (out == null) return;

        ReflectionTool.set(System.class, "out", null, out);
    }

    public static void disableSystemErr()
    {
        if (System.err == null || System.err instanceof NullPrintStream) return;

        err = System.err;
        ReflectionTool.set(System.class, "err", null, null);
    }

    public static void enableSystemErr()
    {
        if (err == null) return;

        ReflectionTool.set(System.class, "err", null, err);
    }


    public static String fixFileSeparators(String input)
    {
        return input.replaceAll("[/\\\\]", Matcher.quoteReplacement(File.separator));
    }


    public static ByteBuffer cloneByteBuffer(ByteBuffer original)
    {
        //Get position, limit, and mark
        int pos = original.position();
        int limit = original.limit();
        int mark = -1;
        try
        {
            original.reset();
            mark = original.position();
        }
        catch (InvalidMarkException e)
        {
            //This happens when the original's mark is -1, so leave mark at default value of -1
        }

        //Create clone with matching capacity and byte order
        ByteBuffer clone = (original.isDirect()) ? ByteBuffer.allocateDirect(original.capacity()) : ByteBuffer.allocate(original.capacity());
        clone.order(original.order());

        //Copy FULL buffer contents, including the "out-of-bounds" part
        original.limit(original.capacity());
        original.position(0);
        clone.put(original);

        //Set mark of both buffers to what it was originally
        if (mark != -1)
        {
            original.position(mark);
            original.mark();

            clone.position(mark);
            clone.mark();
        }

        //Set position and limit of both buffers to what they were originally
        original.position(pos);
        original.limit(limit);
        clone.position(pos);
        clone.limit(limit);

        return clone;
    }


    public static char[] sort(Character... values)
    {
        char[] result = new char[values.length];
        System.arraycopy(values, 0, result, 0, result.length);

        char c;
        for (int passesRemaining = result.length - 1; passesRemaining > 0; passesRemaining--)
        {
            for (int i = 0; i < passesRemaining; i++)
            {
                if (inOrder(result[i], result[i + 1])) continue;

                c = result[i];
                result[i] = result[i + 1];
                result[i + 1] = c;
            }
        }

        return result;
    }

    public static boolean inOrder(Character s1, Character s2)
    {
        return inOrder("" + s1, "" + s2);
    }


    public static String[] sort(String... values)
    {
        String[] result = new String[values.length];
        System.arraycopy(values, 0, result, 0, result.length);

        String s;
        for (int passesRemaining = result.length - 1; passesRemaining > 0; passesRemaining--)
        {
            for (int i = 0; i < passesRemaining; i++)
            {
                if (inOrder(result[i], result[i + 1])) continue;

                s = result[i];
                result[i] = result[i + 1];
                result[i + 1] = s;
            }
        }

        return result;
    }

    public static boolean inOrder(String s1, String s2)
    {
        char[] chars1 = s1.toLowerCase().toCharArray();
        char[] chars2 = s2.toLowerCase().toCharArray();

        for (int i = 0; i < chars1.length; i++)
        {
            if (i >= chars2.length || chars2[i] < chars1[i]) return false;
            if (chars1[i] < chars2[i]) return true;
        }

        return true;
    }


    public static int parseHexInt(String hex)
    {
        hex = hex.replaceAll("0x", "");
        if (hex.length() > 8) throw new NumberFormatException("Hex string too long (max 8 characters): " + hex);

        if (hex.length() < 8) return Integer.parseInt(hex, 16);

        return (Integer.parseInt(hex.substring(0, 2), 16) << 24) | (Integer.parseInt(hex.substring(2), 16));
    }

    public static boolean isPowerOfTwo(int n)
    {
        return (n != 0) && ((n & (n - 1)) == 0);
    }


    public static ArrayList<String> allRecursiveRelativeFilenames(String directory)
    {
        File folder = new File(directory);
        if (!folder.exists() || !folder.isDirectory())
        {
            throw new IllegalStateException("Directory does not exist: " + directory);
        }


        ArrayList<String> result = new ArrayList<>();
        for (File file : folder.listFiles())
        {
            if (!file.isDirectory()) result.add(file.getName());
            else result.addAll(allRecursiveRelativeFilenames(directory, file.getName()));
        }

        return result;
    }

    private static ArrayList<String> allRecursiveRelativeFilenames(String mainDirectory, String relativeSubDirectory)
    {
        String fullDirectory = mainDirectory + File.separator + relativeSubDirectory;

        File folder = new File(fullDirectory);
        if (!folder.exists() || !folder.isDirectory())
        {
            throw new IllegalStateException("Directory does not exist: " + fullDirectory);
        }


        ArrayList<String> result = new ArrayList<>();
        for (File file : folder.listFiles())
        {
            String relativeName = relativeSubDirectory + File.separator + file.getName();
            if (!file.isDirectory()) result.add(relativeName);
            else result.addAll(allRecursiveRelativeFilenames(mainDirectory, relativeName));
        }

        return result;
    }


    public static boolean deleteFilesRecursively(File file)
    {
        if (file.isDirectory())
        {
            File[] files = file.listFiles();
            if (files != null)
            {
                for (File f : files) deleteFilesRecursively(f);
            }
        }
        return file.delete();
    }

    public static String[] fixedSplit(String string, String regex)
    {
        if (string == null) return null;

        if (regex == null) return new String[]{string};

        return preservedSplitSeparated(string, regex)[0];
    }

    public static String[] preservedSplit(String string, String regex, boolean interpolateResult)
    {
        if (string == null) return null;

        if (regex == null) return new String[]{string};

        String[][] separated = preservedSplitSeparated(string, regex);
        String[] tokens = separated[0];
        String[] delimiters = separated[1];
        String[] result = new String[tokens.length + delimiters.length];

        if (interpolateResult)
        {
            int delimiterCount = delimiters.length;
            for (int i = 0; i < delimiterCount; i++)
            {
                result[i << 1] = tokens[i];
                result[(i << 1) + 1] = delimiters[i];
            }
            result[result.length - 1] = tokens[tokens.length - 1];
        }
        else
        {
            System.arraycopy(tokens, 0, result, 0, tokens.length);
            System.arraycopy(delimiters, 0, result, tokens.length, delimiters.length);
        }

        return result;
    }

    public static String[][] preservedSplitSeparated(String string, String regex)
    {
        if (string == null) return null;

        if (regex == null) return new String[][]{new String[]{string}, new String[]{}};

        ArrayList<String> tokens = new ArrayList<>();
        ArrayList<String> delimiters = new ArrayList<>();
        String prev = string, current = string.replaceFirst(regex, "");
        while (!prev.equals(current))
        {
            int lengthDif = prev.length() - current.length();

            int prevIndex = prev.length() - 1;
            int curIndex = current.length() - 1;
            while (curIndex >= 0 && prev.charAt(prevIndex) == current.charAt(curIndex))
            {
                prevIndex--;
                curIndex--;
            }
            int startIndex = prevIndex + 1 - lengthDif;

            tokens.add(prev.substring(0, startIndex));
            delimiters.add(prev.substring(startIndex, startIndex + lengthDif));

            prev = prev.substring(startIndex + lengthDif);
            current = prev.replaceFirst(regex, "");
        }
        tokens.add(current);

        return new String[][]{tokens.toArray(new String[0]), delimiters.toArray(new String[0])};
    }


    public static int angleDifDeg(int angle1, int angle2)
    {
        return posMod(angle2 - angle1, 360);
    }

    public static float angleDifDeg(float angle1, float angle2)
    {
        return posMod(angle2 - angle1, 360);
    }

    public static double angleDifDeg(double angle1, double angle2)
    {
        return posMod(angle2 - angle1, 360);
    }

    public static float angleDifRad(float angle1, float angle2)
    {
        return posMod(angle2 - angle1, (float) Math.PI * 2);
    }

    public static double angleDifRad(double angle1, double angle2)
    {
        return posMod(angle2 - angle1, Math.PI * 2);
    }


    public static int average(int... values)
    {
        int sum = 0;
        for (int value : values)
        {
            sum += value;
        }
        return sum / values.length;
    }

    public static float average(float... values)
    {
        float sum = 0;
        for (float value : values)
        {
            sum += value;
        }
        return sum / values.length;
    }

    public static double average(double... values)
    {
        double sum = 0;
        for (double value : values)
        {
            sum += value;
        }
        return sum / values.length;
    }


    public static int min(int... values)
    {
        int result = Integer.MAX_VALUE;
        for (int value : values)
        {
            if (value < result) result = value;
        }
        return result;
    }

    public static long min(long... values)
    {
        long result = Long.MAX_VALUE;
        for (long value : values)
        {
            if (value < result) result = value;
        }
        return result;
    }

    public static float min(float... values)
    {
        float result = Float.POSITIVE_INFINITY;
        for (float value : values)
        {
            if (value < result) result = value;
        }
        return result;
    }

    public static double min(double... values)
    {
        double result = Double.POSITIVE_INFINITY;
        for (double value : values)
        {
            if (value < result) result = value;
        }
        return result;
    }


    public static int max(int... values)
    {
        int result = Integer.MIN_VALUE;
        for (int value : values)
        {
            if (value > result) result = value;
        }
        return result;
    }

    public static long max(long... values)
    {
        long result = Long.MIN_VALUE;
        for (long value : values)
        {
            if (value > result) result = value;
        }
        return result;
    }

    public static float max(float... values)
    {
        float result = Float.NEGATIVE_INFINITY;
        for (float value : values)
        {
            if (value > result) result = value;
        }
        return result;
    }

    public static double max(double... values)
    {
        double result = Double.NEGATIVE_INFINITY;
        for (double value : values)
        {
            if (value > result) result = value;
        }
        return result;
    }


    public static void printStackTrace(Thread thread, int maxNodes)
    {
        StackTraceElement[] stack = thread.getStackTrace();
        for (int i = 0; i < maxNodes && i < stack.length; i++)
        {
            System.out.println(stack[i].toString());
        }
    }

    public static void printStackTrace(Thread thread)
    {
        printStackTrace(thread, Integer.MAX_VALUE);
    }

    public static void printStackTrace()
    {
        printStackTrace(Thread.currentThread());
    }

    public static boolean stackContainsSubstring(Thread thread, String subString)
    {
        subString = subString.toLowerCase();

        StackTraceElement[] stack = thread.getStackTrace();
        for (StackTraceElement element : stack)
        {
            if (element.getClassName().toLowerCase().contains(subString)) return true;
        }
        return false;
    }

    public static boolean stackContainsSubstring(String substring)
    {
        return stackContainsSubstring(Thread.currentThread(), substring);
    }


    public static List<Class> getClassTree(Class clss)
    {
        if (clss == null) return null;

        List<Class> classList = new ArrayList<>();
        while (clss != null)
        {
            classList.add(clss);
            clss = clss.getSuperclass();
        }
        return classList;
    }

    public static void printClassTree(Class clss)
    {
        if (clss == null) System.out.println("Class given was null");
        else
        {
            System.out.println("===================================");
            System.out.println(clss.getSimpleName() + " Classtree:");
            while (clss != null)
            {
                System.out.println(clss.getName());
                clss = clss.getSuperclass();
            }
            System.out.println("===================================");
        }
    }


    public static void printMethods(Class clss)
    {
        if (clss == null) System.out.println("Class given was null");
        else
        {
            System.out.println("===================================");
            System.out.println(clss.getSimpleName() + " Methods:");
            for (Method method : clss.getDeclaredMethods()) System.out.println(method);
            System.out.println("===================================");
        }
    }


    public static void printFields(Class clss)
    {
        if (clss == null) System.out.println("Class given was null");
        else
        {
            System.out.println("===================================");
            System.out.println(clss.getSimpleName() + " Fields:");
            for (Field field : clss.getDeclaredFields()) System.out.println(field);
            System.out.println("===================================");
        }
    }


    public static <T> T choose(T[] choices)
    {
        return choices[(int) Math.floor(Math.random() * choices.length)];
    }

    public static <T> T choose(List<T> choices)
    {
        return choices.get((int) Math.floor(Math.random() * choices.size()));
    }

    public static byte choose(byte... choices)
    {
        return choices[(int) Math.floor(Math.random() * choices.length)];
    }

    public static short choose(short... choices)
    {
        return choices[(int) Math.floor(Math.random() * choices.length)];
    }

    public static int choose(int... choices)
    {
        return choices[(int) Math.floor(Math.random() * choices.length)];
    }

    public static long choose(long... choices)
    {
        return choices[(int) Math.floor(Math.random() * choices.length)];
    }

    public static float choose(float... choices)
    {
        return choices[(int) Math.floor(Math.random() * choices.length)];
    }

    public static double choose(double... choices)
    {
        return choices[(int) Math.floor(Math.random() * choices.length)];
    }

    public static boolean choose(boolean... choices)
    {
        return choices[(int) Math.floor(Math.random() * choices.length)];
    }

    public static char choose(char... choices)
    {
        return choices[(int) Math.floor(Math.random() * choices.length)];
    }

    public static String choose(String... choices)
    {
        return choices[(int) Math.floor(Math.random() * choices.length)];
    }

    @SuppressWarnings("unchecked")
    public static <E> E chooseObj(E... choices)
    {
        return choices[(int) Math.floor(Math.random() * choices.length)];
    }


    public static void insertBytes(byte[] bytes, int index, short value)
    {
        bytes[index] = (byte) (value >>> 8);
        bytes[index + 1] = (byte) value;
    }

    public static void insertBytes(byte[] bytes, int index, char value)
    {
        bytes[index] = (byte) (value >>> 8);
        bytes[index + 1] = (byte) value;
    }

    public static void insertBytes(byte[] bytes, int index, int value)
    {
        bytes[index] = (byte) (value >>> 24);
        bytes[index + 1] = (byte) (value >>> 16);
        bytes[index + 2] = (byte) (value >>> 8);
        bytes[index + 3] = (byte) value;
    }

    public static void insertBytes(byte[] bytes, int index, float value)
    {
        insertBytes(bytes, index, Float.floatToRawIntBits(value));
    }

    public static void insertBytes(byte[] bytes, int index, long value)
    {
        bytes[index] = (byte) (value >>> 56);
        bytes[index + 1] = (byte) (value >>> 48);
        bytes[index + 2] = (byte) (value >>> 40);
        bytes[index + 3] = (byte) (value >>> 32);
        bytes[index + 4] = (byte) (value >>> 24);
        bytes[index + 5] = (byte) (value >>> 16);
        bytes[index + 6] = (byte) (value >>> 8);
        bytes[index + 7] = (byte) value;
    }

    public static void insertBytes(byte[] bytes, int index, double value)
    {
        insertBytes(bytes, index, Double.doubleToRawLongBits(value));
    }


    public static byte[] mergeByteArrays(byte[] dest, byte[] src)
    {
        byte[] temp = new byte[dest.length + src.length];
        int i = 0;
        for (; i < dest.length; i++) temp[i] = dest[i];
        for (int i2 = i; i < temp.length; i++) temp[i] = src[i - i2];

        return temp;
    }

    public static byte[] subArray(byte[] bytes, int start)
    {
        return subArray(bytes, start, bytes.length - start);
    }

    public static byte[] subArray(byte[] bytes, int start, int length)
    {
        byte[] result = new byte[length];
        System.arraycopy(bytes, start, result, 0, length);
        return result;
    }

    public static char[] subArray(char[] chars, int start)
    {
        return subArray(chars, start, chars.length - start);
    }

    public static char[] subArray(char[] chars, int start, int length)
    {
        char[] result = new char[length];
        System.arraycopy(chars, start, result, 0, length);
        return result;
    }


    public static short bytesToShort(byte[] b, int index)
    {
        return (short) (((b[index] & 0xFF) << 8) | (b[index + 1] & 0xFF));
    }

    public static char bytesToChar(byte[] b, int index)
    {
        return (char) (((b[index] & 0xFF) << 8) | (b[index + 1] & 0xFF));
    }

    public static int bytesToInt(byte[] b, int index)
    {
        return (((b[index] & 0xFF) << 24) | ((b[index + 1] & 0xFF) << 16) | ((b[index + 2] & 0xFF) << 8) | (b[index + 3] & 0xFF));
    }

    public static float bytesToFloat(byte[] b, int index)
    {
        return Float.intBitsToFloat(bytesToInt(b, index));
    }

    public static String bytesToASCII(byte[] b, int index, int length)
    {
        String result = "";
        for (int i = 0; i < length; i++) result += (char) b[index + i];
        return result;
    }

    public static double degtorad(double deg)
    {
        return deg * Math.PI / 180;
    }

    public static double radtodeg(double rad)
    {
        return rad * 180 / Math.PI;
    }

    public static int posMod(int a, int b)
    {
        a = a % b;
        if (a < 0) a += b;
        return a;
    }

    public static float posMod(float a, float b)
    {
        a = a % b;
        if (a < 0) a += b;
        return a;
    }

    public static double posMod(double a, double b)
    {
        a = a % b;
        if (a < 0) a += b;
        return a;
    }

    public static byte random(byte maxvalue)
    {
        return (byte) (maxvalue * Math.random());
    }

    public static short random(short maxvalue)
    {
        return (short) (maxvalue * Math.random());
    }

    public static int random(int maxvalue)
    {
        return (int) (maxvalue * Math.random());
    }

    public static long random(long maxvalue)
    {
        return (long) (maxvalue * Math.random());
    }

    public static float random(float maxvalue)
    {
        return (float) (maxvalue * Math.random());
    }

    public static double random(double maxvalue)
    {
        return maxvalue * Math.random();
    }

    public static char random(char maxvalue)
    {
        return (char) (maxvalue * Math.random());
    }

    public static double randomGaussian(Random random, double center, double range)
    {
        double result = random.nextGaussian() * .5;
        while (result < -1 || result > 1) result = random.nextGaussian() * .5;
        return center + range * result;
    }


    public static double distance(double x1, double y1, double x2, double y2)
    {
        return Math.sqrt(distanceSquared(x1, y1, x2, y2));
    }

    public static double distanceSquared(double x1, double y1, double x2, double y2)
    {
        return (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
    }


    public static void freeDirectByteBuffer(ByteBuffer directBuffer) throws NoSuchFieldException, IllegalAccessException
    {
        Field field = directBuffer.getClass().getDeclaredField("cleaner");
        field.setAccessible(true);
        Cleaner cleaner = (Cleaner) field.get(directBuffer);
        cleaner.clean();
    }


    public static String readTXT(String filename) throws IOException
    {
        return readTXT(filename, false, null);
    }

    public static String readTXT(String filename, boolean internal, Class calledFrom) throws IOException
    {
        InputStream in;
        if (internal) in = calledFrom.getResourceAsStream(filename);
        else in = new FileInputStream(filename);

        String result = "";
        BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));

        String line;
        while ((line = reader.readLine()) != null) result += line + "\r\n";

        reader.close();
        in.close();

        return result;
    }


    public static ByteBuffer allocateNative(int bytes)
    {
        return ByteBuffer.allocateDirect(bytes).order(ByteOrder.nativeOrder());
    }


    public static class Heapdump
    {
        protected byte[][] bytes;

        public Heapdump()
        {
            int size = 1024 * 1024 * 1024;
            bytes = new byte[size][];
            for (int i = 0; i < size; i++)
            {
                bytes[i] = new byte[size];
                bytes[i][size - 1] = 1;
            }
        }
    }
}
