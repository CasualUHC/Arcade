package net.casualuhc.arcade.advancements

import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementRewards
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.CriterionTriggerInstance
import net.minecraft.advancements.DisplayInfo
import net.minecraft.advancements.FrameType
import net.minecraft.advancements.RequirementsStrategy
import net.minecraft.advancements.critereon.ImpossibleTrigger
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike

@Suppress("unused", "MemberVisibilityCanBePrivate")
class AdvancementBuilder private constructor() {
    private val criterion = HashMap<String, Criterion>()

    var parent: Advancement? = null
    var id: ResourceLocation? = null
    var display: ItemStack = ItemStack.EMPTY
    var title: Component = Component.empty()
    var description: Component = Component.empty()
    var background: ResourceLocation? = null
    var frame = FrameType.TASK
    var requirements: RequirementsStrategy = RequirementsStrategy.AND
    var rewards: AdvancementRewards = AdvancementRewards.EMPTY
    var toast = false
    var announce = false
    var hidden = false

    fun parent(parent: Advancement) {
        this.parent = parent
    }

    fun id(id: ResourceLocation): AdvancementBuilder {
        this.id = id
        return this
    }

    fun display(display: ItemStack): AdvancementBuilder {
        this.display = display
        return this
    }

    fun display(display: ItemLike): AdvancementBuilder {
        this.display = display.asItem().defaultInstance
        return this
    }

    fun title(title: Component): AdvancementBuilder {
        this.title = title
        return this
    }

    fun description(description: Component): AdvancementBuilder {
        this.description = description
        return this
    }

    fun background(background: ResourceLocation): AdvancementBuilder {
        this.background = background
        return this
    }

    fun frame(frame: FrameType): AdvancementBuilder {
        this.frame = frame
        return this
    }

    fun requirements(requirements: RequirementsStrategy): AdvancementBuilder {
        this.requirements = requirements
        return this
    }

    fun rewards(rewards: AdvancementRewards): AdvancementBuilder {
        this.rewards = rewards
        return this
    }

    fun toast(): AdvancementBuilder {
        this.toast = true
        return this
    }

    fun announce(): AdvancementBuilder {
        this.announce = true
        return this
    }

    fun hidden(): AdvancementBuilder {
        this.hidden = true
        return this
    }

    fun criterion(name: String, trigger: CriterionTriggerInstance): AdvancementBuilder {
        this.criterion[name] = Criterion(trigger)
        return this
    }

    fun impossible(): AdvancementBuilder {
        this.criterion("impossible", ImpossibleTrigger.TriggerInstance())
        return this
    }

    fun build(): Advancement {
        val id = this.id
        requireNotNull(id)
        val requirements = this.requirements.createRequirements(this.criterion.keys)
        return Advancement(
            id,
            this.parent,
            DisplayInfo(
                this.display,
                this.title,
                this.description,
                this.background,
                this.frame,
                this.toast,
                this.announce,
                this.hidden
            ),
            this.rewards,
            this.criterion,
            requirements
        )
    }

    companion object {
        fun create(block: AdvancementBuilder.() -> Unit): AdvancementBuilder {
            return AdvancementBuilder().apply(block)
        }
    }
}