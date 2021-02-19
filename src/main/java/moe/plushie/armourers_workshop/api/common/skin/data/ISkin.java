package moe.plushie.armourers_workshop.api.common.skin.data;

import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;

import java.util.ArrayList;

public interface ISkin {
    
    public ISkinType getSkinType();
    
    public ArrayList<ISkinPart> getSubParts();
}
