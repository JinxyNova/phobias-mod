package com.studiobing.phobias;

import java.util.EnumSet;
import java.util.Set;

/**
 * Codage des masques sur 2 longs : bits 0..63 (low) et 64..127 (high).
 * Corrige le débordement de 1L << ordinal au-delà de 63 : sans ce correctif,
 * la phobie 64 apparaît comme la phobie 0.
 * À brancher dans Phobias.toMask/fromMask et dans le paquet StatePayload.
 */
public final class Masks {
    private Masks() {}

    public static long low(Set<Phobia> s) {
        long m = 0L;
        for (Phobia p : s) {
            if (p.ordinal() < 64) m |= 1L << p.ordinal();
        }
        return m;
    }

    public static long high(Set<Phobia> s) {
        long m = 0L;
        for (Phobia p : s) {
            int o = p.ordinal() - 64;
            if (o >= 0 && o < 64) m |= 1L << o;
        }
        return m;
    }

    public static EnumSet<Phobia> from(long low, long high) {
        EnumSet<Phobia> out = EnumSet.noneOf(Phobia.class);
        for (Phobia p : Phobia.values()) {
            int i = p.ordinal();
            boolean on = i < 64 ? (low & (1L << i)) != 0L : (high & (1L << (i - 64))) != 0L;
            if (on) out.add(p);
        }
        return out;
    }
}
