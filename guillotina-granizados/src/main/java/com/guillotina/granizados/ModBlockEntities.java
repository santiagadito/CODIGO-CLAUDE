package com.guillotina.granizados;

import com.guillotina.granizados.blockentity.GuilotinaBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GranizadosMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<GuilotinaBlockEntity>> GUILLOTINA_BE =
            BLOCK_ENTITIES.register("guillotina",
                    () -> BlockEntityType.Builder
                            .of(GuilotinaBlockEntity::new, ModBlocks.GUILLOTINA.get())
                            .build(null));
}
