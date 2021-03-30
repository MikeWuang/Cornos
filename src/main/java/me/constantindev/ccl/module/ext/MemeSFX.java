/*
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
# Project: Cornos
# File: MemeSFX
# Created by constantin at 01:36, Mär 31 2021
PLEASE READ THE COPYRIGHT NOTICE IN THE PROJECT ROOT, IF EXISTENT
@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@
*/
package me.constantindev.ccl.module.ext;

import me.constantindev.ccl.Cornos;
import me.constantindev.ccl.etc.base.Module;
import me.constantindev.ccl.etc.event.EventHelper;
import me.constantindev.ccl.etc.event.EventType;
import me.constantindev.ccl.etc.event.arg.PacketEvent;
import me.constantindev.ccl.etc.ms.MType;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;

public class MemeSFX extends Module {
    public MemeSFX() {
        super("MemeSFX", "Taco bell", MType.FUN);
        EventHelper.BUS.registerEvent(EventType.ONPACKETSEND,event -> {
            PacketEvent pe = (PacketEvent) event;
            if (pe.packet instanceof PlayerInteractEntityC2SPacket) {
                Cornos.minecraft.player.playSound(Cornos.HITMARKER_SOUND,1f,1f);
            }
        });
    }
}
