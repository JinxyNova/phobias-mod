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
import net.minecraft.class_1282;
import net.minecraft.class_1293;
import net.minecraft.class_1308;
import net.minecraft.class_1799;
import net.minecraft.class_1944;
import net.minecraft.class_2338;
import net.minecraft.class_2378;
import net.minecraft.class_2561;
import net.minecraft.class_2680;
import net.minecraft.class_2960;
import net.minecraft.class_3218;
import net.minecraft.class_3222;
import net.minecraft.class_5321;
import net.minecraft.class_6880;
import net.minecraft.class_7923;
import net.minecraft.class_7924;
import net.minecraft.server.MinecraftServer;

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
    private static final Map<String, class_6880> FX = new HashMap<>();

    @Override
    public void onInitialize() {
        ServerTickEvents.END_SERVER_TICK.register(ExtraPhobias4::tick);
    }

    public static void tick(MinecraftServer server) {
        ticks++;
        List<?> players = server.method_3760().method_14571();
        if (ticks % 6000 == 0) {
            Set<UUID> online = new HashSet<>();
            for (Object o : players) online.add(((class_3222) o).method_5667());
            NEXT_HIT.keySet().retainAll(online);
            NEXT_MSG.keySet().retainAll(online);
        }
        if (ticks % 20 != 0) return;
        for (Object o : players) {
            class_3222 p = (class_3222) o;
            if (p.method_7325() || p.method_29504()) continue;
            for (Phobia ph : NEW) {
                if (Etat.has(p.method_5667(), ph)) run(p, p.method_51469(), ph);
            }
        }
    }

    private static void run(class_3222 p, class_3218 w, Phobia ph) {
        class_2338 pos = p.method_24515();
        switch (ph) {
            case BRONTOPHOBIE:
                if (w.method_8478(1.0F) > 0.5F && w.method_8311(pos)) {
                    effect(p, "darkness", 60, 0);
                    effect(p, "nausea", 60, 0);
                    if (cool(p, ph, 60)) hurt(p, w, ph, 2.0F);
                    warn(p, ph, "Le tonnerre gronde… cache-toi !");
                }
                break;
            case SCOTOPHOBIE:
                if (w.method_8314(class_1944.field_9284, pos) < 4) {
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
                if (w.method_8530() && armorEmpty(p)) {
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
                if (p.method_7344().method_7586() < 6) {
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
                if (w.method_8419() && w.method_8311(pos)) {
                    effect(p, "slowness", 40, 0);
                    warn(p, ph, "La pluie te glace…");
                }
                break;
            case SPELEOPHOBIE:
                if (p.method_23318() < 50 && !w.method_8311(pos)) {
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

    private static class_5321 key(String id) {
        return class_5321.method_29179(class_7924.field_42534, class_2960.method_60655("phobias", id));
    }

    private static class_6880 fx(String id) {
        return FX.computeIfAbsent(id, k -> {
            try {
                Optional<?> o = class_7923.field_41174.method_10223(class_2960.method_60655("minecraft", k));
                return o.isPresent() ? (class_6880) o.get() : null;
            } catch (Throwable t) {
                return null;
            }
        });
    }

    private static void effect(class_3222 p, String id, int dur, int amp) {
        class_6880 e = fx(id);
        if (e != null) p.method_6092(new class_1293(e, dur, amp, false, false, true));
    }

    private static void hurt(class_3222 p, class_3218 w, Phobia ph, float amount) {
        if (p.method_68878()) return;
        class_5321 type = key(ph.name().toLowerCase(Locale.ROOT));
        class_6880 entry = ((class_2378) w.method_30349().method_30530(class_7924.field_42534)).method_46747(type);
        p.method_64397(w, new class_1282(entry), amount);
    }

    private static boolean cool(class_3222 p, Phobia ph, int period) {
        int[] a = NEXT_HIT.computeIfAbsent(p.method_5667(), k -> new int[Phobia.values().length]);
        int o = ph.ordinal();
        if (a[o] > ticks) return false;
        a[o] = ticks + period;
        return true;
    }

    private static void warn(class_3222 p, Phobia ph, String text) {
        int[] a = NEXT_MSG.computeIfAbsent(p.method_5667(), k -> new int[Phobia.values().length]);
        int o = ph.ordinal();
        if (a[o] > ticks) return;
        a[o] = ticks + 600;
        p.method_7353(class_2561.method_43470(ph.color + text), true);
    }

    private static String idOfItem(class_1799 s) {
        try {
            class_2960 id = class_7923.field_41178.method_10221(s.method_7909());
            return id == null ? "" : id.method_12832();
        } catch (Throwable t) {
            return "";
        }
    }

    private static String idOfBlock(class_2680 s) {
        try {
            class_2960 id = class_7923.field_41175.method_10221(s.method_26204());
            return id == null ? "" : id.method_12832();
        } catch (Throwable t) {
            return "";
        }
    }

    private static String idOfMob(class_1308 m) {
        try {
            class_2960 id = class_7923.field_41177.method_10221(m.method_5864());
            return id == null ? "" : id.method_12832();
        } catch (Throwable t) {
            return "";
        }
    }

    private static int mobs(class_3222 p, class_3218 w, double r, String... ids) {
        int n = 0;
        for (Object o : w.method_8390(class_1308.class, p.method_5829().method_1014(r), m -> true)) {
            class_1308 m = (class_1308) o;
            if (!m.method_5805() || m.method_5858(p) > r * r) continue;
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

    private static int playersNear(class_3222 p, class_3218 w, double r) {
        int n = 0;
        for (Object o : w.method_8390(class_3222.class, p.method_5829().method_1014(r), q -> true)) {
            if (o != p && !((class_3222) o).method_7325()) n++;
        }
        return n;
    }

    private static int scan(class_3218 w, class_2338 c, int rx, int ry, int rz, Predicate<String> match, int cap) {
        int n = 0;
        for (int dx = -rx; dx <= rx; dx++) {
            for (int dy = -ry; dy <= ry; dy++) {
                for (int dz = -rz; dz <= rz; dz++) {
                    class_2680 s = w.method_8320(c.method_10069(dx, dy, dz));
                    if (s != null && !s.method_26215() && match.test(idOfBlock(s)) && ++n >= cap) return n;
                }
            }
        }
        return n;
    }

    private static boolean invHas(class_3222 p, Predicate<String> match) {
        for (int i = 0; i < 41; i++) {
            class_1799 s = p.method_31548().method_5438(i);
            if (s != null && !s.method_7960() && match.test(idOfItem(s))) return true;
        }
        return false;
    }

    private static boolean stackOf(class_3222 p, int count) {
        for (int i = 0; i < 41; i++) {
            class_1799 s = p.method_31548().method_5438(i);
            if (s != null && !s.method_7960() && s.method_7947() == count) return true;
        }
        return false;
    }

    private static boolean armorEmpty(class_3222 p) {
        for (int i = 36; i < 40; i++) {
            class_1799 s = p.method_31548().method_5438(i);
            if (s != null && !s.method_7960()) return false;
        }
        return true;
    }
}
