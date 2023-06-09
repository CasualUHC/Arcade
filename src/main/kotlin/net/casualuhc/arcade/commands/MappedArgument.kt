package net.casualuhc.arcade.commands

import com.mojang.brigadier.StringReader
import com.mojang.brigadier.arguments.ArgumentType
import com.mojang.brigadier.context.CommandContext
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType
import com.mojang.brigadier.suggestion.Suggestions
import com.mojang.brigadier.suggestion.SuggestionsBuilder
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.SharedSuggestionProvider
import net.minecraft.network.chat.Component
import java.util.concurrent.CompletableFuture

open class MappedArgument<T>(
    private val options: Map<String, T>
): ArgumentType<T>, CustomArgumentType {
    init {
        for (name in this.options.keys) {
            if (!CustomStringArgumentInfo.isAllowedWord(name)) {
                throw IllegalArgumentException("Mapped key $name has invalid characters")
            }
        }
    }

    override fun parse(reader: StringReader): T {
        val name = reader.readUnquotedString()
        val enumeration = this.options[name]
        if (enumeration != null) {
            return enumeration
        }
        throw INVALID_ELEMENT.create(name)
    }

    override fun <S> listSuggestions(context: CommandContext<S>, builder: SuggestionsBuilder): CompletableFuture<Suggestions> {
        return SharedSuggestionProvider.suggest(this.options.keys, builder)
    }

    companion object {
        private val INVALID_ELEMENT = DynamicCommandExceptionType { Component.literal("$it is not a valid argument option") }

        @JvmStatic
        fun <T> mapped(map: Map<String, T>): MappedArgument<T> {
            return MappedArgument(map)
        }

        inline fun <reified T> getMapped(context: CommandContext<CommandSourceStack>, string: String): T {
            return context.getArgument(string, T::class.java)
        }

        @JvmStatic
        fun <T> getMapped(context: CommandContext<CommandSourceStack>, string: String, clazz: Class<T>): T {
            return context.getArgument(string, clazz)
        }
    }
}