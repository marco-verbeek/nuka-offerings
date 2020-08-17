package me.nuka.offerings;

import org.bukkit.ChatColor;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.scheduler.BukkitRunnable;

import static me.nuka.offerings.Utils.getWorldEditMinMax;

public class EventListener implements Listener {
    private Main plugin;

    public EventListener(Main plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event){
        if(event == null) return;

        Player player = event.getPlayer();
        Item itemDropped = event.getItemDrop();

        if(itemDropped.getItemStack().getItemMeta().getLore().isEmpty()) return;

        String lore = ChatColor.stripColor(itemDropped.getItemStack().getItemMeta().getLore().get(1));

        if(!lore.contains("Offering for the")) return;
        itemDropped.setPickupDelay(20);

        new BukkitRunnable() {
            @Override
            public void run() {
                String templeName = plugin.droppedInOffering(itemDropped.getLocation());
                if(templeName == null || templeName.equals("")) return;

                // TODO if itemStack.getAmount > 1, reimburse the other ones

                itemDropped.remove();
                plugin.performOffering(player, itemDropped, templeName);
            }
        }.runTaskLater(plugin, 30);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event){
        if(event == null) return;
        if(!event.getLine(0).equalsIgnoreCase("[Offering]")) return;

        if(!event.getPlayer().hasPermission("offerings.create")) {
            event.getPlayer().sendMessage("You do not have the permission to create Offerings.");
            return;
        }

        if(event.getLine(1).equals("")) {
            event.getPlayer().sendMessage("You need to specify the temple name on line 2.");
            return;
        }

        plugin.getTemples().put(event.getLine(1), getWorldEditMinMax(event.getPlayer()));
        event.getPlayer().sendMessage("Offering successfully created.");
    }

}