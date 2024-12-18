package com.samsthenerd.hexgloop.misc;


import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.mod.HexTags;
import at.petrak.hexcasting.api.mod.HexTags.Actions;
import at.petrak.hexcasting.interop.patchouli.LookupPatternComponent;
import at.petrak.hexcasting.xplat.IXplatAbstractions;
import java.util.List;

import java.util.function.UnaryOperator;
import javax.annotation.Nullable;

import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import org.jetbrains.annotations.NotNull;

import com.samsthenerd.hexgloop.misc.clientgreatbook.GreatBook;

import at.petrak.hexcasting.api.PatternRegistry;
import at.petrak.hexcasting.api.PatternRegistry.PatternEntry;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import vazkii.patchouli.api.IVariable;
import vazkii.patchouli.client.book.BookEntry;
import vazkii.patchouli.client.book.BookPage;
import vazkii.patchouli.common.book.Book;
import vazkii.patchouli.common.item.ItemModBook;

public class GetPatchouliPatterns {
    @Environment(EnvType.CLIENT)
    @Nullable
    public static Book getHexBookInHand(){
        ItemStack stack = MinecraftClient.getInstance().player.getMainHandStack();
        Book book = ItemModBook.getBook(stack);
        if(book != null && book.id.toString().equals("hexcasting:thehexbook")){
            return book;
        } else {
            return null;
        }
    }

    @Environment(EnvType.CLIENT)
    @NotNull
    public static Pair<HexPattern, HexPattern> getPatternsFromEntry(BookEntry entry, int spread){
        List<BookPage> pages = entry.getPages();
        int leftNum = spread * 2;
        int rightNum = (spread * 2) + 1;

        BookPage leftPage = leftNum < pages.size() ? pages.get(leftNum) : null;
        BookPage rightPage = rightNum < pages.size() ? pages.get(rightNum) : null;

        Pair<HexPattern, HexPattern> patterns = new Pair<HexPattern, HexPattern>(
            getPatternFromPage(leftPage), getPatternFromPage(rightPage));

        return patterns;
    }

    @Environment(EnvType.CLIENT)
    @Nullable
    public static HexPattern getPatternFromPage(@Nullable BookPage page){
        if(page == null || page.sourceObject == null) return null;
        if(page.sourceObject.has("type") && page.sourceObject.has("op_id")){
            String type = page.sourceObject.get("type").getAsString();
            Identifier opId = new Identifier(page.sourceObject.get("op_id").getAsString());
            if(type.equals("hexcasting:pattern")){
//                new LookupPatternComponent().getPatterns(
//                    (UnaryOperator<IVariable>) UnaryOperator.identity().apply(IVariable.wrap(opId.toString())));

                ActionRegistryEntry entry = null;
                try{
                    entry = IXplatAbstractions.INSTANCE.getActionRegistry().get(key);
                } catch (Exception e){
                    return null;
                }

                if(IXplatAbstractions.INSTANCE.getActionRegistry().isPerWorld()){
                    return GreatBook.INSTANCE.getPattern(opId);
                }
                return entry.prototype();
            }
        }
        return null;
    }
}
