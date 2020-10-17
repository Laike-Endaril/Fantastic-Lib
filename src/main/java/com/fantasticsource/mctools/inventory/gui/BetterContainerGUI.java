package com.fantasticsource.mctools.inventory.gui;

import com.fantasticsource.mctools.inventory.slot.BetterSlot;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

import static org.lwjgl.opengl.GL11.GL_QUADS;

@SideOnly(Side.CLIENT)
public class BetterContainerGUI extends GuiContainer
{
    protected boolean buttonClicked;
    protected Slot hoveredSlot;
    protected ItemStack draggedStack = ItemStack.EMPTY;
    protected boolean isRightMouseClick;
    protected int dragSplittingRemnant;
    protected ItemStack returningStack = ItemStack.EMPTY;
    protected long returningStackTime;
    protected Slot returningStackDestSlot;
    protected int touchUpX;
    protected int touchUpY;
    protected boolean doubleClick;
    protected Slot lastClickSlot;
    protected long lastClickTime;
    protected int lastClickButton;
    protected boolean ignoreMouseUp;
    protected Slot clickedSlot;
    protected ItemStack shiftClickedStack = ItemStack.EMPTY;
    protected int dragSplittingButton;
    protected int dragSplittingLimit;

    public BetterContainerGUI(Container container)
    {
        super(container);
        ignoreMouseUp = true;
        allowUserInput = true;

        mc = Minecraft.getMinecraft();
        fontRenderer = mc.fontRenderer;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        drawDefaultBackground();

        drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        GlStateManager.disableRescaleNormal();
        RenderHelper.disableStandardItemLighting();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();

        for (GuiButton button : buttonList) button.drawButton(mc, mouseX, mouseY, partialTicks);
        for (GuiLabel label : labelList) label.drawLabel(mc, mouseX, mouseY);

        if (inventorySlots != null)
        {
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) guiLeft, (float) guiTop, 0);
            GlStateManager.color(1, 1, 1, 1);
            GlStateManager.enableRescaleNormal();
            hoveredSlot = null;
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240, 240);
            GlStateManager.color(1, 1, 1, 1);

            for (int i1 = 0; i1 < inventorySlots.inventorySlots.size(); ++i1)
            {
                Slot slot = inventorySlots.inventorySlots.get(i1);

                if (slot.isEnabled())
                {
                    drawSlot(slot);
                }

                if (isMouseOverSlot(slot, mouseX, mouseY) && slot.isEnabled())
                {
                    hoveredSlot = slot;
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                    GlStateManager.colorMask(true, true, true, false);
                    drawGradientRect(slot.xPos, slot.yPos, slot.xPos + 16, slot.yPos + 16, -2130706433, -2130706433);
                    GlStateManager.colorMask(true, true, true, true);
                    GlStateManager.enableLighting();
                    GlStateManager.enableDepth();
                }
            }

            RenderHelper.disableStandardItemLighting();
            drawGuiContainerForegroundLayer(mouseX, mouseY);
            RenderHelper.enableGUIStandardItemLighting();
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiContainerEvent.DrawForeground(this, mouseX, mouseY));
            InventoryPlayer inventoryplayer = mc.player.inventory;
            ItemStack itemstack = draggedStack.isEmpty() ? inventoryplayer.getItemStack() : draggedStack;

            if (!itemstack.isEmpty())
            {
                String s = null;

                if (!draggedStack.isEmpty() && isRightMouseClick)
                {
                    itemstack = itemstack.copy();
                    itemstack.setCount(MathHelper.ceil((float) itemstack.getCount() / 2));
                }
                else if (dragSplitting && dragSplittingSlots.size() > 1)
                {
                    itemstack = itemstack.copy();
                    itemstack.setCount(dragSplittingRemnant);

                    if (itemstack.isEmpty())
                    {
                        s = "" + TextFormatting.YELLOW + "0";
                    }
                }

                drawItemStack(itemstack, mouseX - guiLeft - 8, mouseY - guiTop - (draggedStack.isEmpty() ? 8 : 16), s);
            }

            if (!returningStack.isEmpty())
            {
                float f = (float) (Minecraft.getSystemTime() - returningStackTime) / 100;

                if (f >= 1)
                {
                    f = 1;
                    returningStack = ItemStack.EMPTY;
                }

                int l2 = returningStackDestSlot.xPos - touchUpX;
                int i3 = returningStackDestSlot.yPos - touchUpY;
                int l1 = touchUpX + (int) ((float) l2 * f);
                int i2 = touchUpY + (int) ((float) i3 * f);
                drawItemStack(returningStack, l1, i2, null);
            }

            GlStateManager.popMatrix();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            RenderHelper.enableStandardItemLighting();

            renderHoveredToolTip(mouseX, mouseY);
        }
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
    }

    protected void actionPerformed(GuiButton button)
    {
        buttonClicked = true;
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        if (mouseButton == 0)
        {
            for (int i = 0; i < buttonList.size(); ++i)
            {
                GuiButton guibutton = buttonList.get(i);

                if (guibutton.mousePressed(mc, mouseX, mouseY))
                {
                    net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event = new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre(this, guibutton, buttonList);
                    if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event))
                        break;
                    guibutton = event.getButton();
                    selectedButton = guibutton;
                    guibutton.playPressSound(mc.getSoundHandler());
                    actionPerformed(guibutton);
                    if (equals(mc.currentScreen))
                        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Post(this, event.getButton(), buttonList));
                }
            }
        }


        //Inventory
        if (inventorySlots != null)
        {
            boolean clickedOutside = hasClickedOutside(mouseX, mouseY);
            long i = Minecraft.getSystemTime();
            Slot slot = getSlotAtPosition(mouseX, mouseY);
            doubleClick = lastClickSlot == slot && i - lastClickTime < 250 && lastClickButton == mouseButton;
            ignoreMouseUp = false;

            if (mouseButton == 0 || mouseButton == 1 || mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100))
            {
                if (slot != null) clickedOutside = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
                int l = -1;

                if (slot != null)
                {
                    l = slot.slotNumber;
                }

                if (clickedOutside)
                {
                    l = -999;
                }

                if (l != -1)
                {
                    if (!dragSplitting)
                    {
                        if (mc.player.inventory.getItemStack().isEmpty())
                        {
                            if (mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100))
                            {
                                handleMouseClick(slot, l, mouseButton, ClickType.CLONE);
                            }
                            else
                            {
                                boolean flag2 = l != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));
                                ClickType clicktype = ClickType.PICKUP;

                                if (flag2)
                                {
                                    shiftClickedStack = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
                                    clicktype = ClickType.QUICK_MOVE;
                                }
                                else if (l == -999)
                                {
                                    clicktype = ClickType.THROW;
                                }

                                handleMouseClick(slot, l, mouseButton, clicktype);
                            }

                            ignoreMouseUp = true;
                        }
                        else
                        {
                            dragSplitting = true;
                            dragSplittingButton = mouseButton;
                            dragSplittingSlots.clear();

                            if (mouseButton == 0)
                            {
                                dragSplittingLimit = 0;
                            }
                            else if (mouseButton == 1)
                            {
                                dragSplittingLimit = 1;
                            }
                            else if (mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100))
                            {
                                dragSplittingLimit = 2;
                            }
                        }
                    }
                }
            }

            lastClickSlot = slot;
            lastClickTime = i;
            lastClickButton = mouseButton;
        }
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick)
    {
        //Inventory slots
        if (inventorySlots != null)
        {
            Slot slot = getSlotAtPosition(mouseX, mouseY);
            ItemStack itemstack = mc.player.inventory.getItemStack();


            if (dragSplitting && slot != null && !itemstack.isEmpty() && (itemstack.getCount() > dragSplittingSlots.size() || dragSplittingLimit == 2) && Container.canAddItemToSlot(slot, itemstack, true) && slot.isItemValid(itemstack) && inventorySlots.canDragIntoSlot(slot))
            {
                dragSplittingSlots.add(slot);
                updateDragSplitting();
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (buttonClicked) buttonClicked = false;
        else
        {
            if (selectedButton != null && state == 0)
            {
                selectedButton.mouseReleased(mouseX, mouseY);
                selectedButton = null;
            }

            Slot slot = getSlotAtPosition(mouseX, mouseY);
            boolean clickedOutside = hasClickedOutside(mouseX, mouseY);
            if (slot != null) clickedOutside = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
            int k = -1;

            if (slot != null)
            {
                k = slot.slotNumber;
            }

            if (clickedOutside)
            {
                k = -999;
            }

            if (doubleClick && slot != null && state == 0 && inventorySlots.canMergeSlot(ItemStack.EMPTY, slot))
            {
                if (isShiftKeyDown())
                {
                    if (!shiftClickedStack.isEmpty())
                    {
                        for (Slot slot2 : inventorySlots.inventorySlots)
                        {
                            if (slot2 != null && slot2.canTakeStack(mc.player) && slot2.getHasStack() && slot2.isSameInventory(slot) && Container.canAddItemToSlot(slot2, shiftClickedStack, true))
                            {
                                handleMouseClick(slot2, slot2.slotNumber, state, ClickType.QUICK_MOVE);
                            }
                        }
                    }
                }
                else
                {
                    handleMouseClick(slot, k, state, ClickType.PICKUP_ALL);
                }

                doubleClick = false;
                lastClickTime = 0L;
            }
            else
            {
                if (dragSplitting && dragSplittingButton != state)
                {
                    dragSplitting = false;
                    dragSplittingSlots.clear();
                    ignoreMouseUp = true;
                    return;
                }

                if (ignoreMouseUp)
                {
                    ignoreMouseUp = false;
                    return;
                }

                if (dragSplitting && !dragSplittingSlots.isEmpty())
                {
                    handleMouseClick(null, -999, Container.getQuickcraftMask(0, dragSplittingLimit), ClickType.QUICK_CRAFT);

                    for (Slot slot1 : dragSplittingSlots)
                    {
                        handleMouseClick(slot1, slot1.slotNumber, Container.getQuickcraftMask(1, dragSplittingLimit), ClickType.QUICK_CRAFT);
                    }

                    handleMouseClick(null, -999, Container.getQuickcraftMask(2, dragSplittingLimit), ClickType.QUICK_CRAFT);
                }
                else if (!mc.player.inventory.getItemStack().isEmpty())
                {
                    if (mc.gameSettings.keyBindPickBlock.isActiveAndMatches(state - 100))
                    {
                        handleMouseClick(slot, k, state, ClickType.CLONE);
                    }
                    else
                    {
                        boolean flag1 = k != -999 && (Keyboard.isKeyDown(42) || Keyboard.isKeyDown(54));

                        if (flag1)
                        {
                            shiftClickedStack = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
                        }

                        handleMouseClick(slot, k, state, flag1 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
                    }
                }
            }

            if (mc.player.inventory.getItemStack().isEmpty())
            {
                lastClickTime = 0L;
            }

            dragSplitting = false;
        }
    }

    protected boolean hasClickedOutside(int mouseX, int mouseY)
    {
        return mouseX < guiLeft || mouseY < guiTop || mouseX >= guiLeft + this.xSize || mouseY >= guiTop + this.ySize;
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY)
    {
        if (mc.player.inventory.getItemStack().isEmpty() && hoveredSlot != null && hoveredSlot.getHasStack() && (!(hoveredSlot instanceof BetterSlot) || ((BetterSlot) hoveredSlot).enabled))
        {
            renderToolTip(hoveredSlot.getStack(), mouseX, mouseY);
        }
    }

    @Override
    public void drawWorldBackground(int tint)
    {
        if (mc.world == null) drawBackground(tint);
    }

    @Override
    protected void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor)
    {
        //In this case, this is used for item slot highlighting only
        if (hoveredSlot == null || (hoveredSlot instanceof BetterSlot && !((BetterSlot) hoveredSlot).enabled)) return;

        super.drawGradientRect(left, top, right, bottom, startColor, endColor);
    }

    protected void drawSlot(Slot slot)
    {
        if (slot instanceof BetterSlot && !((BetterSlot) slot).enabled) return;


        int x = slot.xPos;
        int y = slot.yPos;
        ItemStack itemstack = slot.getStack();
        boolean flag = false;
        boolean flag1 = slot == clickedSlot && !draggedStack.isEmpty() && !isRightMouseClick;
        ItemStack itemstack1 = mc.player.inventory.getItemStack();
        String s = null;

        if (slot == clickedSlot && !draggedStack.isEmpty() && isRightMouseClick && !itemstack.isEmpty())
        {
            itemstack = itemstack.copy();
            itemstack.setCount(itemstack.getCount() >> 1);
        }
        else if (dragSplitting && dragSplittingSlots.contains(slot) && !itemstack1.isEmpty())
        {
            if (dragSplittingSlots.size() == 1)
            {
                return;
            }

            if (Container.canAddItemToSlot(slot, itemstack1, true) && inventorySlots.canDragIntoSlot(slot))
            {
                itemstack = itemstack1.copy();
                flag = true;
                Container.computeStackSize(dragSplittingSlots, dragSplittingLimit, itemstack, slot.getStack().isEmpty() ? 0 : slot.getStack().getCount());
                int k = Math.min(itemstack.getMaxStackSize(), slot.getItemStackLimit(itemstack));

                if (itemstack.getCount() > k)
                {
                    s = TextFormatting.YELLOW.toString() + k;
                    itemstack.setCount(k);
                }
            }
            else
            {
                dragSplittingSlots.remove(slot);
                updateDragSplitting();
            }
        }

        zLevel = 100;
        itemRender.zLevel = 100;

        if (itemstack.isEmpty() && slot.isEnabled())
        {
            GlStateManager.disableLighting();

            if (slot instanceof BetterSlot && ((BetterSlot) slot).u >= 0 && ((BetterSlot) slot).v >= 0)
            {
                BetterSlot betterSlot = (BetterSlot) slot;
                int u = betterSlot.u, v = betterSlot.v;

                mc.getTextureManager().bindTexture(betterSlot.texture);

                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder bufferbuilder = tessellator.getBuffer();
                bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX);
                bufferbuilder.pos(x, y + 16, zLevel).tex(u * betterSlot.uPixel, (v + 16) * betterSlot.vPixel).endVertex();
                bufferbuilder.pos(x + 16, y + 16, zLevel).tex((u + 16) * betterSlot.uPixel, (v + 16) * betterSlot.vPixel).endVertex();
                bufferbuilder.pos(x + 16, y, zLevel).tex((u + 16) * betterSlot.uPixel, v * betterSlot.vPixel).endVertex();
                bufferbuilder.pos(x, y, zLevel).tex(u * betterSlot.uPixel, v * betterSlot.vPixel).endVertex();
                tessellator.draw();
            }
            else
            {
                TextureAtlasSprite textureatlassprite = slot.getBackgroundSprite();
                if (textureatlassprite != null)
                {
                    mc.getTextureManager().bindTexture(slot.getBackgroundLocation());
                    drawTexturedModalRect(x, y, textureatlassprite, 16, 16);
                }
            }

            GlStateManager.enableLighting();
            flag1 = true;
        }

        if (!flag1)
        {
            if (flag)
            {
                drawRect(x, y, x + 16, y + 16, -2130706433);
            }

            GlStateManager.enableDepth();
            itemRender.renderItemAndEffectIntoGUI(mc.player, itemstack, x, y);
            itemRender.renderItemOverlayIntoGUI(fontRenderer, itemstack, x, y, s);
        }

        itemRender.zLevel = 0;
        zLevel = 0;
    }

    protected void updateDragSplitting()
    {
        ItemStack itemstack = mc.player.inventory.getItemStack();

        if (!itemstack.isEmpty() && dragSplitting)
        {
            if (dragSplittingLimit == 2)
            {
                dragSplittingRemnant = itemstack.getMaxStackSize();
            }
            else
            {
                dragSplittingRemnant = itemstack.getCount();

                for (Slot slot : dragSplittingSlots)
                {
                    ItemStack itemstack1 = itemstack.copy();
                    ItemStack itemstack2 = slot.getStack();
                    int i = itemstack2.isEmpty() ? 0 : itemstack2.getCount();
                    Container.computeStackSize(dragSplittingSlots, dragSplittingLimit, itemstack1, i);
                    int j = Math.min(itemstack1.getMaxStackSize(), slot.getItemStackLimit(itemstack1));

                    if (itemstack1.getCount() > j)
                    {
                        itemstack1.setCount(j);
                    }

                    dragSplittingRemnant -= itemstack1.getCount() - i;
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode)
    {
        if (keyCode == 1) mc.player.closeScreen();

        checkHotbarKeys(keyCode);

        if (hoveredSlot != null && hoveredSlot.getHasStack())
        {
            if (mc.gameSettings.keyBindPickBlock.isActiveAndMatches(keyCode))
            {
                handleMouseClick(hoveredSlot, hoveredSlot.slotNumber, 0, ClickType.CLONE);
            }
            else if (mc.gameSettings.keyBindDrop.isActiveAndMatches(keyCode))
            {
                handleMouseClick(hoveredSlot, hoveredSlot.slotNumber, isCtrlKeyDown() ? 1 : 0, ClickType.THROW);
            }
        }
    }

    @Override
    protected boolean checkHotbarKeys(int keyCode)
    {
        if (mc.player.inventory.getItemStack().isEmpty() && hoveredSlot != null)
        {
            for (int i = 0; i < 9; ++i)
            {
                if (mc.gameSettings.keyBindsHotbar[i].isActiveAndMatches(keyCode))
                {
                    handleMouseClick(hoveredSlot, hoveredSlot.slotNumber, i, ClickType.SWAP);
                    return true;
                }
            }
        }

        return false;
    }

    protected boolean isMouseOverSlot(Slot slotIn, int mouseX, int mouseY)
    {
        return isPointInRegion(slotIn.xPos, slotIn.yPos, 16, 16, mouseX, mouseY);
    }

    protected void drawItemStack(ItemStack stack, int x, int y, String altText)
    {
        GlStateManager.translate(0, 0, 32);
        zLevel = 200;
        itemRender.zLevel = 200;
        net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
        if (font == null) font = fontRenderer;
        itemRender.renderItemAndEffectIntoGUI(stack, x, y);
        itemRender.renderItemOverlayIntoGUI(font, stack, x, y - (draggedStack.isEmpty() ? 0 : 8), altText);
        zLevel = 0;
        itemRender.zLevel = 0;
    }

    protected Slot getSlotAtPosition(int x, int y)
    {
        if (inventorySlots == null) return null;

        for (int i = 0; i < inventorySlots.inventorySlots.size(); ++i)
        {
            Slot slot = inventorySlots.inventorySlots.get(i);

            if (isMouseOverSlot(slot, x, y) && slot.isEnabled())
            {
                return slot;
            }
        }

        return null;
    }


    @Override
    public Slot getSlotUnderMouse()
    {
        return hoveredSlot;
    }

    @Override
    public int getGuiLeft()
    {
        return guiLeft;
    }

    @Override
    public int getGuiTop()
    {
        return guiTop;
    }

    @Override
    public int getXSize()
    {
        return xSize;
    }

    @Override
    public int getYSize()
    {
        return ySize;
    }
}
