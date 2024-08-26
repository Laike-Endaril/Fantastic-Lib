package com.fantasticsource.mctools.gui.screen;

import com.fantasticsource.mctools.gui.GUIScreen;
import com.fantasticsource.mctools.gui.element.GUIElement;
import com.fantasticsource.mctools.gui.element.other.GUIDarkenedBackground;
import com.fantasticsource.mctools.gui.element.other.GUIGradient;
import com.fantasticsource.mctools.gui.element.other.GUIGradientBorder;
import com.fantasticsource.mctools.gui.element.other.GUIHorizontalSlider;
import com.fantasticsource.mctools.gui.element.text.*;
import com.fantasticsource.mctools.gui.element.text.filter.FilterColor;
import com.fantasticsource.mctools.gui.element.text.filter.FilterInt;
import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedFloat;
import com.fantasticsource.mctools.gui.element.text.filter.FilterRangedInt;
import com.fantasticsource.mctools.gui.element.view.GUIView;
import com.fantasticsource.tools.datastructures.Color;
import net.minecraft.client.Minecraft;

public class ColorSelectionGUI extends GUIScreen
{
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


        Color color = clickedElement.getValue();


        //Setup
        GUIView left = new GUIView(this, 0.3, 1);
        GUIView center = new GUIView(this, 0.3, 1);
        GUIView right = new GUIView(this, 0.3, 1);

        FilterRangedInt filter0to255 = FilterRangedInt.get(0, 255);
        FilterRangedFloat filter0to1 = FilterRangedFloat.get(0, 1);

        GUILabeledTextInput
                hex = new GUILabeledTextInput(this, "Hex: ", color.hex8(), FilterColor.INSTANCE),
                dec = new GUILabeledTextInput(this, "Dec: ", "" + color.color(), FilterInt.INSTANCE),
                r = new GUILabeledTextInput(this, "Red (0-255): ", "" + color.r(), filter0to255),
                g = new GUILabeledTextInput(this, "Green (0-255): ", "" + color.g(), filter0to255),
                b = new GUILabeledTextInput(this, "Blue (0-255): ", "" + color.b(), filter0to255),
                a = new GUILabeledTextInput(this, "Alpha (0-255): ", "" + color.a(), filter0to255),
                rf = new GUILabeledTextInput(this, "Red (0-1): ", "" + color.rf(), filter0to1),
                gf = new GUILabeledTextInput(this, "Green (0-1): ", "" + color.gf(), filter0to1),
                bf = new GUILabeledTextInput(this, "Blue (0-1): ", "" + color.bf(), filter0to1),
                af = new GUILabeledTextInput(this, "Alpha (0-1): ", "" + color.af(), filter0to1);

        GUIHorizontalSlider
                rSlider = new GUIHorizontalSlider(this, 1, 0.03, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK),
                gSlider = new GUIHorizontalSlider(this, 1, 0.03, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK),
                bSlider = new GUIHorizontalSlider(this, 1, 0.03, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK),
                aSlider = new GUIHorizontalSlider(this, 1, 0.03, Color.GRAY, Color.BLANK, Color.WHITE, Color.BLANK);

        rSlider.setAmount(color.rf());
        gSlider.setAmount(color.gf());
        bSlider.setAmount(color.bf());
        aSlider.setAmount(color.af());

        GUIGradient preview = new GUIGradient(this, 1, 0.3, color);

        GUITextButton
                save = new GUITextButton(this, "Save", Color.GREEN),
                cancel = new GUITextButton(this, "Cancel", Color.RED);


        //Root
        root.add(new GUIDarkenedBackground(this));
        root.add(new GUINavbar(this));

        root.add(save.addClickActions(() ->
        {
            clickedElement.setValue(color);
            close();
        }));
        root.add(cancel.addClickActions(this::close));
        root.add(new GUIGradientBorder(this, 1, 0.01, 1, Color.GRAY, Color.BLANK));

        root.addAll(new GUIElement(this, 0.025, 1), left, new GUIElement(this, 0.025, 1), center, new GUIElement(this, 0.025, 1), right);


        //Left
        left.add(new GUITextSpacer(this));
        left.add(r);
        r.input.addEditActions(() ->
        {
            if (r.input.isActive() && r.input.valid())
            {
                color.setR(filter0to255.parse(r.input.getText()));
                preview.setColor(color);
                hex.setInput(color.hex8(), false);
                dec.setInput("" + color.color(), false);
                rSlider.setAmount(color.rf(), false);
                rf.setInput("" + color.rf(), false);
            }
        });
        left.add(rSlider);
        rSlider.addEditActions(() ->
        {
            double amount = rSlider.getAmount();
            if (amount >= 0 && amount <= 1)
            {
                color.setRF((float) amount);
                preview.setColor(color);
                hex.setInput(color.hex8(), false);
                dec.setInput("" + color.color(), false);
                r.setInput("" + color.r(), false);
                rf.setInput("" + color.rf(), false);
            }
        });
        left.add(rf);
        rf.input.addEditActions(() ->
        {
            if (rf.input.isActive() && rf.input.valid())
            {
                color.setRF(filter0to1.parse(rf.input.getText()));
                preview.setColor(color);
                hex.setInput(color.hex8(), false);
                dec.setInput("" + color.color(), false);
                r.setInput("" + color.r(), false);
                rSlider.setAmount(color.rf(), false);
            }
        });
        left.add(new GUITextSpacer(this, 1, 2));
        left.add(g);
        g.input.addEditActions(() ->
        {
            if (g.input.isActive() && g.input.valid())
            {
                color.setG(filter0to255.parse(g.input.getText()));
                preview.setColor(color);
                hex.setInput(color.hex8(), false);
                dec.setInput("" + color.color(), false);
                gSlider.setAmount(color.gf(), false);
                gf.setInput("" + color.gf(), false);
            }
        });
        left.add(gSlider);
        gSlider.addEditActions(() ->
        {
            double amount = gSlider.getAmount();
            if (amount >= 0 && amount <= 1)
            {
                color.setGF((float) amount);
                preview.setColor(color);
                hex.setInput(color.hex8(), false);
                dec.setInput("" + color.color(), false);
                g.setInput("" + color.g(), false);
                gf.setInput("" + color.gf(), false);
            }
        });
        left.add(gf);
        gf.input.addEditActions(() ->
        {
            if (gf.input.isActive() && gf.input.valid())
            {
                color.setGF(filter0to1.parse(gf.input.getText()));
                preview.setColor(color);
                hex.setInput(color.hex8(), false);
                dec.setInput("" + color.color(), false);
                g.setInput("" + color.g(), false);
                gSlider.setAmount(color.gf(), false);
            }
        });
        left.add(new GUITextSpacer(this, 1, 2));
        left.add(b);
        b.input.addEditActions(() ->
        {
            if (b.input.isActive() && b.input.valid())
            {
                color.setB(filter0to255.parse(b.input.getText()));
                preview.setColor(color);
                hex.setInput(color.hex8(), false);
                dec.setInput("" + color.color(), false);
                bSlider.setAmount(color.bf(), false);
                bf.setInput("" + color.bf(), false);
            }
        });
        left.add(bSlider);
        bSlider.addEditActions(() ->
        {
            double amount = bSlider.getAmount();
            if (amount >= 0 && amount <= 1)
            {
                color.setBF((float) amount);
                preview.setColor(color);
                hex.setInput(color.hex8(), false);
                dec.setInput("" + color.color(), false);
                b.setInput("" + color.b(), false);
                bf.setInput("" + color.bf(), false);
            }
        });
        left.add(bf);
        bf.input.addEditActions(() ->
        {
            if (bf.input.isActive() && bf.input.valid())
            {
                color.setBF(filter0to1.parse(bf.input.getText()));
                preview.setColor(color);
                hex.setInput(color.hex8(), false);
                dec.setInput("" + color.color(), false);
                b.setInput("" + color.b(), false);
                bSlider.setAmount(color.bf(), false);
            }
        });
        left.add(new GUITextSpacer(this, 1, 2));
        left.add(a);
        a.input.addEditActions(() ->
        {
            if (a.input.isActive() && a.input.valid())
            {
                color.setA(filter0to255.parse(a.input.getText()));
                preview.setColor(color);
                hex.setInput(color.hex8(), false);
                dec.setInput("" + color.color(), false);
                aSlider.setAmount(color.af(), false);
                af.setInput("" + color.af(), false);
            }
        });
        left.add(aSlider);
        aSlider.addEditActions(() ->
        {
            double amount = aSlider.getAmount();
            if (amount >= 0 && amount <= 1)
            {
                color.setAF((float) amount);
                preview.setColor(color);
                hex.setInput(color.hex8(), false);
                dec.setInput("" + color.color(), false);
                a.setInput("" + color.a(), false);
                af.setInput("" + color.af(), false);
            }
        });
        left.add(af);
        af.input.addEditActions(() ->
        {
            if (af.input.isActive() && af.input.valid())
            {
                color.setAF(filter0to1.parse(af.input.getText()));
                preview.setColor(color);
                hex.setInput(color.hex8(), false);
                dec.setInput("" + color.color(), false);
                a.setInput("" + color.a(), false);
                aSlider.setAmount(color.af(), false);
            }
        });


        //Center
        center.add(new GUITextSpacer(this));
        center.add(hex);
        hex.input.addEditActions(() ->
        {
            if (hex.input.isActive() && hex.input.valid())
            {
                color.setColor(FilterColor.INSTANCE.parse(hex.input.getText()));
                preview.setColor(color);
                dec.setInput("" + color.color(), false);
                r.setInput("" + color.r(), false);
                g.setInput("" + color.g(), false);
                b.setInput("" + color.b(), false);
                a.setInput("" + color.a(), false);
                rf.setInput("" + color.rf(), false);
                gf.setInput("" + color.gf(), false);
                bf.setInput("" + color.bf(), false);
                af.setInput("" + color.af(), false);
                rSlider.setAmount(color.rf(), false);
                gSlider.setAmount(color.gf(), false);
                bSlider.setAmount(color.bf(), false);
                aSlider.setAmount(color.af(), false);
            }
        });
        center.add(new GUITextSpacer(this));
        center.add(dec);
        dec.input.addEditActions(() ->
        {
            if (dec.input.isActive() && dec.input.valid())
            {
                color.setColor(FilterInt.INSTANCE.parse(dec.input.getText()));
                preview.setColor(color);
                hex.setInput(color.hex8(), false);
                r.setInput("" + color.r(), false);
                g.setInput("" + color.g(), false);
                b.setInput("" + color.b(), false);
                a.setInput("" + color.a(), false);
                rf.setInput("" + color.rf(), false);
                gf.setInput("" + color.gf(), false);
                bf.setInput("" + color.bf(), false);
                af.setInput("" + color.af(), false);
                rSlider.setAmount(color.rf(), false);
                gSlider.setAmount(color.gf(), false);
                bSlider.setAmount(color.bf(), false);
                aSlider.setAmount(color.af(), false);
            }
        });
        center.add(new GUITextSpacer(this));
        center.add(preview);


        //Right
        right.add(new GUITextSpacer(this));
    }

    @Override
    public String title()
    {
        return "Color";
    }
}
