package com.fantasticsource.tools.json;

import java.io.*;
import java.util.ArrayList;

public class JSONManipulator
{
    //This was originally created to remove CNPC bank data; need to generify it
    private static void removeCNPCBankData()
    {
        try
        {
            File file = new File("playerdata");
            String s;
            ArrayList<String> strings = new ArrayList<>();
            boolean deleting;
            int braceCount = 0;
            for (File f : file.listFiles())
            {
                deleting = false;
                strings.clear();

                BufferedReader reader = new BufferedReader(new FileReader(f));
                while ((s = reader.readLine()) != null)
                {
                    if (s.contains("BankData"))
                    {
                        deleting = true;
                        strings.add(s);
                        braceCount = 1;
                    }
                    else
                    {
                        if (deleting)
                        {
                            if (s.contains("[")) braceCount++;
                            if (s.contains("]")) braceCount--;
                            if (braceCount == 0)
                            {
                                deleting = false;
                                strings.add(s);
                            }
                        }
                        else strings.add(s);
                    }
                }
                reader.close();

                BufferedWriter writer = new BufferedWriter(new FileWriter(f));
                for (String s2 : strings) writer.write(s2 + "\n");
                writer.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private static void removeCNPCQuestData()
    {
        try
        {
            File file = new File("playerdata");
            String s;
            ArrayList<String> strings = new ArrayList<>();
            boolean deleting;
            int braceCount = 0;
            for (File f : file.listFiles())
            {
                deleting = false;
                strings.clear();

                BufferedReader reader = new BufferedReader(new FileReader(f));
                while ((s = reader.readLine()) != null)
                {
                    if (s.contains("QuestData") || s.contains("DialogData"))
                    {
                        deleting = true;
                        strings.add(s);
                        braceCount = 1;
                    }
                    else
                    {
                        if (deleting)
                        {
                            if (s.contains("[")) braceCount++;
                            if (s.contains("]")) braceCount--;
                            if (braceCount == 0)
                            {
                                deleting = false;
                                strings.add(s);
                            }
                        }
                        else strings.add(s);
                    }
                }
                reader.close();

                BufferedWriter writer = new BufferedWriter(new FileWriter(f));
                for (String s2 : strings) writer.write(s2 + "\n");
                writer.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
