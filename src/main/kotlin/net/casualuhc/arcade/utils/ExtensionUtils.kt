package net.casualuhc.arcade.utils

import net.casualuhc.arcade.extensions.DataExtension
import net.casualuhc.arcade.extensions.Extension
import net.casualuhc.arcade.extensions.ExtensionHolder
import net.minecraft.nbt.CompoundTag

object ExtensionUtils {
    @JvmStatic
    fun ExtensionHolder.deserialize(tag: CompoundTag) {
        for (extension in this.getExtensions()) {
            if (extension is DataExtension) {
                val data = tag[extension.getName()]
                if (data != null) {
                    extension.deserialize(data)
                }
            }
        }
    }

    @JvmStatic
    fun ExtensionHolder.serialize(tag: CompoundTag) {
        for (extension in this.getExtensions()) {
            if (extension is DataExtension) {
                tag.put(extension.getName(), extension.serialize())
            }
        }
    }

    @JvmStatic
    fun ExtensionHolder.addExtension(extension: Extension) {
        this.getExtensionMap().addExtension(extension)
    }

    @JvmStatic
    fun <T: Extension> ExtensionHolder.getExtension(type: Class<T>): T {
        val extension = this.getExtensionMap().getExtension(type)
        if (extension === null) {
            throw IllegalArgumentException("No such extension $type exists for $this")
        }
        return extension
    }

    @JvmStatic
    fun ExtensionHolder.getExtensions(): Collection<Extension> {
        return this.getExtensionMap().getExtensions()
    }
}