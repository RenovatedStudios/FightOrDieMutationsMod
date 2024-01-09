package net.teamabyssal.entity.custom;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.teamabyssal.config.FightOrDieMutationsConfig;
import net.teamabyssal.constants.MathHelper;
import net.teamabyssal.controls.WallMovementControl;
import net.teamabyssal.entity.ai.CustomMeleeAttackGoal;
import net.teamabyssal.entity.ai.MalruptorInfects;
import net.teamabyssal.entity.categories.Evolved;
import net.teamabyssal.entity.categories.Evolving;
import net.teamabyssal.entity.categories.Hunter;
import net.teamabyssal.entity.categories.Infector;
import net.teamabyssal.registry.EffectRegistry;
import net.teamabyssal.registry.EntityRegistry;
import net.teamabyssal.registry.SoundRegistry;
import net.teamabyssal.registry.WorldDataRegistry;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;

public class MalruptorEntity extends Infector implements GeoEntity, Evolving, Evolved, Hunter {



    public static final EntityDataAccessor<Integer> KILLS = SynchedEntityData.defineId(MalruptorEntity.class, EntityDataSerializers.INT);

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static boolean jumping = false;

    public MalruptorEntity(EntityType<? extends Monster> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
        this.moveControl = new WallMovementControl(this);
        this.navigation = new WallClimberNavigation(this, pLevel);
    }


    @Override
    public boolean causeFallDamage(float pFallDistance, float pMultiplier, DamageSource pSource) {
        return false;
    }


    @Nullable
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createLivingAttributes()
                .add(Attributes.ATTACK_KNOCKBACK, 0.6D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.1D)
                .add(Attributes.FOLLOW_RANGE, 32.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.MAX_HEALTH, FightOrDieMutationsConfig.SERVER.malruptor_health.get())
                .add(Attributes.ATTACK_DAMAGE, FightOrDieMutationsConfig.SERVER.malruptor_damage.get())
                .add(Attributes.ARMOR, 2.0D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(3, new CustomMeleeAttackGoal(this, 1.2, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return 2.0 + entity.getBbWidth() * entity.getBbWidth();
            }
        });
        this.goalSelector.addGoal(1, new MalruptorJumpGoal(this, 0.7F));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, IronGolem.class, true) {

            @Override
            public boolean canUse() {
                return super.canUse() && this.mob.level() instanceof ServerLevel world && WorldDataRegistry.getWorldDataRegistry(world).getPhase() > 2;

            }
        });
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, true) {
            @Override
            public boolean canUse() {
                return super.canUse() && this.mob.level() instanceof ServerLevel world && WorldDataRegistry.getWorldDataRegistry(world).getPhase() > 1;
            }
        });
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Skeleton.class, true) {
            @Override
            public boolean canUse() {
                return super.canUse() && this.mob.level() instanceof ServerLevel world && WorldDataRegistry.getWorldDataRegistry(world).getPhase() > 1;
            }
        });
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, EnderMan.class, true) {
            @Override
            public boolean canUse() {
                return super.canUse() && FightOrDieMutationsConfig.SERVER.enderman_attack.get() && this.mob.level() instanceof ServerLevel world && WorldDataRegistry.getWorldDataRegistry(world).getPhase() > 2;
            }
        });
        this.targetSelector.addGoal(8, new NearestAttackableTargetGoal<>(this, Creeper.class, true) {
            @Override
            public boolean canUse() {
                return super.canUse() && FightOrDieMutationsConfig.SERVER.creeper_attack.get() && this.mob.level() instanceof ServerLevel world && WorldDataRegistry.getWorldDataRegistry(world).getPhase() > 2;
            }
        });
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Witch.class, true) {
            @Override
            public boolean canUse() {
                return super.canUse() && FightOrDieMutationsConfig.SERVER.witch_attack.get() && this.mob.level() instanceof ServerLevel world && WorldDataRegistry.getWorldDataRegistry(world).getPhase() > 2;
            }
        });
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Endermite.class, true) {
            @Override
            public boolean canUse() {
                return super.canUse() && this.mob.level() instanceof ServerLevel world && WorldDataRegistry.getWorldDataRegistry(world).getPhase() > 1;
            }
        });
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Silverfish.class, true) {
            @Override
            public boolean canUse() {
                return super.canUse() && this.mob.level() instanceof ServerLevel world && WorldDataRegistry.getWorldDataRegistry(world).getPhase() > 1;
            }
        });
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Spider.class, true) {
            @Override
            public boolean canUse() {
                return super.canUse() && this.mob.level() instanceof ServerLevel world && WorldDataRegistry.getWorldDataRegistry(world).getPhase() > 1;
            }
        });
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Zombie.class, true) {
            @Override
            public boolean canUse() {
                return super.canUse() && this.mob.level() instanceof ServerLevel world && WorldDataRegistry.getWorldDataRegistry(world).getPhase() > 1;
            }
        });
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Slime.class, true) {
            @Override
            public boolean canUse() {
                return super.canUse() && this.mob.level() instanceof ServerLevel world && WorldDataRegistry.getWorldDataRegistry(world).getPhase() > 1;
            }
        });
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(10, new MalruptorInfects(this, 1.0, Cow.class) {
            @Override
            public boolean canUse() {
                return super.canUse() && this.mob.getTarget() == null && this.partner != null && !this.partner.hasEffect(EffectRegistry.HIVE_SICKNESS.get()) && (this.mob.level() instanceof ServerLevel world && WorldDataRegistry.getWorldDataRegistry(world).getPhase() < 4);
            }
        });
    }

    @Override
    public void tick() {
        if (this.isAlive() && this.getKills() == 20) {

            this.level().playSound((Player) null, this.blockPosition(), SoundEvents.ZOMBIE_INFECT, SoundSource.HOSTILE, 1.0F, 1.0F);
            if (this.level() instanceof ServerLevel server) {
                server.sendParticles(ParticleTypes.EXPLOSION, this.getX(), this.getY() + 1, this.getZ(), 5, 0.4, 1.0, 0.4, 0);
                WorldDataRegistry worldDataRegistry = WorldDataRegistry.getWorldDataRegistry(server);
                int currentScore = worldDataRegistry.getScore();
                worldDataRegistry.setScore(currentScore + 5);
            }

            this.discard();
            this.EvolveIntoMargrouper(this);
        }
        super.tick();
    }
    private void EvolveIntoMargrouper(Entity entity) {
        MargrouperEntity margrouperEntity = new MargrouperEntity(EntityRegistry.MARGROUPER.get(), entity.level());
        margrouperEntity.moveTo(entity.getX(),entity.getY(),entity.getZ());
        entity.level().addFreshEntity(margrouperEntity);
    }

    public boolean isHunting() {
        return !this.onGround() && jumping;
    }

    public void setKills(Integer count) {
        entityData.set(KILLS, count);
    }

    public int getKills() {
        return entityData.get(KILLS);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("kills", entityData.get(KILLS));
    }


    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        entityData.set(KILLS, tag.getInt("kills"));
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(KILLS, 0);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerV) {
        controllerV.add(
                new AnimationController<>(this, "controllerV", 7, event -> {
                    if (event.isMoving() && !this.isAggressive()) {
                        return event.setAndContinue(RawAnimation.begin().thenLoop("malruptor_walk"));
                    }
                    else if (event.isMoving() && this.isAggressive() && this.onGround()) {
                        return event.setAndContinue(RawAnimation.begin().thenLoop("malruptor_target"));
                    }
                    else if (!event.isMoving() && !this.isAggressive()) {
                        return event.setAndContinue(RawAnimation.begin().thenLoop("malruptor_idle"));
                    }
                    return PlayState.STOP;
                }));
        controllerV.add(
                new AnimationController<>(this, "controllerK", 7, event -> {
                    if (this.isHunting()) {
                        return event.setAndContinue(RawAnimation.begin().thenLoop("malruptor_air"));
                    }
                    return PlayState.STOP;
                }));

    }
    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return SoundRegistry.ENTITY_MALRUPTOR_HURT.get();
    }
    @Override
    protected SoundEvent getDeathSound() {
        return SoundRegistry.ENTITY_MALRUPTOR_DEATH.get();
    }
    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SoundRegistry.ENTITY_MALRUPTOR_AMBIENT.get();
    }

    @Override
    public void die(DamageSource source) {
        if (this.level() instanceof ServerLevel world) {
            WorldDataRegistry worldDataRegistry = WorldDataRegistry.getWorldDataRegistry(world);
            int currentScore = worldDataRegistry.getScore();
            int currentPhase = worldDataRegistry.getPhase();
            if (Math.random() <= 0.85F && currentPhase > 2) {
                worldDataRegistry.setScore(currentScore - 1);
            }
        }
        super.die(source);
    }

    public static class MalruptorJumpGoal extends Goal {
        private final Mob mob;
        private LivingEntity target;
        private final float yd;


        public MalruptorJumpGoal(Mob mob, float v) {
            this.mob = mob;
            this.yd = v;
            this.setFlags(EnumSet.of(Goal.Flag.JUMP, Goal.Flag.MOVE));
        }

        public boolean canUse() {
            this.target = this.mob.getTarget();
            if (this.target == null) {
                return false;
            } else if (this.mob.isInWater()) {
                return false;
            } else {
                double d0 = this.mob.distanceTo(this.target);
                if (d0 > 6.0D && d0 < 14.0D) {
                    if (!this.mob.onGround()) {
                        return false;
                    } else {
                        return this.mob.getRandom().nextInt(reducedTickDelay(14)) == 0;
                    }
                } else {
                    return false;
                }
            }

        }


        @Override
        public void tick() {
            if (this.mob.getTarget() != null) {
                this.mob.getLookControl().setLookAt(this.target);
                jumping = true;
            }
        }

        public void start() {
            Vec3 vec3 = this.mob.getDeltaMovement();
            Vec3 vec31 = new Vec3(this.target.getX() - this.mob.getX(), 0.0D, this.target.getZ() - this.mob.getZ());
            if (vec31.lengthSqr() > 1.0E-7D) {
                vec31 = vec31.normalize().scale(2D).add(vec3.scale(1.5D));
            }

            this.mob.setDeltaMovement(vec31.x + yd * (MathHelper.RELAT / 156), this.yd + (MathHelper.HEX / 12), vec31.z + yd);
        }

        @Override
        public void stop() {
            super.stop();
        }
    }
}
