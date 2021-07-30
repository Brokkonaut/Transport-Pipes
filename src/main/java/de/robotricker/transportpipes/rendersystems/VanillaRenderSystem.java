package de.robotricker.transportpipes.rendersystems;

import org.bukkit.inventory.ItemStack;

import de.robotricker.transportpipes.duct.Duct;
import de.robotricker.transportpipes.duct.DuctRegister;
import de.robotricker.transportpipes.duct.types.BaseDuctType;

public abstract class VanillaRenderSystem extends RenderSystem {

    public VanillaRenderSystem(BaseDuctType<? extends Duct> baseDuctType) {
        super(baseDuctType);
    }

    public static ItemStack getItem(DuctRegister ductRegister) {
        return ductRegister.baseDuctTypeOf("pipe").getItemManager().getClonedItem(ductRegister.baseDuctTypeOf("pipe").ductTypeOf("white"));
    }

    @SuppressWarnings("SameReturnValue")
    public static String getDisplayName() {
        return "VANILLA";
    }

}
