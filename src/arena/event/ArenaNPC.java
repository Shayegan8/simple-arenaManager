package arena.event;

import arena.Arena;
import arena.ArenaManager;
import arena.ArenaTeam;
import arena.STATES;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;

public class ArenaNPC extends Event implements Cancellable {

    private static HandlerList handlers = new HandlerList();
    private boolean cancelled = false;
    private Player player;
    private Arena arena;
    private String pluginName;
    private STATES status;
    private EntityDamageEvent e1;
    private PlayerInteractEntityEvent e2;
    private InventoryClickEvent e3;

    public Player getPlayer() {
        return player;
    }

    public Arena getArena() {
        return arena;
    }

    public ArenaTeam getTeam() {
        return team;
    }

    private ArenaTeam team;

    public ArenaNPC(Player player, EntityDamageEvent e1, PlayerInteractEntityEvent e2, InventoryClickEvent e3) {
        this.player = player;
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
    }

    public ArenaNPC(Player player, Arena arena, EntityDamageEvent e1, PlayerInteractEntityEvent e2, InventoryClickEvent e3) {
        this.player = player;
        this.arena = arena;
        this.team = ArenaManager.getPlayersTeam(player.getName());
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
    }

    public ArenaNPC(Player player, ArenaTeam team, EntityDamageEvent e1, PlayerInteractEntityEvent e2, InventoryClickEvent e3) {
        this.player = player;
        this.team = team;
        this.arena = ArenaManager.getPlayersArena(player.getName());
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
    }

    public ArenaNPC(String pluginName, Player player, ArenaTeam team, EntityDamageEvent e1, PlayerInteractEntityEvent e2, InventoryClickEvent e3) {
        this.player = player;
        this.team = team;
        this.arena = team.getArena();
        this.pluginName = pluginName;
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
    }

    public ArenaNPC(Player player, ArenaTeam team, STATES status, String pluginName, EntityDamageEvent e1, PlayerInteractEntityEvent e2, InventoryClickEvent e3) {
        this.player = player;
        this.team = team;
        this.arena = team.getArena();
        this.pluginName = pluginName;
        this.status = status;
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public String getPluginName() {
        return pluginName;
    }

    public STATES getStatus() {
        return status;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public EntityDamageEvent getE1() {
        return e1;
    }

    public PlayerInteractEntityEvent getE2() {
        return e2;
    }

    public InventoryClickEvent getE3() {
        return e3;
    }
}