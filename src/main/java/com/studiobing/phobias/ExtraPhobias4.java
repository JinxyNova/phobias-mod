package com.studiobing.phobias;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;

/** Effets des phobies du lot 4. Tick serveur + Etat.has(). */
public final class ExtraPhobias4 implements ModInitializer {
    private static int ticks;

    private static final Phobia[] NEW = {
            Phobia.BRONTOPHOBIE, Phobia.SCOTOPHOBIE, Phobia.OMPHALOPHOBIE, Phobia.ERGOPHOBIE, Phobia.POTIONOPHOBIE,
            Phobia.DENDROPHOBIE, Phobia.CAPNOPHOBIE, Phobia.ROBOPHOBIE, Phobia.GYMNOPHOBIE, Phobia.ARCTOPHOBIE,
            Phobia.OSTEOPHOBIE, Phobia.ACOUSTICOPHOBIE, Phobia.LIMOPHOBIE, Phobia.MYCOPHOBIE, Phobia.OCTOPHOBIE,
            Phobia.OCHLOPHOBIE, Phobia.PLUVIOPHOBIE, Phobia.SPELEOPHOBIE, Phobia.LUPOPHOBIE, Phobia.VULPOPHOBIE
    };

    private static final Map<UUID, int[]> NEXT_HIT = new HashMap<>();
    private static final Map<UUID, int[]> NEXT_MSG = new HashMap<>();
    private static final Map<String, RegistryEntry> FX = new HashMap<>();

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(ExtraPhobias4::tick);
    }

    public static void tick(MinecraftServer server) {
        ticks++;
        List<?> players = server.getPlayerManager().getPlayerList();
        if (ticks % 6000 == 0) {
            Set<UUID> online = new HashSet<>();
            for (Object o : players) online.add(((ServerPlayerEntity) o).getUuid());
            NEXT_HIT.keySet().retainAll(online);
            NEXT_MSG.keySet().retainAll(online);
        }
        if (ticks % 20 != 0) return;
        for (Object o : players) {
            ServerPlayerEntity p = (ServerPlayerEntity) o;
            if (p.isSpectator() || p.isDead()) continue;
            for (Phobia ph : NEW) {
                if (Etat.has(p.getUuid(), ph)) run(p, p.getServerWorld(), ph);
            }
        }
    }

    private static void run(ServerPlayerEntity p, ServerWorld w, Phobia ph) {
        BlockPos pos = p.getBlockPos();
        switch (ph) {
            case BRONTOPHOBIE:
                if (w.getThunderGradient(1.0F) > 0.5F && w.isSkyVisible(pos)) {
                    effect(p, "darkness", 60, 0);
                    effect(p, "nausea", 60, 0);
                    if (cool(p, ph, 60)) hurt(p, w, ph, 2.0F);
                    warn(p, ph, "Le tonnerre gronde… cache-toi !");
                }
                break;
            case SCOTOPHOBIE:
                if (w.getLightLevel(LightType.SKY, pos) < 4) {
                    effect(p, "darkness", 60, 0);
                    warn(p, ph, "Le noir t'enveloppe…");
                }
                break;
            case OMPHALOPHOBIE:
                if (scan(w, pos, 3, 2, 3, s -> s.endsWith("_button") || s.equals("lever"), 1) > 0) {
                    effect(p, "nausea", 60, 0);
                    warn(p, ph, "Ces boutons te donnent la nausée…");
                }
                break;
            case ERGOPHOBIE:
                if (scan(w, pos, 3, 2, 3, s -> s.equals("crafting_table") || s.endsWith("furnace")
                        || s.equals("smoker") || s.equals("anvil"), 1) > 0) {
                    effect(p, "mining_fatigue", 60, 0);
                    effect(p, "slowness", 40, 0);
                    warn(p, ph, "Le travail t'épuise…");
                }
                break;
            case POTIONOPHOBIE:
                if (invHas(p, s -> s.contains("potion"))) {
                    effect(p, "nausea", 80, 0);
                    warn(p, ph, "Ces potions te rendent malade…");
                }
                break;
            case DENDROPHOBIE:
                if (scan(w, pos, 2, 2, 2, s -> s.endsWith("_log") || s.endsWith("_wood"), 1) > 0) {
                    effect(p, "slowness", 40, 0);
                    warn(p, ph, "Les troncs t'écrasent du regard…");
                }
                break;
            case CAPNOPHOBIE:
                if (scan(w, pos, 3, 2, 3, s -> s.endsWith("campfire"), 1) > 0) {
                    effect(p, "nausea", 60, 0);
                    if (cool(p, ph, 60)) hurt(p, w, ph, 1.0F);
                    warn(p, ph, "La fumée te coupe le souffle !");
                }
                break;
            case ROBOPHOBIE:
                if (mobs(p, w, 16, "iron_golem") > 0) {
                    effect(p, "weakness", 60, 0);
                    effect(p, "slowness", 40, 0);
                    if (mobs(p, w, 3, "iron_golem") > 0 && cool(p, ph, 40)) hurt(p, w, ph, 2.0F);
                    warn(p, ph, "Le golem te domine…");
                }
                break;
            case GYMNOPHOBIE:
                if (w.isDay() && armorEmpty(p)) {
                    effect(p, "nausea", 60, 0);
                    effect(p, "weakness", 60, 0);
                    warn(p, ph, "Tu te sens nu et vulnérable…");
                }
                break;
            case ARCTOPHOBIE:
                if (mobs(p, w, 16, "polar_bear") > 0) {
                    effect(p, "weakness", 60, 0);
                    if (mobs(p, w, 4, "polar_bear") > 0 && cool(p, ph, 40)) hurt(p, w, ph, 2.0F);
                    warn(p, ph, "Un ours polaire rôde…");
                }
                break;
            case OSTEOPHOBIE:
                if (invHas(p, s -> s.equals("bone") || s.equals("bone_block"))) {
                    effect(p, "weakness", 60, 0);
                    effect(p, "slowness", 20, 0);
                    warn(p, ph, "Ces os te glacent le sang…");
                }
                break;
            case ACOUSTICOPHOBIE:
                if (scan(w, pos, 4, 2, 4, s -> s.equals("note_block") || s.equals("jukebox") || s.equals("bell"), 1) > 0) {
                    effect(p, "nausea", 60, 0);
                    if (cool(p, ph, 80)) hurt(p, w, ph, 0.5F);
                    warn(p, ph, "Ce bruit te déchire les oreilles !");
                }
                break;
            case LIMOPHOBIE:
                if (p.getHungerManager().getFoodLevel() < 6) {
                    effect(p, "weakness", 60, 0);
                    warn(p, ph, "Tu as faim… tes forces s'en vont.");
                }
                break;
            case MYCOPHOBIE:
                if (scan(w, pos, 2, 1, 2, s -> s.endsWith("mushroom"), 1) > 0) {
                    effect(p, "nausea", 80, 0);
                    warn(p, ph, "Ces champignons te donnent la nausée…");
                }
                break;
            case OCTOPHOBIE:
                if (stackOf(p, 8)) {
                    if (cool(p, ph, 60)) hurt(p, w, ph, 1.0F);
                    warn(p, ph, "Un stack de 8 te blesse…");
                }
                break;
            case OCHLOPHOBIE:
                if (playersNear(p, w, 16) >= 6) {
                    effect(p, "nausea", 60, 0);
                    effect(p, "slowness", 40, 0);
                    warn(p, ph, "Trop de monde autour de toi…");
                }
                break;
            case PLUVIOPHOBIE:
                if (w.isRaining() && w.isSkyVisible(pos)) {
                    effect(p, "slowness", 40, 0);
                    warn(p, ph, "La pluie te glace…");
                }
                break;
            case SPELEOPHOBIE:
                if (p.getY() < 50 && !w.isSkyVisible(pos)) {
                    effect(p, "darkness", 60, 0);
                    effect(p, "nausea", 60, 0);
                    warn(p, ph, "Les grottes t'étouffent…");
                }
                break;
            case LUPOPHOBIE:
                if (mobs(p, w, 10, "wolf") > 0) {
                    effect(p, "slowness", 40, 1);
                    if (mobs(p, w, 3, "wolf") > 0 && cool(p, ph, 40)) hurt(p, w, ph, 1.0F);
                    warn(p, ph, "Des loups te pistent…");
                }
                break;
            case VULPOPHOBIE:
                if (mobs(p, w, 10, "fox") > 0) {
                    effect(p, "nausea", 60, 0);
                    if (mobs(p, w, 3, "fox") > 0 && cool(p, ph, 60)) hurt(p, w, ph, 0.5F);
                    warn(p, ph, "Ce renard te fixe…");
                }
                break;
            default:
                break;
        }
    }

    // ------------------------------------------------------------ helpers

    private static RegistryKey key(String id) {
        return RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of("phobias", id));
    }

    private static RegistryEntry fx(String id) {
        return FX.computeIfAbsent(id, k -> {
            try {
                Optional<?> o = Registries.STATUS_EFFECT.getEntry(Identifier.of("minecraft", k));
                return o.isPresent() ? (RegistryEntry) o.get() : null;
            } catch (Throwable t) {
                return null;
            }
        });
    }

    private static void effect(ServerPlayerEntity p, String id, int dur, int amp) {
        RegistryEntry e = fx(id);
        if (e != null) p.addStatusEffect(new StatusEffectInstance(e, dur, amp, false, false, true));
    }

    private static void hurt(ServerPlayerEntity p, ServerWorld w, Phobia ph, float amount) {
        if (p.isCreative()) return;
        RegistryKey type = key(ph.name().toLowerCase(Locale.ROOT));
        RegistryEntry entry = ((Registry) w.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE)).getOrThrow(type);
        p.damage(w, new DamageSource(entry), amount);
    }

    private static boolean cool(ServerPlayerEntity p, Phobia ph, int period) {
        int[] a = NEXT_HIT.computeIfAbsent(p.getUuid(), k -> new int[Phobia.values().length]);
        int o = ph.ordinal();
        if (a[o] > ticks) return false;
        a[o] = ticks + period;
        return true;
    }

    private static void warn(ServerPlayerEntity p, Phobia ph, String text) {
        int[] a = NEXT_MSG.computeIfAbsent(p.getUuid(), k -> new int[Phobia.values().length]);
        int o = ph.ordinal();
        if (a[o] > ticks) return;
        a[o] = ticks + 600;
        p.sendMessage(Text.literal(ph.color + text), true);
    }

    private static String idOfItem(ItemStack s) {
        try {
            Identifier id = Registries.ITEM.getId(s.getItem());
            return id == null ? "" : id.getPath();
        } catch (Throwable t) {
            return "";
        }
    }

    private static String idOfBlock(BlockState s) {
        try {
            Identifier id = Registries.BLOCK.getId(s.getBlock());
            return id == null ? "" : id.getPath();
        } catch (Throwable t) {
            return "";
        }
    }

    private static String idOfMob(MobEntity m) {
        try {
            Identifier id = Registries.ENTITY_TYPE.getId(m.getType());
            return id == null ? "" : id.getPath();
        } catch (Throwable t) {
            return "";
        }
    }

    private static int mobs(ServerPlayerEntity p, ServerWorld w, double r, String... ids) {
        int n = 0;
        for (Object o : w.getEntitiesByClass(MobEntity.class, p.getBoundingBox().expand(r), m -> true)) {
            MobEntity m = (MobEntity) o;
            if (!m.isAlive() || m.squaredDistanceTo(p) > r * r) continue;
            String id = idOfMob(m);
            for (String want : ids) {
                if (id.equals(want)) {
                    n++;
                    break;
                }
            }
        }
        return n;
    }

    private static int playersNear(ServerPlayerEntity p, ServerWorld w, double r) {
        int n = 0;
        for (Object o : w.getEntitiesByClass(ServerPlayerEntity.class, p.getBoundingBox().expand(r), q -> true)) {
            if (o != p && !((ServerPlayerEntity) o).isSpectator()) n++;
        }
        return n;
    }

    private static int scan(ServerWorld w, BlockPos c, int rx, int ry, int rz, Predicate<String> match, int cap) {
        int n = 0;
        for (int dx = -rx; dx <= rx; dx++) {
            for (int dy = -ry; dy <= ry; dy++) {
                for (int dz = -rz; dz <= rz; dz++) {
                    BlockState s = w.getBlockState(c.add(dx, dy, dz));
                    if (s != null && !s.isAir() && match.test(idOfBlock(s)) && ++n >= cap) return n;
                }
            }
        }
        return n;
    }

    private static boolean invHas(ServerPlayerEntity p, Predicate<String> match) {
        for (int i = 0; i < 41; i++) {
            ItemStack s = p.getInventory().getStack(i);
            if (s != null && !s.isEmpty() && match.test(idOfItem(s))) return true;
        }
        return false;
    }

    private static boolean stackOf(ServerPlayerEntity p, int count) {
        for (int i = 0; i < 41; i++) {
            ItemStack s = p.getInventory().getStack(i);
            if (s != null && !s.isEmpty() && s.getCount() == count) return true;
        }
        return false;
    }

    private static boolean armorEmpty(ServerPlayerEntity p) {
        for (int i = 36; i < 40; i++) {
            ItemStack s = p.getInventory().getStack(i);
            if (s != null && !s.isEmpty()) return false;
        }
        return true;
    }
}
