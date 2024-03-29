package net.teamabyssal.registry;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.teamabyssal.fight_or_die.FightOrDieMutations;

@EventBusSubscriber(modid = FightOrDieMutations.MODID, bus = EventBusSubscriber.Bus.MOD)
public class SoundRegistry {
    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, FightOrDieMutations.MODID);
    public static void register(IEventBus eventBus) {
        SOUNDS.register(eventBus);
    }

    private static RegistryObject<SoundEvent> soundRegistry(String id) {
        return SOUNDS.register(id, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(FightOrDieMutations.MODID, id)));
    }

    public static final RegistryObject<SoundEvent> ENTITY_MALRUPTOR_AMBIENT  = soundRegistry("entity.malruptor.ambient");
    public static final RegistryObject<SoundEvent> ENTITY_MALRUPTOR_HURT  = soundRegistry("entity.malruptor.hurt");
    public static final RegistryObject<SoundEvent> ENTITY_MALRUPTOR_DEATH = soundRegistry("entity.malruptor.death");
    public static final RegistryObject<SoundEvent> ENTITY_MALRUPTOR_INFECT = soundRegistry("entity.malruptor.infect");
    public static final RegistryObject<SoundEvent> ENTITY_ASSIMILATED_HUMAN_AMBIENT  = soundRegistry("entity.assimilated_human.ambient");
    public static final RegistryObject<SoundEvent> ENTITY_ASSIMILATED_VILLAGER_AMBIENT  = soundRegistry("entity.assimilated_villager.ambient");
    public static final RegistryObject<SoundEvent> ENTITY_ASSIMILATED_CREEPER_AMBIENT  = soundRegistry("entity.assimilated_creeper.ambient");
    public static final RegistryObject<SoundEvent> ENTITY_ASSIMILATED_CREEPER_DEATH  = soundRegistry("entity.assimilated_creeper.death");
    public static final RegistryObject<SoundEvent> ENTITY_ASSIMILATED_HURT  = soundRegistry("entity.assimilated.hurt");
    public static final RegistryObject<SoundEvent> ENTITY_EXPLOSION = soundRegistry("entity.explosion");
    public static final RegistryObject<SoundEvent> ENTITY_TURN = soundRegistry("entity.turn");
    public static final RegistryObject<SoundEvent> HEAD_AMBIENT = soundRegistry("entity.head.ambient");
    public static final RegistryObject<SoundEvent> HUMANOID_DEATH = soundRegistry("humanoid.death");
    public static final RegistryObject<SoundEvent> ASSIMILATED_ANIMAL_AMBIENT = soundRegistry("assimilated.animal.ambient");
    public static final RegistryObject<SoundEvent> PHASE0 = soundRegistry("player_phase0");
    public static final RegistryObject<SoundEvent> PHASE1 = soundRegistry("player_phase1");
    public static final RegistryObject<SoundEvent> PHASE2 = soundRegistry("player_phase2");
    public static final RegistryObject<SoundEvent> PHASE3 = soundRegistry("player_phase3");
    public static final RegistryObject<SoundEvent> PHASE4 = soundRegistry("player_phase4");
    public static final RegistryObject<SoundEvent> PHASE5 = soundRegistry("player_phase5");

}
