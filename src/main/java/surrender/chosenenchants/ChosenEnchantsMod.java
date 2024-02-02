package surrender.chosenenchants;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ChosenEnchantsMod implements ModInitializer {
	public static final String MOD_ID = "chosenenchants";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		// Register the event callback for item usage
		UseItemCallback.EVENT.register((player, world, hand) -> {
			ItemStack heldItem = player.getStackInHand(hand);

			// Check if the item is a book and has a custom name
			if (heldItem.getItem() == Items.BOOK && heldItem.hasCustomName()) {
				String bookName = heldItem.getName().getString().toLowerCase().replace(' ', '_');

				// Check if the custom name matches a known enchantment
				Enchantment enchantment = getEnchantment(bookName);
				if (enchantment != null) {
					int bookCount = heldItem.getCount();

					// Create enchanted books and drop them
					for (int i = 0; i < bookCount; i++) {
						ItemStack enchantedBook = new ItemStack(Items.ENCHANTED_BOOK);
						EnchantmentLevelEntry enchantmentEntry = new EnchantmentLevelEntry(enchantment, 1);
						EnchantedBookItem.addEnchantment(enchantedBook, enchantmentEntry);

						// Drop the enchanted book
						world.spawnEntity(new ItemEntity(world, player.getX(), player.getY(), player.getZ(), enchantedBook));
					}

					// Remove the original books from the player's hand
					player.setStackInHand(hand, ItemStack.EMPTY);


					// Return a TypedActionResult with SUCCESS
					return TypedActionResult.success(player.getStackInHand(hand));
				}
			}

			// Return a TypedActionResult with PASS for other cases
			return TypedActionResult.pass(player.getStackInHand(hand));
		});
	}

	private Enchantment getEnchantment(String name) {
		if (Registries.ENCHANTMENT.get(Identifier.tryParse(name)) != null) {
			LOGGER.info(String.valueOf(Registries.ENCHANTMENT.get(Identifier.tryParse(name))));
			return Registries.ENCHANTMENT.get(Identifier.tryParse(name));
		}
		return null; // Return null if no matching enchantment is found
	}
}