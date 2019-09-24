package com.fantasticsource.mctools.gui.element.text;

import com.fantasticsource.mctools.MonoASCIIFontRenderer;
import com.fantasticsource.mctools.gui.GUILeftClickEvent;
import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.text.filter.FilterNone;
import com.fantasticsource.mctools.gui.element.text.filter.TextFilter;
import com.fantasticsource.tools.Tools;
import com.fantasticsource.tools.datastructures.Color;
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
    private static final Color T_WHITE = new Color(0xFFFFFF88);

    protected int cursorPosition, selectorPosition = -1;
    protected Color cursorColor, highlightColor, shadowColor;
    protected long cursorTime;
    protected TextFilter filter;

    protected long lastClickTime;
    protected int clicks = 1;
    protected int lastAbsMouseX;


    public GUITextInput(GUIScreen screen, String text, TextFilter filter)
    {
        super(screen, text, filter.acceptable(text) ? GREEN : RED);

        this.filter = filter;

        cursorPosition = text.length();

        cursorColor = WHITE;
        highlightColor = T_WHITE;
        shadowColor = color.v() >= 64 ? BLACK : WHITE;
    }

    public GUITextInput(GUIScreen screen, String text, Color color, Color hoverColor, Color activeColor, Color cursorColor, Color hightlightColor)
    {
        super(screen, text, color, hoverColor, activeColor);

        filter = FilterNone.INSTANCE;

        cursorPosition = text.length();

        this.cursorColor = cursorColor;
        this.highlightColor = hightlightColor;
        shadowColor = color.v() >= 64 ? BLACK : WHITE;
    }

    public GUITextInput(GUIScreen screen, double x, double y, String text, TextFilter filter)
    {
        super(screen, x, y, text, filter.acceptable(text) ? GREEN : RED);

        this.filter = filter;

        cursorPosition = text.length();

        cursorColor = WHITE;
        highlightColor = T_WHITE;
        shadowColor = color.v() >= 64 ? BLACK : WHITE;
    }

    public GUITextInput(GUIScreen screen, double x, double y, String text, Color color, Color hoverColor, Color activeColor, Color cursorColor, Color hightlightColor)
    {
        super(screen, x, y, text, color, hoverColor, activeColor);

        filter = FilterNone.INSTANCE;

        cursorPosition = text.length();

        this.cursorColor = cursorColor;
        this.highlightColor = hightlightColor;
        shadowColor = color.v() >= 64 ? BLACK : WHITE;
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
        if (!(parent instanceof MultilineTextInput)) return 0;

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
        if (parent instanceof MultilineTextInput)
        {
            ((MultilineTextInput) parent).selectionStartY = -1;
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

        if (parent instanceof MultilineTextInput) ((MultilineTextInput) parent).cursorX = cursorPosition;
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

        if (parent instanceof MultilineTextInput) ((MultilineTextInput) parent).cursorX = cursorPosition;
    }

    public boolean valid()
    {
        return filter.acceptable(text);
    }

    protected GUITextInput multilineDelete()
    {
        if (!(parent instanceof MultilineTextInput) || ((MultilineTextInput) parent).selectionStartY == -1 || ((MultilineTextInput) parent).selectionStartY == parent.indexOf(this)) return null;

        MultilineTextInput multi = (MultilineTextInput) parent;
        int index = parent.indexOf(this);
        int firstY = Tools.min(index, multi.selectionStartY);
        int lastY = Tools.max(index, multi.selectionStartY);

        GUITextInput element = (GUITextInput) multi.get(lastY);
        String s = element.text.substring(Tools.max(element.cursorPosition, element.selectorPosition));

        element = (GUITextInput) multi.get(firstY);
        int nextCursorPos = element.selectorPosition == -1 ? element.cursorPosition : Tools.min(element.selectorPosition, element.cursorPosition);
        element.text = element.text.substring(0, nextCursorPos) + s;

        setActive(false);
        for (int i = lastY - firstY; i > 0; i--)
        {
            multi.remove(firstY + 1);
        }
        element.setActive(true);

        element.selectorPosition = -1;
        element.cursorPosition = nextCursorPos;

        multi.cursorX = nextCursorPos;
        multi.selectionStartY = -1;

        return element;
    }

    protected GUITextInput activeLine()
    {
        if (parent instanceof MultilineTextInput)
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

        MultilineTextInput multi = parent instanceof MultilineTextInput ? (MultilineTextInput) parent : null;

        if (keyCode == Keyboard.KEY_RETURN)
        {
            if (multi != null)
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

                element = (GUITextInput) multi.add(multi.indexOf(element) + 1, tabbing + afterTrimmed);
                element.setActive(true);
                element.cursorPosition = tabs;

                multi.cursorX = tabs;
            }
        }
        else if (keyCode == Keyboard.KEY_HOME)
        {
            int index = parent.indexOf(this);
            if (multi != null && index != 0 && GUIScreen.isCtrlKeyDown())
            {
                GUITextInput first = (GUITextInput) multi.get(0);

                if (GUIScreen.isShiftKeyDown())
                {
                    if (multi.selectionStartY == -1) multi.selectionStartY = index;

                    for (int i = 0; i < parent.size(); i++)
                    {
                        GUITextInput element = (GUITextInput) parent.get(i);

                        if (i > multi.selectionStartY) element.selectorPosition = -1;
                        else if (i < multi.selectionStartY)
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

                multi.cursorX = first.cursorPosition;
            }
            else singleLineHome();
        }
        else if (keyCode == Keyboard.KEY_END)
        {
            int index = parent.indexOf(this);
            if (multi != null && index != parent.size() - 1 && GUIScreen.isCtrlKeyDown())
            {
                GUITextInput last = (GUITextInput) multi.get(multi.size() - 1);

                if (GUIScreen.isShiftKeyDown())
                {
                    if (multi.selectionStartY == -1) multi.selectionStartY = index;

                    for (int i = 0; i < parent.size(); i++)
                    {
                        GUITextInput element = (GUITextInput) parent.get(i);

                        if (i < multi.selectionStartY) element.selectorPosition = -1;
                        else if (i > multi.selectionStartY)
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

                multi.cursorX = last.cursorPosition;
            }
            else singleLineEnd();
        }
        else if (GUIScreen.isCtrlKeyDown() && keyCode == Keyboard.KEY_A)
        {
            if (multi != null)
            {
                GUITextInput last = (GUITextInput) multi.get(multi.size() - 1);

                for (GUIElement e : multi.children)
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

                multi.selectionStartY = 0;
                multi.cursorX = last.cursorPosition;
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

            if (multi != null && ((MultilineTextInput) parent).selectionStartY != -1 && ((MultilineTextInput) parent).selectionStartY != parent.indexOf(this))
            {
                int startY = Tools.min(multi.indexOf(this), multi.selectionStartY);
                int endY = Tools.max(multi.indexOf(this), multi.selectionStartY);

                GUITextInput element = (GUITextInput) multi.get(startY);
                s.append(element.text.substring(element.selectorPosition == -1 ? element.cursorPosition : Tools.min(element.cursorPosition, element.selectorPosition)));

                for (int i = startY + 1; i < endY; i++)
                {
                    s.append("\r\n").append(((GUITextInput) multi.get(i)).text);
                }

                element = (GUITextInput) multi.get(endY);
                s.append("\r\n").append(element.text, 0, Tools.max(element.cursorPosition, element.selectorPosition));

                multilineDelete();
            }
            else if (hasSelectedText())
            {
                int min = Tools.min(selectorPosition, cursorPosition);
                s.append(text, min, Tools.max(cursorPosition, selectorPosition));
                text = text.substring(0, min) + text.substring(Tools.max(selectorPosition, cursorPosition));
                deselectAll();
                cursorPosition = min;

                if (multi != null) ((MultilineTextInput) parent).cursorX = cursorPosition;
            }

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s.toString()), null);
        }
        else if (GUIScreen.isCtrlKeyDown() && keyCode == Keyboard.KEY_C)
        {
            StringBuilder s = new StringBuilder();

            if (multi != null && ((MultilineTextInput) parent).selectionStartY != -1 && ((MultilineTextInput) parent).selectionStartY != parent.indexOf(this))
            {
                int startY = Tools.min(multi.indexOf(this), multi.selectionStartY);
                int endY = Tools.max(multi.indexOf(this), multi.selectionStartY);

                GUITextInput element = (GUITextInput) multi.get(startY);
                s.append(element.text.substring(element.selectorPosition == -1 ? element.cursorPosition : Tools.min(element.cursorPosition, element.selectorPosition)));

                for (int i = startY + 1; i < endY; i++)
                {
                    s.append("\r\n").append(((GUITextInput) multi.get(i)).text);
                }

                element = (GUITextInput) multi.get(endY);
                s.append("\r\n").append(element.text, 0, Tools.max(element.cursorPosition, element.selectorPosition));
            }
            else if (hasSelectedText())
            {
                s = new StringBuilder(text.substring(Tools.min(cursorPosition, selectorPosition), Tools.max(cursorPosition, selectorPosition)));
            }

            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s.toString()), null);
        }
        else if (GUIScreen.isCtrlKeyDown() && keyCode == Keyboard.KEY_V)
        {
            String[] tokens = Tools.fixedSplit(GUIScreen.getClipboardString(), "\n");
            GUITextInput element = multilineDelete();
            if (element == null) element = this;

            if (multi != null && tokens.length > 1)
            {
                int min = element.selectorPosition == -1 ? element.cursorPosition : Tools.min(element.cursorPosition, element.selectorPosition);
                String before = element.text.substring(0, min);
                String after = element.text.substring(Tools.max(element.cursorPosition, element.selectorPosition));
                element.text = before + removeIllegalChars(tokens[0]);
                element.setActive(false);

                int index = multi.indexOf(element) + 1;
                for (int i = 1; i < tokens.length - 1; i++)
                {
                    multi.add(index++, removeIllegalChars(tokens[i]));
                }

                before = removeIllegalChars(tokens[tokens.length - 1]);
                element = (GUITextInput) multi.add(index, before + after);

                deselectAll();
                element.setActive(true);
                element.cursorPosition = before.length();

                multi.cursorX = element.cursorPosition;
                multi.selectionStartY = -1;
            }
            else
            {
                int min = Tools.min(element.cursorPosition, element.selectorPosition);
                if (min == -1) min = element.cursorPosition;
                String before = element.text.substring(0, min) + removeIllegalChars(GUIScreen.getClipboardString());
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

            if (multi != null)
            {
                multi.cursorX = element.cursorPosition;
                multi.selectionStartY = -1;
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
                else if (multi != null)
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

                if (multi != null)
                {
                    multi.cursorX = cursorPosition;
                    multi.selectionStartY = -1;
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
                else if (multi != null)
                {
                    int index = parent.indexOf(this);
                    if (index != parent.size() - 1)
                    {
                        text = text + ((GUITextInput) parent.get(index + 1)).text;
                        parent.remove(index + 1);
                    }
                }

                if (multi != null)
                {
                    multi.cursorX = cursorPosition;
                    multi.selectionStartY = -1;
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

                if (multi != null) ((MultilineTextInput) parent).cursorX = cursorPosition;
            }
            else
            {
                if (multi != null)
                {
                    int index = multi.indexOf(this);
                    if (index != 0)
                    {
                        GUITextInput other = (GUITextInput) multi.get(index - 1);

                        if (GUIScreen.isShiftKeyDown() && multi.selectionStartY == -1)
                        {
                            multi.selectionStartY = index;
                            other.selectorPosition = -1;
                        }

                        setActive(false);
                        other.setActive(true);
                        other.cursorPosition = other.text.length();

                        multi.cursorX = other.cursorPosition;
                    }
                    else multi.cursorX = cursorPosition;
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

                if (multi != null) ((MultilineTextInput) parent).cursorX = cursorPosition;
            }
            else
            {
                if (multi != null)
                {
                    int index = multi.indexOf(this);
                    if (index != parent.size() - 1)
                    {
                        GUITextInput other = (GUITextInput) multi.get(index + 1);

                        if (GUIScreen.isShiftKeyDown() && multi.selectionStartY == -1)
                        {
                            multi.selectionStartY = index;
                            other.selectorPosition = -1;
                        }

                        setActive(false);
                        other.setActive(true);
                        other.cursorPosition = 0;

                        multi.cursorX = other.cursorPosition;
                    }
                    else multi.cursorX = cursorPosition;
                }
            }
        }
        else if (keyCode == Keyboard.KEY_UP)
        {
            if (multi != null && parent.indexOf(this) > 0)
            {
                int index = multi.indexOf(this);
                GUITextInput other = (GUITextInput) multi.get(index - 1);

                if (GUIScreen.isShiftKeyDown())
                {
                    if (multi.selectionStartY == -1) multi.selectionStartY = index;

                    if (multi.selectionStartY > index)
                    {
                        selectorPosition = text.length();
                        cursorPosition = 0;
                        other.selectorPosition = other.text.length();
                    }
                    else if (multi.selectionStartY < index) selectorPosition = -1;
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

                other.cursorPosition = Tools.min(other.text.length(), multi.cursorX);
            }
            else singleLineHome();
        }
        else if (keyCode == Keyboard.KEY_DOWN)
        {
            if (multi != null && parent.indexOf(this) != parent.size() - 1)
            {
                int index = multi.indexOf(this);
                GUITextInput other = (GUITextInput) multi.get(index + 1);

                if (GUIScreen.isShiftKeyDown())
                {
                    if (multi.selectionStartY == -1) multi.selectionStartY = index;

                    if (multi.selectionStartY < index)
                    {
                        selectorPosition = 0;
                        cursorPosition = text.length();
                        other.selectorPosition = 0;
                    }
                    else if (multi.selectionStartY > index) selectorPosition = -1;
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

                other.cursorPosition = Tools.min(other.text.length(), multi.cursorX);
            }
            else singleLineEnd();
        }


        if (multi != null)
        {
            GUITextInput element = activeLine();
            if (element != null)
            {
                if (element.y * multi.height < multi.top)
                {
                    multi.progress = element.y * multi.height / (multi.internalHeight - multi.height);
                }

                if (element.y * multi.height + element.height > multi.bottom)
                {
                    multi.progress = (element.y * multi.height + element.height - multi.height) / (multi.internalHeight - multi.height);
                }
            }
        }


        cursorTime = System.currentTimeMillis();

        if (filter.getClass() != FilterNone.class)
        {
            activeColor = filter.acceptable(text) ? GREEN : RED;
            color = getColor(activeColor);
            hoverColor = getHover(activeColor);
        }

        recalc();
    }

    @Override
    public GUITextInput recalc()
    {
        super.recalc();

        if (!(parent instanceof MultilineTextInput)) width = 1 - x;

        return this;
    }

    @Override
    public boolean isWithin(double x, double y)
    {
        if (parent instanceof MultilineTextInput)
        {
            double yy = getScreenY();
            return yy <= y && y < yy + getScreenHeight();
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

                cursorPosition = findCursorPosition(mouseX());

                if (parent instanceof MultilineTextInput && ((MultilineTextInput) parent).selectionStartY == -1) ((MultilineTextInput) parent).selectionStartY = parent.indexOf(this);
            }
            else if (clicks == 2)
            {
                deselectAll();

                cursorPosition = findCursorPosition(mouseX());
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

            if (parent instanceof MultilineTextInput) ((MultilineTextInput) parent).cursorX = cursorPosition;
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
        if (button == 0 && isMouseWithin())
        {
            if (parent instanceof MultilineTextInput && ((MultilineTextInput) parent).selectionStartY != -1 && ((MultilineTextInput) parent).selectionStartY != parent.indexOf(this))
            {
                MultilineTextInput multi = (MultilineTextInput) parent;
                int index = multi.indexOf(this);

                if (multi.selectionStartY < index)
                {
                    GUITextInput element = (GUITextInput) multi.get(multi.selectionStartY);
                    if (element.selectorPosition == -1) element.selectorPosition = element.cursorPosition;
                    element.cursorPosition = element.text.length();

                    for (int i = multi.selectionStartY + 1; i < index; i++)
                    {
                        element = (GUITextInput) multi.get(i);
                        element.selectorPosition = 0;
                        element.cursorPosition = element.text.length();
                    }

                    selectorPosition = 0;

                    for (int i = 0; i < multi.selectionStartY; i++)
                    {
                        ((GUITextInput) multi.get(i)).selectorPosition = -1;
                    }
                    for (int i = index + 1; i < multi.size(); i++)
                    {
                        ((GUITextInput) multi.get(i)).selectorPosition = -1;
                    }
                }
                else
                {
                    GUITextInput element = (GUITextInput) multi.get(multi.selectionStartY);
                    if (element.selectorPosition == -1) element.selectorPosition = element.cursorPosition;
                    element.cursorPosition = 0;

                    for (int i = multi.selectionStartY - 1; i > index; i--)
                    {
                        element = (GUITextInput) multi.get(i);
                        element.selectorPosition = element.text.length();
                        element.cursorPosition = 0;
                    }

                    selectorPosition = text.length();

                    for (int i = 0; i < index; i++)
                    {
                        ((GUITextInput) multi.get(i)).selectorPosition = -1;
                    }
                    for (int i = multi.selectionStartY + 1; i < multi.size(); i++)
                    {
                        ((GUITextInput) multi.get(i)).selectorPosition = -1;
                    }
                }

                cursorPosition = findCursorPosition(mouseX());
            }
            else
            {
                if (selectorPosition == -1) selectorPosition = cursorPosition;
                cursorPosition = findCursorPosition(mouseX());
                if (selectorPosition == cursorPosition) selectorPosition = -1;

                int sp = selectorPosition, cp = cursorPosition;
                deselectAll();

                selectorPosition = sp;
                cursorPosition = cp;

                if (parent instanceof MultilineTextInput)
                {
                    MultilineTextInput multi = (MultilineTextInput) parent;
                    multi.selectionStartY = multi.indexOf(this);
                    multi.cursorX = cursorPosition;
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
        double screenWidth = screen.width, screenHeight = screen.height;

        int mcScale = new ScaledResolution(screen.mc).getScaleFactor();
        double wScale = screenWidth * mcScale, hScale = screenHeight * mcScale;

        if (children.size() > 0 && width > 0 && height > 0)
        {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor((int) (x * wScale), (int) ((1 - (y + height)) * hScale), (int) (width * wScale), (int) (height * hScale));
            for (GUIElement element : children)
            {
                if (element.x + element.width < 0 || element.x > width || element.y + element.height < 0 || element.y >= height) continue;
                element.draw();
            }
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }


        GlStateManager.enableTexture2D();

        GlStateManager.pushMatrix();
        GlStateManager.translate(getScreenX(), getScreenY(), 0);
        GlStateManager.scale(1d / screen.width, 1d / screen.height, 1);


        //Highlight red if text does not pass filter
        if (!filter.acceptable(text))
        {
            //TODO
//            GlStateManager.disableTexture2D();
//            GlStateManager.color(1, 0, 0, 0.7f);
//            GlStateManager.glBegin(GL11.GL_LINES);
//            GlStateManager.glVertex3f(0, -0.5f, 0);
//            GlStateManager.glVertex3f(0, (float) (height * screenHeight), 0);
//            GlStateManager.glEnd();
        }


        //Actual text
        Color c = active ? activeColor : isMouseWithin() ? hoverColor : color;
        if (text.length() > 0)
        {
            if (parent instanceof MultilineTextInput) MonoASCIIFontRenderer.draw(text, 0, 0, c, shadowColor);
            else FONT_RENDERER.drawString(text, 0, 0, (c.color() >> 8) | c.a() << 24, false);
        }


        //Draw cursor and selection highlight
        if (active || parent instanceof MultilineTextInput)
        {
            float cursorX = parent instanceof MultilineTextInput ? MonoASCIIFontRenderer.getStringWidth(text.substring(0, cursorPosition)) : FONT_RENDERER.getStringWidth(text.substring(0, cursorPosition)) - 0.5f;
            cursorX = Tools.max(cursorX, 1f / screen.width);
            float selectorX = selectorPosition == -1 ? cursorX : (parent instanceof MultilineTextInput ? MonoASCIIFontRenderer.getStringWidth(text.substring(0, selectorPosition)) : FONT_RENDERER.getStringWidth(text.substring(0, selectorPosition))) - 0.5f;

            if (cursorX != selectorX)
            {
                float min = Tools.min(cursorX, selectorX), max = Tools.max(cursorX, selectorX);
                GlStateManager.disableTexture2D();
                GlStateManager.color(highlightColor.rf(), highlightColor.gf(), highlightColor.bf(), highlightColor.af());
                GlStateManager.glBegin(GL11.GL_QUADS);
                GlStateManager.glVertex3f(min, 0, 0);
                GlStateManager.glVertex3f(min, (float) (height * screenHeight), 0);
                GlStateManager.glVertex3f(max, (float) (height * screenHeight), 0);
                GlStateManager.glVertex3f(max, 0, 0);
                GlStateManager.glEnd();
            }

            if (active && (System.currentTimeMillis() - cursorTime) % 1000 < 500)
            {
                GlStateManager.disableTexture2D();
                GlStateManager.color(cursorColor.rf(), cursorColor.gf(), cursorColor.bf(), cursorColor.af());
                GlStateManager.glBegin(GL11.GL_LINES);
                GlStateManager.glVertex3f(cursorX, -0.5f, 0);
                GlStateManager.glVertex3f(cursorX, (float) (height * screenHeight), 0);
                GlStateManager.glEnd();
            }
        }

        GlStateManager.popMatrix();
    }

    @Override
    public void setActive(boolean active)
    {
        if (active && !this.active) cursorTime = System.currentTimeMillis();
        super.setActive(active);
    }

    protected String removeIllegalChars(String text)
    {
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray())
        {
            if (c >= ' ' && c <= '~') result.append(c);
        }
        return result.toString();
    }

    protected int findCursorPosition(double x)
    {
        double dif = x - getScreenX();
        int result = 0;
        for (char c : text.toCharArray())
        {
            double lastDif = dif;
            dif -= (double) (parent instanceof MultilineTextInput ? MonoASCIIFontRenderer.LINE_HEIGHT : FONT_RENDERER.getCharWidth(c)) / screen.width;
            if (dif <= 0)
            {
                if (Math.abs(dif) < lastDif) result++;
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
