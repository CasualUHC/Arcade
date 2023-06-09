package net.casualuhc.arcade.commands

import com.mojang.brigadier.suggestion.SuggestionProvider
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.commands.synchronization.SuggestionProviders

interface CustomArgumentType {
    fun getSuggestionProvider(): SuggestionProvider<SharedSuggestionProvider>? {
        return SuggestionProviders.ASK_SERVER
    }
}