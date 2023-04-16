package net.casualuhc.arcade.mixin.scoreboard;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientboundSetObjectivePacket.class)
public interface ClientboundSetObjectivePacketAccessor {
	@Mutable
	@Accessor("displayName")
	void setDisplayName(Component name);
}