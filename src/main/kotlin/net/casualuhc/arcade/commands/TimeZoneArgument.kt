package net.casualuhc.arcade.commands

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.SharedSuggestionProvider
import java.time.ZoneId
import java.util.concurrent.CompletableFuture

class TimeZoneArgument: ArgumentType<ZoneId>, CustomArgumentType {
    override fun parse(reader: StringReader): ZoneId {
        return ZoneId.of(reader.readUnquotedString())
    }

    override fun <S: Any> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        return SharedSuggestionProvider.suggest(ZoneId.getAvailableZoneIds().map { "\"$it\"" }, builder)
    }

    companion object {
        fun timeZone(): TimeZoneArgument {
            return TimeZoneArgument()
        }

        fun getTimeZone(context: CommandContext<CommandSourceStack>, string: String): ZoneId {
            return context.getArgument(string, ZoneId::class.java)
        }
    }
}