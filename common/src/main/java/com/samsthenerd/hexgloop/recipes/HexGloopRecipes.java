package com.samsthenerd.hexgloop.recipes;

import com.samsthenerd.hexgloop.HexGloop;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialRecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class HexGloopRecipes {
    public static RegistrySupplier<SpecialRecipeSerializer<SealMultiFocusRecipe>> SEAL_MULTI_FOCUS_RECIPE;
    public static Registrar<RecipeSerializer<?> > recipeSerializers = HexGloop.REGISTRIES.get().get(Registry.RECIPE_SERIALIZER_KEY);
    
    static {
        SEAL_MULTI_FOCUS_RECIPE = register("crafting_seal_multi_focus", SealMultiFocusRecipe.SERIALIZER);
    }

    public static void init(){}

    

    public static <S extends RecipeSerializer<T>, T extends Recipe<?>> RegistrySupplier<S> register(String id, S serializer) {
        return recipeSerializers.register(new Identifier(HexGloop.MOD_ID, id), () -> serializer);
    }
}
