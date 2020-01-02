package com.fantasticsource.mctools;

import com.fantasticsource.fantasticlib.FantasticLib;
import net.minecraftforge.common.config.Configuration;

import java.io.*;
import java.util.ArrayList;

public class ConfigHandler
{
    protected final String modid;
    protected final File file;
    protected final ArrayList<String> lines = new ArrayList<>();
    protected final ArrayList<String> comments = new ArrayList<>();
    protected Configuration config;
    protected BufferedReader reader;
    protected BufferedWriter writer;

    public ConfigHandler(String modid) throws IllegalAccessException
    {
        this.modid = modid;
        config = MCTools.getConfig(modid);
        System.out.println(config == null);
        System.out.println(modid);
        file = config.getConfigFile();
    }

    public ConfigHandler load() throws IOException
    {
        reader = new BufferedReader(new FileReader(file));
        String line = reader.readLine();
        while (line != null)
        {
            int index = line.indexOf("#");
            if (index > -1)
            {
                lines.add(line.substring(0, index));
                comments.add(line.substring(index));
            }
            else
            {
                lines.add(line);
                comments.add("");
            }


            line = reader.readLine();
        }
        reader.close();

        return this;
    }

    public ConfigHandler save() throws IOException
    {
        writer = new BufferedWriter(new FileWriter(file));
        for (int i = 0; i < lines.size(); i++)
        {
            writer.write(lines.get(i) + comments.get(i) + (i < lines.size() - 1 ? "\r\n" : ""));
        }
        writer.close();

        return this;
    }

    public ConfigHandler sync() throws IllegalAccessException
    {
        MCTools.reloadConfig(modid);
        config = MCTools.getConfig(modid);
        return this;
    }

    public ConfigHandler removeCategory(String path) throws IllegalAccessException
    {
        String[] targetNodes = path.split("[.]");
        ArrayList<String> currentNodes = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++)
        {
            String line = lines.get(i);
            if (line.trim().equals("")) continue;


            boolean parentFound = true;
            if (currentNodes.size() != targetNodes.length)
            {
                parentFound = false;
            }
            else
            {
                for (int i2 = 0; i2 < targetNodes.length; i2++)
                {
                    if (!currentNodes.get(i2).equals(targetNodes[i2]))
                    {
                        parentFound = false;
                        break;
                    }
                }
            }

            if (parentFound)
            {
                removeCategory(i - 1);
                if (FantasticLib.isClient) MCTools.removeLangKey(currentNodes.get(currentNodes.size() - 1).toLowerCase());
                break;
            }
            else if (line.contains("}")) currentNodes.remove(currentNodes.size() - 1);
            else if (line.contains("{"))
            {
                String s = line.substring(0, line.indexOf("{")).trim();
                int index = s.indexOf('"'), lastIndex = s.lastIndexOf('"');
                if (index > -1 && lastIndex != index)
                {
                    s = s.substring(index + 1, lastIndex);
                }

                currentNodes.add(s);
            }
        }

        return this;
    }

    public ConfigHandler addCategory(String path) throws IllegalAccessException
    {
        String[] targetNodes = path.split("[.]");
        ArrayList<String> currentNodes = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++)
        {
            String line = lines.get(i);
            if (line.trim().equals("")) continue;


            boolean parentFound = true;
            if (currentNodes.size() != targetNodes.length - 1)
            {
                parentFound = false;
            }
            else
            {
                for (int i2 = 0; i2 < targetNodes.length - 1; i2++)
                {
                    if (!currentNodes.get(i2).equals(targetNodes[i2]))
                    {
                        parentFound = false;
                        break;
                    }
                }
            }

            if (parentFound)
            {
                addCategory(i, targetNodes[targetNodes.length - 1]);
                if (FantasticLib.isClient) MCTools.addLangKey(targetNodes[targetNodes.length - 1].toLowerCase(), targetNodes[targetNodes.length - 1]);
                break;
            }
            else if (line.contains("}")) currentNodes.remove(currentNodes.size() - 1);
            else if (line.contains("{"))
            {
                String s = line.substring(0, line.indexOf("{")).trim();
                int index = s.indexOf('"'), lastIndex = s.lastIndexOf('"');
                if (index > -1 && lastIndex != index)
                {
                    s = s.substring(index + 1, lastIndex);
                }

                currentNodes.add(s);
            }
        }

        return this;
    }

    public ConfigHandler addProperty(String path, Object value)
    {
        String[] targetNodes = path.split("[.]");
        ArrayList<String> currentNodes = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++)
        {
            String line = lines.get(i);
            if (line.trim().equals("")) continue;


            boolean parentFound = true;
            if (currentNodes.size() != targetNodes.length - 1)
            {
                parentFound = false;
            }
            else
            {
                for (int i2 = 0; i2 < targetNodes.length - 1; i2++)
                {
                    if (!currentNodes.get(i2).equals(targetNodes[i2]))
                    {
                        parentFound = false;
                        break;
                    }
                }
            }

            if (parentFound)
            {
                addProperty(i, targetNodes[targetNodes.length - 1], value);
                break;
            }
            else if (line.contains("}")) currentNodes.remove(currentNodes.size() - 1);
            else if (line.contains("{"))
            {
                String s = line.substring(0, line.indexOf("{")).trim();
                int index = s.indexOf('"'), lastIndex = s.lastIndexOf('"');
                if (index > -1 && lastIndex != index)
                {
                    s = s.substring(index + 1, lastIndex);
                }

                currentNodes.add(s);
            }
        }

        return this;
    }

    protected void removeCategory(int index)
    {
        int parens = 1;
        lines.remove(index);
        comments.remove(index);
        while (parens > 0)
        {
            comments.remove(index);
            String line = lines.remove(index);
            if (line.contains("{")) parens++;
            else if (line.contains("}")) parens--;
        }
    }

    protected void addCategory(int index, String name)
    {
        lines.add(index++, name + " {");
        comments.add("");
        lines.add(index++, "}");
        comments.add("");
    }

    protected void addProperty(int index, String name, Object value)
    {
        if (value.getClass() == String.class)
        {
            lines.add(index++, "S:" + name + "=" + value);
            comments.add("");
        }
    }
}
