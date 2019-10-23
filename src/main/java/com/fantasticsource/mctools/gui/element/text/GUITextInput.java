package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.MonoASCIIFontRenderer;
import com.fantasticsource.mctools.Render;
import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.text.filter.TextFilter;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;

import static com.fantasticsource.mctools.gui.GUIScreen.*;
import static com.fantasticsource.tools.datastructures.Color.*;

public class GUITextInput extends GUIText
{
    protected static final Color T_RED = RED.copy().setAF(0.4f);

    protected int cursorPosition, selectorPosition = -1;
    protected long cursorTime;
    protected TextFilter filter;

    protected long lastClickTime;
    protected int clicks = 1;
    protected int lastAbsMouseX;


    public GUITextInput(GUIScreen screen, String text, TextFilter filter)
    {
        this(screen, text, filter, WHITE, 1);
    }

    public GUITextInput(GUIScreen screen, String text, TextFilter filter, double scale)
    {
        this(screen, text, filter, WHITE, scale);
    }

    public GUITextInput(GUIScreen screen, String text, TextFilter filter, Color activeColor)
    {
        this(screen, text, filter, activeColor, 1);
    }

    public GUITextInput(GUIScreen screen, String text, TextFilter filter, Color activeColor, double scale)
    {
        super(screen, text, getIdleColor(activeColor), getHoverColor(activeColor), activeColor, scale);

        this.filter = filter;

        cursorPosition = text.length();
    }


    public GUITextInput(GUIScreen screen, double x, double y, String text, TextFilter filter)
    {
        this(screen, x, y, text, filter, WHITE, 1);
    }

    public GUITextInput(GUIScreen screen, double x, double y, String text, TextFilter filter, double scale)
    {
        this(screen, x, y, text, filter, WHITE, scale);
    }

    public GUITextInput(GUIScreen screen, double x, double y, String text, TextFilter filter, Color activeColor)
    {
        this(screen, x, y, text, filter, activeColor, 1);
    }

    public GUITextInput(GUIScreen screen, double x, double y, String text, TextFilter filter, Color activeColor, double scale)
    {
        super(screen, x, y, text, getIdleColor(activeColor), getHoverColor(activeColor), activeColor, scale);

        this.filter = filter;

        cursorPosition = text.length();
    }


    protected boolean hasSelectedText()
    {
        return selectorPosition != -1 && selectorPosition != cursorPosition;
    }

    protected int charType(char c)
    {
        if (Character.isWhitespace(c)) return 0;
        if (Character.isLetterOrDigit(c) || c == '_') return 1;
        return -1;
    }

    protected boolean isWhitespace()
    {
        for (char c : text.toCharArray()) if (c != ' ') return false;
        return true;
    }

    protected int nonWhitespaceStart()
    {
        int i = 0;
        for (char c : text.toCharArray())
        {
            if (c != ' ') break;
            i++;
        }
        return i;
    }

    protected int nonWhitespaceEnd()
    {
        char[] chars = text.toCharArray();
        int i = chars.length;
        for (; i > 0; i--)
        {
            if (chars[i - 1] != ' ') break;
        }
        return i;
    }

    protected int tabs()
    {
        if (!(parent instanceof CodeInput)) return 0;

        int tabbing = 0;
        for (int index = 0; index < parent.size(); index++)
        {
            GUITextInput element = (GUITextInput) parent.get(index);
            if (element == this) return Tools.max(0, tabbing);

            for (char c : element.text.toCharArray())
            {
                if (c == '{') tabbing++;
                else if (c == '}') tabbing--;
            }
        }

        throw new IllegalStateException("This should be impossible");
    }

    protected void deselectAll()
    {
        if (parent instanceof CodeInput)
        {
            ((CodeInput) parent).selectionStartY = -1;
            for (GUIElement element : parent.children)
            {
                ((GUITextInput) element).selectorPosition = -1;
            }
        }
        else selectorPosition = -1;
    }

    protected void singleLineHome()
    {
        int startPos = isWhitespace() ? Tools.min(text.length(), tabs()) : nonWhitespaceStart();
        if (cursorPosition == startPos) startPos = 0;

        if (GUIScreen.isShiftKeyDown())
        {
            if (selectorPosition == -1 && cursorPosition != startPos) selectorPosition = cursorPosition;
        }
        else deselectAll();

        cursorPosition = startPos;

        if (parent instanceof CodeInput) ((CodeInput) parent).cursorX = cursorPosition;
    }

    protected void singleLineEnd()
    {
        int endPos = isWhitespace() ? Tools.min(text.length(), tabs()) : nonWhitespaceEnd();
        if (cursorPosition == endPos) endPos = text.length();

        if (GUIScreen.isShiftKeyDown())
        {
            if (selectorPosition == -1 && cursorPosition != endPos) selectorPosition = cursorPosition;
        }
        else deselectAll();

        cursorPosition = endPos;

        if (parent instanceof CodeInput) ((CodeInput) parent).cursorX = cursorPosition;
    }

    public boolean valid()
    {
        return filter.acceptable(text);
    }

    protected GUITextInput multilineDelete()
    {
        if (!(parent instanceof CodeInput) || ((CodeInput) parent).selectionStartY == -1 || ((CodeInput) parent).selectionStartY == parent.indexOf(this)) return null;

        CodeInput code = (CodeInput) parent;
        int index = parent.indexOf(this);
        int firstY = Tools.min(index, code.selectionStartY);
        int lastY = Tools.max(index, code.selectionStartY);

        GUITextInput element = (GUITextInput) code.get(lastY);
        String s = element.text.substring(Tools.max(element.cursorPosition, element.selectorPosition));

        element = (GUITextInput) code.get(firstY);
        int nextCursorPos = element.selectorPosition == -1 ? element.cursorPosition : Tools.min(element.selectorPosition, element.cursorPosition);
        element.text = element.text.substring(0, nextCursorPos) + s;

        setActive(false);
        for (int i = lastY - firstY; i > 0; i--)
        {
            code.remove(firstY + 1);
        }
        element.setActive(true);

        element.selectorPosition = -1;
        element.cursorPosition = nextCursorPos;

        code.cursorX = nextCursorPos;
        code.selectionStartY = -1;

        return element;
    }

    protected GUITextInput activeLine()
    {
        if (parent instanceof CodeInput)
        {
            for (GUIElement element : parent.children)
            {
                if (element.isActive()) return (GUITextInput) element;
            }
        }

        return isActive() ? this : null;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode)
    {
        super.keyTyped(typedChar, keyCode);

        if (!active) return;

        CodeInput code = parent instanceof CodeInput ? (CodeInput) parent : null;

        if (keyCode == Keyboard.KEY_RETURN)
        {
            if (this instanceof GUIMultilineTextInput)
            {
                GUITextInput element = multilineDelete();
                if (element == null) element = this;

                int min = Tools.min(element.cursorPosition, element.selectorPosition);
                if (min == -1) min = element.cursorPosition;
                String before = element.text.substring(0, min);
                String after = element.text.substring(Tools.max(element.cursorPosition, element.selectorPosition));
                element.text = before + "\n" + after;
                deselectAll();
                element.cursorPosition = min + 1;
            }
            else if (code != null)
            {
                GUITextInput element = multilineDelete();
                if (element == null) element = this;

                int min = element.selectorPosition == -1 ? element.cursorPosition : Tools.min(element.cursorPosition, element.selectorPosition);
                String before = element.text.substring(0, min);
                String after = element.text.substring(Tools.max(element.cursorPosition, element.selectorPosition));

                element.text = before;
                deselectAll();
                element.cursorPosition = min;
                element.setActive(false);

                int tabs = element.tabs();
                for (char c : before.toCharArray())
                {
                    if (c == '{') tabs++;
                    else if (c == '}') tabs--;
                }
                String afterTrimmed = after.trim();
                if (afterTrimmed.length() > 0 && afterTrimmed.charAt(0) == '}') tabs--;
                tabs = Tools.max(0, tabs);

                StringBuilder tabbing = new StringBuilder();
                for (int i = tabs; i > 0; i--) tabbing.append(" ");

                element = (GUITextInput) code.add(code.indexOf(element) + 1, tabbing + afterTrimmed);
                element.setActive(true);
                element.cursorPosition = tabs;

                code.cursorX = tabs;
            }
        }
        else if (keyCode == Keyboard.KEY_HOME)
        {
            if (this instanceof GUIMultilineTextInput)
            {
                if (GUIScreen.isShiftKeyDown())
                {
                    if (selectorPosition == -1) selectorPosition = cursorPosition;
                }
                else selectorPosition = -1;

                if (GUIScreen.isCtrlKeyDown()) cursorPosition = 0;
                else
                {
                    int position = 0;
                    for (String fullLine : fullLines)
                    {
                        position += fullLine.length();
                        if (position >= cursorPosition)
                        {
                            cursorPosition = position - fullLine.length();
                            if (fullLine.length() > 0 && fullLine.charAt(0) == '\n') cursorPosition++;
                            break;
                        }
                    }
                }
            }
            else
            {
                int index = parent.indexOf(this);
                if (code != null && index != 0 && GUIScreen.isCtrlKeyDown())
                {
                    GUITextInput first = (GUITextInput) code.get(0);

                    if (GUIScreen.isShiftKeyDown())
                    {
                        if (code.selectionStartY == -1) code.selectionStartY = index;

                        for (int i = 0; i < parent.size(); i++)
                        {
                            GUITextInput element = (GUITextInput) parent.get(i);

                            if (i > code.selectionStartY) element.selectorPosition = -1;
                            else if (i < code.selectionStartY)
                            {
                                element.selectorPosition = element.text.length();
                                element.cursorPosition = 0;
                            }
                            else
                            {
                                if (element.selectorPosition == -1) element.selectorPosition = element.cursorPosition;
                                element.cursorPosition = 0;
                            }
                        }
                    }
                    else
                    {
                        deselectAll();
                        first.cursorPosition = 0;
                    }

                    setActive(false);
                    first.setActive(true);

                    code.cursorX = first.cursorPosition;
                }
                else singleLineHome();
            }
        }
        else if (keyCode == Keyboard.KEY_END)
        {
            if (this instanceof GUIMultilineTextInput)
            {
                if (GUIScreen.isShiftKeyDown())
                {
                    if (selectorPosition == -1) selectorPosition = cursorPosition;
                }
                else selectorPosition = -1;

                if (GUIScreen.isCtrlKeyDown()) cursorPosition = text.length();
                else
                {
                    int position = 0;
                    for (int i = 0; i < fullLines.size(); i++)
                    {
                        String fullLine = fullLines.get(i);
                        position += fullLine.length();
                        if (position >= cursorPosition)
                        {
                            if (cursorPosition == position && cursorPosition < text.length() && text.charAt(cursorPosition) != '\n') cursorPosition += fullLines.get(i + 1).length();
                            else cursorPosition = position;
                            break;
                        }
                    }
                }
            }
            else
            {
                int index = parent.indexOf(this);
                if (code != null && index != parent.size() - 1 && GUIScreen.isCtrlKeyDown())
                {
                    GUITextInput last = (GUITextInput) code.get(code.size() - 1);

                    if (GUIScreen.isShiftKeyDown())
                    {
                        if (code.selectionStartY == -1) code.selectionStartY = index;

                        for (int i = 0; i < parent.size(); i++)
                        {
                            GUITextInput element = (GUITextInput) parent.get(i);

                            if (i < code.selectionStartY) element.selectorPosition = -1;
                            else if (i > code.selectionStartY)
                            {
                                element.selectorPosition = 0;
                                element.cursorPosition = element.text.length();
                            }
                            else
                            {
                                if (element.selectorPosition == -1) element.selectorPosition = element.cursorPosition;
                                element.cursorPosition = element.text.length();
                            }
                        }
                    }
                    else
                    {
                        deselectAll();
                        last.cursorPosition = last.text.length();
                    }

                    setActive(false);
                    last.setActive(true);

                    code.cursorX = last.cursorPosition;
                }
                else singleLineEnd();
            }
        }
        else if (GUIScreen.isCtrlKeyDown() && keyCode == Keyboard.KEY_A)
        {
            if (code != null)
            {
                GUITextInput last = (GUITextInput) code.get(code.size() - 1);

                for (GUIElement e : code.children)
                {
                    GUITextInput element = (GUITextInput) e;
                    if (element.text.length() > 0)
                    {
                        element.selectorPosition = 0;
                        element.cursorPosition = element.text.length();
                    }
                }

                setActive(false);
                last.setActive(true);

                code.selectionStartY = 0;
                code.cursorX = last.cursorPosition;
            }
            else
            {
                if (text.length() > 0)
                {
                    selectorPosition = 0;
                    cursorPosition = text.length();
                }
            }
        }
        else if (GUIScreen.isCtrlKeyDown() && keyCode == Keyboard.KEY_X)
        {
            StringBuilder s = new StringBuilder();

            if (code != null && ((CodeInput) parent).selectionStartY != -1 && ((CodeInput) parent).selectionStartY != parent.indexOf(this))
            {
                int startY = Tools.min(code.indexOf(this), code.selectionStartY);
                int endY = Tools.max(code.indexOf(this), code.selectionStartY);

                GUITextInput element = (GUITextInput) code.get(startY);
                s.append(element.text.substring(element.selectorPosition == -1 ? element.cursorPosition : Tools.min(element.cursorPosition, element.selectorPosition)));

                for (int i = startY + 1; i < endY; i++)
                {
                    s.append("\n").append(((GUITextInput) code.get(i)).text);
                }

                element = (GUITextInput) code.get(endY);
                s.append("\n").append(element.text, 0, Tools.max(element.cursorPosition, element.selectorPosition));

                multilineDelete();
            }
            else if (hasSelectedText())
            {
                int min = Tools.min(selectorPosition, cursorPosition);
                s.append(text, min, Tools.max(cursorPosition, selectorPosition));
                text = text.substring(0, min) + text.substring(Tools.max(selectorPosition, cursorPosition));
                deselectAll();
                cursorPosition = min;

                if (code != null) ((CodeInput) parent).cursorX = cursorPosition;
            }

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s.toString().replaceAll("\n", "\r\n")), null);
        }
        else if (GUIScreen.isCtrlKeyDown() && keyCode == Keyboard.KEY_C)
        {
            StringBuilder s = new StringBuilder();

            if (code != null && ((CodeInput) parent).selectionStartY != -1 && ((CodeInput) parent).selectionStartY != parent.indexOf(this))
            {
                int startY = Tools.min(code.indexOf(this), code.selectionStartY);
                int endY = Tools.max(code.indexOf(this), code.selectionStartY);

                GUITextInput element = (GUITextInput) code.get(startY);
                s.append(element.text.substring(element.selectorPosition == -1 ? element.cursorPosition : Tools.min(element.cursorPosition, element.selectorPosition)));

                for (int i = startY + 1; i < endY; i++)
                {
                    s.append("\n").append(((GUITextInput) code.get(i)).text);
                }

                element = (GUITextInput) code.get(endY);
                s.append("\n").append(element.text, 0, Tools.max(element.cursorPosition, element.selectorPosition));
            }
            else if (hasSelectedText())
            {
                s = new StringBuilder(text.substring(Tools.min(cursorPosition, selectorPosition), Tools.max(cursorPosition, selectorPosition)));
            }

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s.toString().replaceAll("\n", "\r\n")), null);
        }
        else if (GUIScreen.isCtrlKeyDown() && keyCode == Keyboard.KEY_V)
        {
            String clipboard = GUIScreen.getClipboardString().replaceAll("\r", "");
            String[] tokens = Tools.fixedSplit(clipboard, "\n");
            GUITextInput element = multilineDelete();
            if (element == null) element = this;

            if (code != null && tokens.length > 1)
            {
                int min = element.selectorPosition == -1 ? element.cursorPosition : Tools.min(element.cursorPosition, element.selectorPosition);
                String before = element.text.substring(0, min);
                String after = element.text.substring(Tools.max(element.cursorPosition, element.selectorPosition));
                element.text = before + tokens[0];
                element.setActive(false);

                int index = code.indexOf(element) + 1;
                for (int i = 1; i < tokens.length - 1; i++)
                {
                    code.add(index++, tokens[i]);
                }

                before = tokens[tokens.length - 1];
                element = (GUITextInput) code.add(index, before + after);

                deselectAll();
                element.setActive(true);
                element.cursorPosition = before.length();

                code.cursorX = element.cursorPosition;
                code.selectionStartY = -1;
            }
            else
            {
                int min = Tools.min(element.cursorPosition, element.selectorPosition);
                if (min == -1) min = element.cursorPosition;
                String before = element.text.substring(0, min) + clipboard;
                element.text = before + element.text.substring(Tools.max(element.cursorPosition, element.selectorPosition));

                deselectAll();
                element.cursorPosition = before.length();
            }
        }
        else if (typedChar >= ' ' && typedChar <= '~')
        {
            GUITextInput element = multilineDelete();
            if (element == null) element = this;

            int min = Tools.min(element.cursorPosition, element.selectorPosition);
            if (min == -1) min = element.cursorPosition;
            String before = element.text.substring(0, min);
            String after = element.text.substring(Tools.max(element.cursorPosition, element.selectorPosition));
            element.text = before + typedChar + after;
            deselectAll();
            element.cursorPosition = min + 1;

            if (code != null)
            {
                code.cursorX = element.cursorPosition;
                code.selectionStartY = -1;
            }
        }
        else if (keyCode == Keyboard.KEY_BACK)
        {
            if (multilineDelete() == null)
            {
                if (hasSelectedText())
                {
                    int min = Tools.min(selectorPosition, cursorPosition);
                    text = text.substring(0, min) + text.substring(Tools.max(selectorPosition, cursorPosition));
                    deselectAll();
                    cursorPosition = min;
                }
                else if (cursorPosition > 0)
                {
                    text = text.substring(0, cursorPosition - 1) + text.substring(cursorPosition);
                    cursorPosition--;
                }
                else if (code != null)
                {
                    int index = parent.indexOf(this);
                    if (index != 0)
                    {
                        GUITextInput other = (GUITextInput) parent.get(index - 1);
                        text = other.text + text;
                        cursorPosition = other.text.length();
                        parent.remove(index - 1);
                    }
                }

                if (code != null)
                {
                    code.cursorX = cursorPosition;
                    code.selectionStartY = -1;
                }
            }
        }
        else if (keyCode == Keyboard.KEY_DELETE)
        {
            if (multilineDelete() == null)
            {
                if (hasSelectedText())
                {
                    int min = Tools.min(selectorPosition, cursorPosition);
                    text = text.substring(0, min) + text.substring(Tools.max(selectorPosition, cursorPosition));
                    deselectAll();
                    cursorPosition = min;
                }
                else if (cursorPosition < text.length())
                {
                    text = text.substring(0, cursorPosition) + text.substring(cursorPosition + 1);
                }
                else if (code != null)
                {
                    int index = parent.indexOf(this);
                    if (index != parent.size() - 1)
                    {
                        text = text + ((GUITextInput) parent.get(index + 1)).text;
                        parent.remove(index + 1);
                    }
                }

                if (code != null)
                {
                    code.cursorX = cursorPosition;
                    code.selectionStartY = -1;
                }
            }
        }
        else if (keyCode == Keyboard.KEY_LEFT)
        {
            if (GUIScreen.isShiftKeyDown())
            {
                if (selectorPosition == -1 && cursorPosition > 0) selectorPosition = cursorPosition;
            }
            else deselectAll();

            if (cursorPosition > 0)
            {
                int type = charType(text.charAt(cursorPosition - 1));
                cursorPosition--;

                if (type != -1 && GUIScreen.isCtrlKeyDown())
                {
                    while (cursorPosition > 0 && charType(text.charAt(cursorPosition - 1)) == type) cursorPosition--;
                }

                if (code != null) ((CodeInput) parent).cursorX = cursorPosition;
            }
            else
            {
                if (code != null)
                {
                    int index = code.indexOf(this);
                    if (index != 0)
                    {
                        GUITextInput other = (GUITextInput) code.get(index - 1);

                        if (GUIScreen.isShiftKeyDown() && code.selectionStartY == -1)
                        {
                            code.selectionStartY = index;
                            other.selectorPosition = -1;
                        }

                        setActive(false);
                        other.setActive(true);
                        other.cursorPosition = other.text.length();

                        code.cursorX = other.cursorPosition;
                    }
                    else code.cursorX = cursorPosition;
                }
            }
        }
        else if (keyCode == Keyboard.KEY_RIGHT)
        {
            if (GUIScreen.isShiftKeyDown())
            {
                if (selectorPosition == -1 && cursorPosition < text.length()) selectorPosition = cursorPosition;
            }
            else deselectAll();

            if (cursorPosition < text.length())
            {
                int type = charType(text.charAt(cursorPosition));
                cursorPosition++;

                if (type != -1 && GUIScreen.isCtrlKeyDown())
                {
                    while (cursorPosition < text.length() && charType(text.charAt(cursorPosition)) == type) cursorPosition++;
                }

                if (code != null) ((CodeInput) parent).cursorX = cursorPosition;
            }
            else
            {
                if (code != null)
                {
                    int index = code.indexOf(this);
                    if (index != parent.size() - 1)
                    {
                        GUITextInput other = (GUITextInput) code.get(index + 1);

                        if (GUIScreen.isShiftKeyDown() && code.selectionStartY == -1)
                        {
                            code.selectionStartY = index;
                            other.selectorPosition = -1;
                        }

                        setActive(false);
                        other.setActive(true);
                        other.cursorPosition = 0;

                        code.cursorX = other.cursorPosition;
                    }
                    else code.cursorX = cursorPosition;
                }
            }
        }
        else if (keyCode == Keyboard.KEY_UP)
        {
            if (this instanceof GUIMultilineTextInput)
            {
                if (GUIScreen.isShiftKeyDown())
                {
                    if (selectorPosition == -1) selectorPosition = cursorPosition;
                }
                else selectorPosition = -1;

                int lineIndex = 0;
                int pos = 0;
                String partialLine = "";
                for (String fullLine : fullLines)
                {
                    pos += fullLine.length();
                    if (pos >= cursorPosition)
                    {
                        partialLine = fullLine.substring(0, cursorPosition - (pos - fullLine.length()));
                        break;
                    }
                    lineIndex++;
                }

                if (lineIndex == 0) cursorPosition = 0;
                else
                {
                    cursorPosition = findCursorPosition(absoluteX() + (double) FONT_RENDERER.getStringWidth(partialLine.replaceAll("\n", "")) * scale / screen.width, lineIndex - 1);
                }
            }
            else
            {
                if (code != null && parent.indexOf(this) > 0)
                {
                    int index = code.indexOf(this);
                    GUITextInput other = (GUITextInput) code.get(index - 1);

                    if (GUIScreen.isShiftKeyDown())
                    {
                        if (code.selectionStartY == -1) code.selectionStartY = index;

                        if (code.selectionStartY > index)
                        {
                            selectorPosition = text.length();
                            cursorPosition = 0;
                            other.selectorPosition = other.text.length();
                        }
                        else if (code.selectionStartY < index) selectorPosition = -1;
                        else
                        {
                            if (selectorPosition == -1) selectorPosition = cursorPosition;
                            cursorPosition = 0;
                            other.selectorPosition = other.text.length();
                        }
                    }
                    else deselectAll();

                    setActive(false);
                    other.setActive(true);

                    other.cursorPosition = Tools.min(other.text.length(), code.cursorX);
                }
                else singleLineHome();
            }
        }
        else if (keyCode == Keyboard.KEY_DOWN)
        {
            if (this instanceof GUIMultilineTextInput)
            {
                if (GUIScreen.isShiftKeyDown())
                {
                    if (selectorPosition == -1) selectorPosition = cursorPosition;
                }
                else selectorPosition = -1;

                int lineIndex = 0;
                int pos = 0;
                String partialLine = "";
                for (String fullLine : fullLines)
                {
                    pos += fullLine.length();
                    if (pos >= cursorPosition)
                    {
                        partialLine = fullLine.substring(0, cursorPosition - (pos - fullLine.length()));
                        break;
                    }
                    lineIndex++;
                }

                if (lineIndex == fullLines.size() - 1) cursorPosition = text.length();
                else
                {
                    cursorPosition = findCursorPosition(absoluteX() + (double) FONT_RENDERER.getStringWidth(partialLine.replaceAll("\n", "")) * scale / screen.width, lineIndex + 1);
                }
            }
            else
            {
                if (code != null && parent.indexOf(this) != parent.size() - 1)
                {
                    int index = code.indexOf(this);
                    GUITextInput other = (GUITextInput) code.get(index + 1);

                    if (GUIScreen.isShiftKeyDown())
                    {
                        if (code.selectionStartY == -1) code.selectionStartY = index;

                        if (code.selectionStartY < index)
                        {
                            selectorPosition = 0;
                            cursorPosition = text.length();
                            other.selectorPosition = 0;
                        }
                        else if (code.selectionStartY > index) selectorPosition = -1;
                        else
                        {
                            if (selectorPosition == -1) selectorPosition = cursorPosition;
                            cursorPosition = text.length();
                            other.selectorPosition = 0;
                        }
                    }
                    else deselectAll();

                    setActive(false);
                    other.setActive(true);

                    other.cursorPosition = Tools.min(other.text.length(), code.cursorX);
                }
                else singleLineEnd();
            }
        }


        if (code != null)
        {
            GUITextInput element = activeLine();
            if (element != null)
            {
                if (element.y < code.top)
                {
                    code.progress = element.y / (code.internalHeight - 1);
                }

                if (element.y + element.height > code.bottom)
                {
                    code.progress = (element.y + element.height - 1) / (code.internalHeight - 1);
                }
            }
        }


        cursorTime = System.currentTimeMillis();

        recalc(0);
    }

    @Override
    public GUITextInput recalc(int subIndexChanged)
    {
        text = text.replaceAll("\r", "");

        lines.clear();
        fullLines.clear();

        if (parent instanceof CodeInput)
        {
            lines.add(text);
            fullLines.add(text);

            width = (double) MonoASCIIFontRenderer.getStringWidth(text) * scale / screen.width;
            height = (double) (MonoASCIIFontRenderer.LINE_HEIGHT + 2) * scale / screen.height;
        }
        else
        {
            StringBuilder previous = new StringBuilder();

            String[] words = Tools.preservedSplit(text, "[\n]|[ ]+", true);

            double parentW = parent == null ? 1 : parent.absoluteWidth();

            StringBuilder line = new StringBuilder();
            StringBuilder fullLine = new StringBuilder();

            int index = 0;
            double maxLineW = 0, lineW = -1d / screen.width;
            while (index < words.length)
            {
                String word = words[index++];

                if (word.equals("")) continue;

                if (word.equals("\n"))
                {
                    lines.add(line.toString());
                    fullLines.add(fullLine.toString());

                    line = new StringBuilder();
                    fullLine = new StringBuilder("\n");

                    maxLineW = 1;
                    lineW = -1d / screen.width;
                }
                else
                {
                    double wordW = (double) Render.getPartialStringWidth(previous.toString().replaceAll("\n", ""), word.replaceAll("\n", "")) * scale / screen.width;

                    if (lineW + wordW > parentW)
                    {
                        if (word.trim().equals(""))
                        {
                            fullLine.append(word);
                            continue;
                        }

                        if (line.length() == 0)
                        {
                            line.append(word);
                            fullLine.append(word);

                            lineW += wordW;
                        }
                        else
                        {
                            lines.add(line.toString());
                            fullLines.add(fullLine.toString());

                            line = new StringBuilder(word);
                            fullLine = new StringBuilder(word);

                            maxLineW = parentW;
                            lineW = (double) (Render.getPartialStringWidth(previous.toString().replaceAll("\n", ""), word.replaceAll("\n", "")) - 1) * scale / screen.width;
                        }
                    }
                    else
                    {
                        line.append(word);
                        fullLine.append(word);

                        lineW += wordW;
                    }
                }

                previous.append(word);
            }

            if (line.length() > 0)
            {
                lines.add(line.toString());
                maxLineW = Tools.max(maxLineW, lineW);
            }
            if (fullLine.length() > 0) fullLines.add(fullLine.toString());

            width = maxLineW;
            if (this instanceof GUIMultilineTextInput && text.length() > 0 && text.charAt(text.length() - 1) == '\n')
            {
                height = (double) (Tools.max(1, fullLines.size()) * FONT_RENDERER.FONT_HEIGHT - 1) * scale / screen.height;
            }
            else height = (double) (Tools.max(1, lines.size()) * FONT_RENDERER.FONT_HEIGHT - 1) * scale / screen.height;
        }

        if (parent != null)
        {
            width /= parent.absoluteWidth();
            height /= parent.absoluteHeight();
        }

        recalcAndRepositionSubElements(0);

        if (parent instanceof CodeInput) width = Tools.max(width, 2d / parent.absolutePxWidth());
        else width = 1 - x;

        postRecalc();

        return this;
    }

    @Override
    public boolean isWithin(double x, double y)
    {
        if (parent instanceof CodeInput)
        {
            double yy = absoluteY();
            return yy <= y && y < yy + absoluteHeight();
        }
        return super.isWithin(x, y);
    }

    @Override
    public boolean mousePressed(double x, double y, int button)
    {
        if (button == 0 && isMouseWithin())
        {
            setActive(true);
            long time = System.currentTimeMillis();
            int absMouseX = (int) (mouseX * screen.width);

            if (time - lastClickTime <= 250 && Math.abs(lastAbsMouseX - absMouseX) < 3) clicks++;
            else clicks = 1;

            lastClickTime = time;

            if (clicks == 1)
            {
                lastAbsMouseX = absMouseX;

                if (isShiftKeyDown())
                {
                    if (selectorPosition == -1) selectorPosition = cursorPosition;
                }
                else deselectAll();

                cursorPosition = findCursorPosition(mouseX(), mouseY());

                if (parent instanceof CodeInput && ((CodeInput) parent).selectionStartY == -1) ((CodeInput) parent).selectionStartY = parent.indexOf(this);
            }
            else if (clicks == 2)
            {
                deselectAll();

                cursorPosition = findCursorPosition(mouseX(), mouseY());
                selectorPosition = cursorPosition;

                char[] chars = text.toCharArray();
                int type = charType(chars[cursorPosition == 0 ? 0 : cursorPosition - 1]);

                while (cursorPosition < chars.length && charType(chars[cursorPosition]) == type) cursorPosition++;
                while (selectorPosition > 0 && charType(chars[selectorPosition - 1]) == type) selectorPosition--;
                if (selectorPosition == cursorPosition) selectorPosition = -1;
            }
            else
            {
                deselectAll();

                cursorPosition = text.length();
                selectorPosition = 0;
            }

            cursorTime = System.currentTimeMillis();

            if (parent instanceof CodeInput) ((CodeInput) parent).cursorX = cursorPosition;
        }
        else setActive(false);

        for (GUIElement child : (ArrayList<GUIElement>) children.clone()) child.mousePressed(x - this.x, y - this.y, button);

        return active;
    }

    @Override
    public boolean mouseReleased(double x, double y, int button)
    {
        boolean result = button == 0 && active && isMouseWithin();
        if (result && !MinecraftForge.EVENT_BUS.post(new GUILeftClickEvent(screen, this))) click();

        for (GUIElement child : (ArrayList<GUIElement>) children.clone()) child.mouseReleased(x - this.x, y - this.y, button);
        return result;
    }

    @Override
    public void mouseDrag(double x, double y, int button)
    {
        if (button == 0 && ((parent instanceof CodeInput && isMouseWithin()) || (!(parent instanceof CodeInput) && active)))
        {
            if (parent instanceof CodeInput && ((CodeInput) parent).selectionStartY != -1 && ((CodeInput) parent).selectionStartY != parent.indexOf(this))
            {
                CodeInput code = (CodeInput) parent;
                int index = code.indexOf(this);

                if (code.selectionStartY < index)
                {
                    GUITextInput element = (GUITextInput) code.get(code.selectionStartY);
                    if (element.selectorPosition == -1) element.selectorPosition = element.cursorPosition;
                    element.cursorPosition = element.text.length();

                    for (int i = code.selectionStartY + 1; i < index; i++)
                    {
                        element = (GUITextInput) code.get(i);
                        element.selectorPosition = 0;
                        element.cursorPosition = element.text.length();
                    }

                    selectorPosition = 0;

                    for (int i = 0; i < code.selectionStartY; i++)
                    {
                        ((GUITextInput) code.get(i)).selectorPosition = -1;
                    }
                    for (int i = index + 1; i < code.size(); i++)
                    {
                        ((GUITextInput) code.get(i)).selectorPosition = -1;
                    }
                }
                else
                {
                    GUITextInput element = (GUITextInput) code.get(code.selectionStartY);
                    if (element.selectorPosition == -1) element.selectorPosition = element.cursorPosition;
                    element.cursorPosition = 0;

                    for (int i = code.selectionStartY - 1; i > index; i--)
                    {
                        element = (GUITextInput) code.get(i);
                        element.selectorPosition = element.text.length();
                        element.cursorPosition = 0;
                    }

                    selectorPosition = text.length();

                    for (int i = 0; i < index; i++)
                    {
                        ((GUITextInput) code.get(i)).selectorPosition = -1;
                    }
                    for (int i = code.selectionStartY + 1; i < code.size(); i++)
                    {
                        ((GUITextInput) code.get(i)).selectorPosition = -1;
                    }
                }

                cursorPosition = findCursorPosition(mouseX(), mouseY());
            }
            else
            {
                if (selectorPosition == -1) selectorPosition = cursorPosition;
                cursorPosition = findCursorPosition(mouseX(), mouseY());
                if (selectorPosition == cursorPosition) selectorPosition = -1;

                int sp = selectorPosition, cp = cursorPosition;
                deselectAll();

                selectorPosition = sp;
                cursorPosition = cp;

                if (parent instanceof CodeInput)
                {
                    CodeInput code = (CodeInput) parent;
                    code.selectionStartY = code.indexOf(this);
                    code.cursorX = cursorPosition;
                }
            }

            activeLine().setActive(false);
            setActive(true);
        }

        cursorTime = System.currentTimeMillis();

        super.mouseDrag(x, y, button);
    }

    @Override
    public void draw()
    {
        GlStateManager.disableTexture2D();


        double adjustedScale = scale * new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();

        if (!filter.acceptable(text))
        {
            //Highlight red if text does not pass filter
            GlStateManager.color(T_RED.rf(), T_RED.gf(), T_RED.bf(), T_RED.af());

            GlStateManager.glBegin(GL11.GL_QUADS);
            GlStateManager.glVertex3f(0, 0, 0);
            GlStateManager.glVertex3f(0, 1, 0);
            GlStateManager.glVertex3f(1, 1, 0);
            GlStateManager.glVertex3f(1, 0, 0);
            GlStateManager.glEnd();
        }
        else if (active)
        {
            //If we pass the filter, highlight gray if active
            GlStateManager.color(GRAY.rf(), GRAY.gf(), GRAY.bf(), 0.2f);

            GlStateManager.glBegin(GL11.GL_QUADS);
            GlStateManager.glVertex3f(0, 0, 0);
            GlStateManager.glVertex3f(0, 1, 0);
            GlStateManager.glVertex3f(1, 1, 0);
            GlStateManager.glVertex3f(1, 0, 0);
            GlStateManager.glEnd();
        }


        //Actual text
        if (text.length() > 0)
        {
            GlStateManager.enableTexture2D();

            GlStateManager.pushMatrix();
            GlStateManager.scale(adjustedScale / absolutePxWidth(), adjustedScale / absolutePxHeight(), 1);

            Color c = active ? activeColor : isMouseWithin() ? hoverColor : color;
            if (parent instanceof CodeInput) MonoASCIIFontRenderer.draw(text, 0, 0, c, BLACK);
            else FONT_RENDERER.drawString(text, 1, 0, (c.color() >> 8) | c.a() << 24, false);

            GlStateManager.popMatrix();

            GlStateManager.disableTexture2D();
        }


        //Draw cursor and selection highlight
        if (active || parent instanceof CodeInput)
        {
            float cursorX = parent instanceof CodeInput ? MonoASCIIFontRenderer.getStringWidth(text.substring(0, cursorPosition)) : FONT_RENDERER.getStringWidth(text.substring(0, cursorPosition).replaceAll("\n", "")) + 0.5f;
            float selectorX = selectorPosition == -1 ? cursorX : (parent instanceof CodeInput ? MonoASCIIFontRenderer.getStringWidth(text.substring(0, selectorPosition)) : FONT_RENDERER.getStringWidth(text.substring(0, selectorPosition).replaceAll("\n", ""))) + 0.5f;

            cursorX = Tools.max(cursorX, 1f / absolutePxWidth() + 0.5f);
            cursorX *= adjustedScale / absolutePxWidth();
            selectorX *= adjustedScale / absolutePxWidth();

            if (selectorPosition != -1 && cursorX != selectorX)
            {
                float min = Tools.min(cursorX, selectorX), max = Tools.max(cursorX, selectorX);
                GlStateManager.color(1, 1, 1, 0.3f);

                GlStateManager.glBegin(GL11.GL_QUADS);
                GlStateManager.glVertex3f(min, 0, 0);
                GlStateManager.glVertex3f(min, 1, 0);
                GlStateManager.glVertex3f(max, 1, 0);
                GlStateManager.glVertex3f(max, 0, 0);
                GlStateManager.glEnd();
            }

            if (active && (System.currentTimeMillis() - cursorTime) % 1000 < 500)
            {
                GlStateManager.color(1, 1, 1, 1);

                GlStateManager.glBegin(GL11.GL_LINES);
                GlStateManager.glVertex3f(cursorX, 0, 0);
                GlStateManager.glVertex3f(cursorX, 1, 0);
                GlStateManager.glEnd();
            }
        }


        drawChildren();
    }

    @Override
    public void setActive(boolean active)
    {
        if (active && !this.active) cursorTime = System.currentTimeMillis();
        super.setActive(active);
    }

    protected int findCursorPosition(double absX, double absY)
    {
        return findCursorPosition(absX, (int) ((absY - absoluteY()) / absoluteHeight() * fullLines.size()));
    }

    protected int findCursorPosition(double absX, int lineIndex)
    {
        //Find y, set line, and set result offset
        String line;
        int result = 0;
        if (this instanceof GUIMultilineTextInput)
        {
            if (lineIndex >= lines.size()) return text.length();

            line = lines.get(lineIndex);
            String fullLine = fullLines.get(lineIndex);
            if (fullLine.length() > 0 && fullLine.charAt(0) == '\n') result++;

            for (int i = 0; i < lineIndex; i++)
            {
                result += fullLines.get(i).length();
            }
        }
        else line = text;

        //Find x
        double xDif = absX - absoluteX();
        for (char c : line.toCharArray())
        {
            double lastDif = xDif;
            xDif -= (double) (parent instanceof CodeInput ? (MonoASCIIFontRenderer.CHAR_WIDTH + 2) : FONT_RENDERER.getCharWidth(c)) * scale / screen.width;
            if (xDif <= 0)
            {
                if (Math.abs(xDif) < lastDif) result++;
                break;
            }
            result++;
        }

        return result;
    }

    @Override
    public String toString()
    {
        return filter.transformInput(text);
    }
}
