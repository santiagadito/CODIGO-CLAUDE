package com.guillotina.granizados;

import com.guillotina.granizados.screen.GuilotinaMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, GranizadosMod.MOD_ID);

    public static final RegistryObject<MenuType<GuilotinaMenu>> GUILLOTINA_MENU =
            MENUS.register("guillotina",
                    () -> IForgeMenuType.create(GuilotinaMenu::new));
}
