package net.casualuhc.arcade.events.server

import net.casualuhc.arcade.events.core.Event
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeManager
import java.util.LinkedList

data class ServerRecipeReloadEvent(
    val recipeManager: RecipeManager,
    val resourceManager: ResourceManager
): Event() {
    private val recipes = LinkedList<Recipe<*>>()

    fun add(recipe: Recipe<*>) {
        this.recipes.add(recipe)
    }

    fun addAll(recipes: Collection<Recipe<*>>) {
        this.recipes.addAll(recipes)
    }

    fun getRecipes(): List<Recipe<*>> {
        return this.recipes
    }
}