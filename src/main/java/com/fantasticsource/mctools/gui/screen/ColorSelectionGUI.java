package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.text.GUIColor;
import com.fantasticsource.mctools.gui.element.text.GUILabeledTextInput;
import com.fantasticsource.mctools.gui.element.text.GUITextSpacer;
import com.fantasticsource.mctools.gui.element.text.filter.FilterColor;
import com.fantasticsource.mctools.gui.element.text.filter.FilterFloatColorChannel;
import com.fantasticsource.mctools.gui.element.text.filter.FilterIntColorChannel;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class ColorSelectionGUI extends GUIScreen
{
    private Color color;
    private GUIColor colorElement;

    public ColorSelectionGUI(GUIColor clickedElement)
    {
        this(clickedElement, 1);
    }

    public ColorSelectionGUI(GUIColor clickedElement, double textScale)
    {
        super(textScale);


        if (Minecraft.getMinecraft().currentScreen instanceof GUIScreen) GUIScreen.showStacked(this);
        else Minecraft.getMinecraft().displayGuiScreen(this);


        drawStack = false;


        colorElement = clickedElement;
        color = colorElement.getValue();


        root.add(new GUIGradient(this, 0, 0, 1, 1, Color.BLACK.copy().setAF(0.85f)));

        GUILabeledTextInput
                hex = new GUILabeledTextInput(this, "Hex: ", color.hex8(), FilterColor.INSTANCE),
                r = new GUILabeledTextInput(this, "Red (0-255): ", "" + color.r(), FilterIntColorChannel.INSTANCE),
                g = new GUILabeledTextInput(this, "Green (0-255): ", "" + color.g(), FilterIntColorChannel.INSTANCE),
                b = new GUILabeledTextInput(this, "Blue (0-255): ", "" + color.b(), FilterIntColorChannel.INSTANCE),
                a = new GUILabeledTextInput(this, "Alpha (0-255): ", "" + color.a(), FilterIntColorChannel.INSTANCE),
                rf = new GUILabeledTextInput(this, "Red (0-1): ", "" + color.rf(), FilterFloatColorChannel.INSTANCE),
                gf = new GUILabeledTextInput(this, "Green (0-1): ", "" + color.gf(), FilterFloatColorChannel.INSTANCE),
                bf = new GUILabeledTextInput(this, "Blue (0-1): ", "" + color.bf(), FilterFloatColorChannel.INSTANCE),
                af = new GUILabeledTextInput(this, "Alpha (0-1): ", "" + color.af(), FilterFloatColorChannel.INSTANCE);

        GUIGradient preview = new GUIGradient(this, 1, 0.3, color);

        GUIView left = new GUIView(this, 0.05, 0, 0.3, 1);
        GUIView center = new GUIView(this, 0.35, 0, 0.3, 1);
        GUIView right = new GUIView(this, 0.65, 0, 0.3, 1);

        left.add(new GUITextSpacer(this));
        left.add(r);
        r.input.addRecalcActions(() ->
        {
            if (r.input.valid())
            {
                color.setR(FilterIntColorChannel.INSTANCE.parse(r.input.text));
                preview.setColor(color);
                hex.setInput(color.hex8());
                rf.setInput("" + color.rf());
            }
        });
        left.add(g);
        g.input.addRecalcActions(() ->
        {
            if (g.input.valid())
            {
                color.setG(FilterIntColorChannel.INSTANCE.parse(g.input.text));
                preview.setColor(color);
                hex.setInput(color.hex8());
                gf.setInput("" + color.gf());
            }
        });
        left.add(b);
        b.input.addRecalcActions(() ->
        {
            if (b.input.valid())
            {
                color.setB(FilterIntColorChannel.INSTANCE.parse(b.input.text));
                preview.setColor(color);
                hex.setInput(color.hex8());
                bf.setInput("" + color.bf());
            }
        });
        left.add(a);
        a.input.addRecalcActions(() ->
        {
            if (a.input.valid())
            {
                color.setA(FilterIntColorChannel.INSTANCE.parse(a.input.text));
                preview.setColor(color);
                hex.setInput(color.hex8());
                af.setInput("" + color.af());
            }
        });

        center.add(new GUITextSpacer(this));
        center.add(hex);
        hex.input.addRecalcActions(() ->
        {
            if (hex.input.valid())
            {
                color.setColor(FilterColor.INSTANCE.parse(hex.input.text));
                preview.setColor(color);
                r.setInput("" + color.r());
                g.setInput("" + color.g());
                b.setInput("" + color.b());
                a.setInput("" + color.a());
                rf.setInput("" + color.rf());
                gf.setInput("" + color.gf());
                bf.setInput("" + color.bf());
                af.setInput("" + color.af());
            }
        });
        center.add(new GUITextSpacer(this));
        center.add(preview);

        right.add(new GUITextSpacer(this));
        right.add(rf);
        rf.input.addRecalcActions(() ->
        {
            if (rf.input.valid())
            {
                color.setRF(FilterFloatColorChannel.INSTANCE.parse(rf.input.text));
                preview.setColor(color);
                hex.setInput(color.hex8());
                r.setInput("" + color.r());
            }
        });
        right.add(gf);
        gf.input.addRecalcActions(() ->
        {
            if (gf.input.valid())
            {
                color.setGF(FilterFloatColorChannel.INSTANCE.parse(gf.input.text));
                preview.setColor(color);
                hex.setInput(color.hex8());
                g.setInput("" + color.g());
            }
        });
        right.add(bf);
        bf.input.addRecalcActions(() ->
        {
            if (bf.input.valid())
            {
                color.setBF(FilterFloatColorChannel.INSTANCE.parse(bf.input.text));
                preview.setColor(color);
                hex.setInput(color.hex8());
                b.setInput("" + color.b());
            }
        });
        right.add(af);
        af.input.addRecalcActions(() ->
        {
            if (af.input.valid())
            {
                color.setAF(FilterFloatColorChannel.INSTANCE.parse(af.input.text));
                preview.setColor(color);
                hex.setInput(color.hex8());
                a.setInput("" + color.a());
            }
        });
    }

    @Override
    public String title()
    {
        return "Color";
    }

    @Override
    protected void init()
    {
    }
}
