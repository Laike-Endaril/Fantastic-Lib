package com.fantasticsource.mctools.component;

import com.fantasticsource.mctools.potions.FantasticPotionEffect;
import com.fantasticsource.tools.component.CBoolean;
import com.fantasticsource.tools.component.CInt;
import com.fantasticsource.tools.component.Component;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class CFantasticPotionEffect extends Component
{
    public FantasticPotionEffect value = null;

    public CFantasticPotionEffect set(FantasticPotionEffect value)
    {
        this.value = value;
        return this;
    }

    @Override
    public CFantasticPotionEffect write(ByteBuf buf)
    {
        new CResourceLocation().set(value.getPotion().getRegistryName()).write(buf);
        buf.writeInt(value.getDuration());
        buf.writeInt(value.getAmplifier());
        buf.writeBoolean(value.getIsAmbient());
        buf.writeBoolean(value.doesShowParticles());

        buf.writeBoolean(value.getIsPotionDurationMax());

        CItemStack cstack = new CItemStack();
        List<ItemStack> cures = value.getCurativeItems();
        buf.writeInt(cures.size());
        for (ItemStack stack : cures) cstack.set(stack).write(buf);


        buf.writeInt(value.interval);

        return this;
    }

    @Override
    public CFantasticPotionEffect read(ByteBuf buf)
    {
        value = new FantasticPotionEffect(Potion.REGISTRY.getObject(new CResourceLocation().read(buf).value), buf.readInt(), buf.readInt(), buf.readBoolean(), buf.readBoolean());

        value.setPotionDurationMax(buf.readBoolean());

        CItemStack cstack = new CItemStack();
        ArrayList<ItemStack> cures = new ArrayList<>();
        for (int i = buf.readInt(); i > 0; i--) cures.add(cstack.read(buf).value);
        value.setCurativeItems(cures);


        value.interval = buf.readInt();

        return this;
    }

    @Override
    public CFantasticPotionEffect save(OutputStream stream)
    {
        CInt ci = new CInt();
        CBoolean cb = new CBoolean();

        new CResourceLocation().set(value.getPotion().getRegistryName()).save(stream);
        ci.set(value.getDuration()).save(stream).set(value.getAmplifier()).save(stream);
        cb.set(value.getIsAmbient()).save(stream).set(value.doesShowParticles()).save(stream).set(value.getIsPotionDurationMax()).save(stream);

        CItemStack cstack = new CItemStack();
        List<ItemStack> cures = value.getCurativeItems();
        ci.set(cures.size()).save(stream);
        for (ItemStack stack : cures) cstack.set(stack).save(stream);


        ci.set(value.interval).save(stream);

        return this;
    }

    @Override
    public CFantasticPotionEffect load(InputStream stream)
    {
        CInt ci = new CInt();
        CBoolean cb = new CBoolean();

        value = new FantasticPotionEffect(Potion.REGISTRY.getObject(new CResourceLocation().load(stream).value), ci.load(stream).value, ci.load(stream).value, cb.load(stream).value, cb.load(stream).value);

        value.setPotionDurationMax(cb.load(stream).value);

        CItemStack cstack = new CItemStack();
        ArrayList<ItemStack> cures = new ArrayList<>();
        for (int i = ci.load(stream).value; i > 0; i--) cures.add(cstack.load(stream).value);
        value.setCurativeItems(cures);


        value.interval = ci.load(stream).value;

        return this;
    }
}
