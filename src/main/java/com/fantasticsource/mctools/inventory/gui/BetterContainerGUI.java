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
    protected boolean buttonClicked_;
    protected Slot hoveredSlot_;
    protected ItemStack draggedStack_ = ItemStack.EMPTY;
    protected boolean isRightMouseClick_;
    protected int dragSplittingRemnant_;
    protected ItemStack returningStack_ = ItemStack.EMPTY;
    protected long returningStackTime_;
    protected Slot returningStackDestSlot_;
    protected int touchUpX_;
    protected int touchUpY_;
    protected boolean doubleClick_;
    protected Slot lastClickSlot_;
    protected long lastClickTime_;
    protected int lastClickButton_;
    protected boolean ignoreMouseUp_;
    protected Slot clickedSlot_;
    protected ItemStack shiftClickedStack_ = ItemStack.EMPTY;
    protected int dragSplittingButton_;
    protected int dragSplittingLimit_;

    public BetterContainerGUI(Container container)
    {
        super(container);
        ignoreMouseUp_ = true;
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


        GlStateManager.pushMatrix();
        GlStateManager.translate((float) guiLeft, (float) guiTop, 0);
        GlStateManager.color(1, 1, 1, 1);

        if (inventorySlots != null)
        {
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.enableRescaleNormal();
            hoveredSlot_ = null;
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
                    hoveredSlot_ = slot;
                    GlStateManager.disableLighting();
                    GlStateManager.disableDepth();
                    GlStateManager.colorMask(true, true, true, false);
                    drawGradientRect(slot.xPos, slot.yPos, slot.xPos + 16, slot.yPos + 16, -2130706433, -2130706433);
                    GlStateManager.colorMask(true, true, true, true);
                    GlStateManager.enableLighting();
                    GlStateManager.enableDepth();
                }
            }
        }


        RenderHelper.disableStandardItemLighting();
        drawGuiContainerForegroundLayer(mouseX, mouseY);
        RenderHelper.enableGUIStandardItemLighting();
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiContainerEvent.DrawForeground(this, mouseX, mouseY));


        GlStateManager.popMatrix();
        if (inventorySlots != null)
        {
            InventoryPlayer inventoryplayer = mc.player.inventory;
            ItemStack itemstack = draggedStack_.isEmpty() ? inventoryplayer.getItemStack() : draggedStack_;

            if (!itemstack.isEmpty())
            {
                String s = null;

                if (!draggedStack_.isEmpty() && isRightMouseClick_)
                {
                    itemstack = itemstack.copy();
                    itemstack.setCount(MathHelper.ceil((float) itemstack.getCount() / 2));
                }
                else if (dragSplitting && dragSplittingSlots.size() > 1)
                {
                    itemstack = itemstack.copy();
                    itemstack.setCount(dragSplittingRemnant_);

                    if (itemstack.isEmpty())
                    {
                        s = "" + TextFormatting.YELLOW + "0";
                    }
                }

                drawItemStack(itemstack, mouseX - 8, mouseY - (draggedStack_.isEmpty() ? 8 : 16), s);
            }

            if (!returningStack_.isEmpty())
            {
                float f = (float) (Minecraft.getSystemTime() - returningStackTime_) / 100;

                if (f >= 1)
                {
                    f = 1;
                    returningStack_ = ItemStack.EMPTY;
                }

                int l2 = returningStackDestSlot_.xPos - touchUpX_;
                int i3 = returningStackDestSlot_.yPos - touchUpY_;
                int l1 = touchUpX_ + (int) ((float) l2 * f);
                int i2 = touchUpY_ + (int) ((float) i3 * f);
                drawItemStack(returningStack_, l1, i2, null);
            }

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
        buttonClicked_ = true;
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
            doubleClick_ = lastClickSlot_ == slot && i - lastClickTime_ < 250 && lastClickButton_ == mouseButton;
            ignoreMouseUp_ = false;

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
                                    shiftClickedStack_ = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
                                    clicktype = ClickType.QUICK_MOVE;
                                }
                                else if (l == -999)
                                {
                                    clicktype = ClickType.THROW;
                                }

                                handleMouseClick(slot, l, mouseButton, clicktype);
                            }

                            ignoreMouseUp_ = true;
                        }
                        else
                        {
                            dragSplitting = true;
                            dragSplittingButton_ = mouseButton;
                            dragSplittingSlots.clear();

                            if (mouseButton == 0)
                            {
                                dragSplittingLimit_ = 0;
                            }
                            else if (mouseButton == 1)
                            {
                                dragSplittingLimit_ = 1;
                            }
                            else if (mc.gameSettings.keyBindPickBlock.isActiveAndMatches(mouseButton - 100))
                            {
                                dragSplittingLimit_ = 2;
                            }
                        }
                    }
                }
            }

            lastClickSlot_ = slot;
            lastClickTime_ = i;
            lastClickButton_ = mouseButton;
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


            if (dragSplitting && slot != null && !itemstack.isEmpty() && (itemstack.getCount() > dragSplittingSlots.size() || dragSplittingLimit_ == 2) && Container.canAddItemToSlot(slot, itemstack, true) && slot.isItemValid(itemstack) && inventorySlots.canDragIntoSlot(slot))
            {
                dragSplittingSlots.add(slot);
                updateDragSplitting();
            }
        }
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state)
    {
        if (buttonClicked_) buttonClicked_ = false;
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

            if (doubleClick_ && slot != null && state == 0 && inventorySlots.canMergeSlot(ItemStack.EMPTY, slot))
            {
                if (isShiftKeyDown())
                {
                    if (!shiftClickedStack_.isEmpty())
                    {
                        for (Slot slot2 : inventorySlots.inventorySlots)
                        {
                            if (slot2 != null && slot2.canTakeStack(mc.player) && slot2.getHasStack() && slot2.isSameInventory(slot) && Container.canAddItemToSlot(slot2, shiftClickedStack_, true))
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

                doubleClick_ = false;
                lastClickTime_ = 0L;
            }
            else
            {
                if (dragSplitting && dragSplittingButton_ != state)
                {
                    dragSplitting = false;
                    dragSplittingSlots.clear();
                    ignoreMouseUp_ = true;
                    return;
                }

                if (ignoreMouseUp_)
                {
                    ignoreMouseUp_ = false;
                    return;
                }

                if (dragSplitting && !dragSplittingSlots.isEmpty())
                {
                    handleMouseClick(null, -999, Container.getQuickcraftMask(0, dragSplittingLimit_), ClickType.QUICK_CRAFT);

                    for (Slot slot1 : dragSplittingSlots)
                    {
                        handleMouseClick(slot1, slot1.slotNumber, Container.getQuickcraftMask(1, dragSplittingLimit_), ClickType.QUICK_CRAFT);
                    }

                    handleMouseClick(null, -999, Container.getQuickcraftMask(2, dragSplittingLimit_), ClickType.QUICK_CRAFT);
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
                            shiftClickedStack_ = slot != null && slot.getHasStack() ? slot.getStack().copy() : ItemStack.EMPTY;
                        }

                        handleMouseClick(slot, k, state, flag1 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
                    }
                }
            }

            if (mc.player.inventory.getItemStack().isEmpty())
            {
                lastClickTime_ = 0L;
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
        if (mc.player.inventory.getItemStack().isEmpty() && hoveredSlot_ != null && hoveredSlot_.getHasStack() && (!(hoveredSlot_ instanceof BetterSlot) || ((BetterSlot) hoveredSlot_).enabled))
        {
            renderToolTip(hoveredSlot_.getStack(), mouseX, mouseY);
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
        if (hoveredSlot_ == null || (hoveredSlot_ instanceof BetterSlot && !((BetterSlot) hoveredSlot_).enabled)) return;

        super.drawGradientRect(left, top, right, bottom, startColor, endColor);
    }

    protected void drawSlot(Slot slot)
    {
        if (slot instanceof BetterSlot && !((BetterSlot) slot).enabled) return;


        int x = slot.xPos;
        int y = slot.yPos;
        ItemStack itemstack = slot.getStack();
        boolean flag = false;
        boolean flag1 = slot == clickedSlot_ && !draggedStack_.isEmpty() && !isRightMouseClick_;
        ItemStack itemstack1 = mc.player.inventory.getItemStack();
        String s = null;

        if (slot == clickedSlot_ && !draggedStack_.isEmpty() && isRightMouseClick_ && !itemstack.isEmpty())
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
                Container.computeStackSize(dragSplittingSlots, dragSplittingLimit_, itemstack, slot.getStack().isEmpty() ? 0 : slot.getStack().getCount());
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
            if (dragSplittingLimit_ == 2)
            {
                dragSplittingRemnant_ = itemstack.getMaxStackSize();
            }
            else
            {
                dragSplittingRemnant_ = itemstack.getCount();

                for (Slot slot : dragSplittingSlots)
                {
                    ItemStack itemstack1 = itemstack.copy();
                    ItemStack itemstack2 = slot.getStack();
                    int i = itemstack2.isEmpty() ? 0 : itemstack2.getCount();
                    Container.computeStackSize(dragSplittingSlots, dragSplittingLimit_, itemstack1, i);
                    int j = Math.min(itemstack1.getMaxStackSize(), slot.getItemStackLimit(itemstack1));

                    if (itemstack1.getCount() > j)
                    {
                        itemstack1.setCount(j);
                    }

                    dragSplittingRemnant_ -= itemstack1.getCount() - i;
                }
            }
        }
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode)
    {
        if (keyCode == 1)
        {
            mc.player.closeScreen();
            return;
        }

        checkHotbarKeys(keyCode);

        if (hoveredSlot_ != null && hoveredSlot_.getHasStack())
        {
            if (mc.gameSettings.keyBindPickBlock.isActiveAndMatches(keyCode))
            {
                handleMouseClick(hoveredSlot_, hoveredSlot_.slotNumber, 0, ClickType.CLONE);
            }
            else if (mc.gameSettings.keyBindDrop.isActiveAndMatches(keyCode))
            {
                handleMouseClick(hoveredSlot_, hoveredSlot_.slotNumber, isCtrlKeyDown() ? 1 : 0, ClickType.THROW);
            }
        }
    }

    @Override
    protected boolean checkHotbarKeys(int keyCode)
    {
        if (mc.player.inventory.getItemStack().isEmpty() && hoveredSlot_ != null)
        {
            for (int i = 0; i < 9; ++i)
            {
                if (mc.gameSettings.keyBindsHotbar[i].isActiveAndMatches(keyCode))
                {
                    handleMouseClick(hoveredSlot_, hoveredSlot_.slotNumber, i, ClickType.SWAP);
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
        itemRender.renderItemOverlayIntoGUI(font, stack, x, y - (draggedStack_.isEmpty() ? 0 : 8), altText);
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
        return hoveredSlot_;
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
