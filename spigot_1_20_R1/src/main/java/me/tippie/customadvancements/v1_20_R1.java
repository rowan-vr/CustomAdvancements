package me.tippie.customadvancements;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import me.tippie.customadvancements.advancement.AdvancementTree;
import me.tippie.customadvancements.advancement.CAdvancement;
import me.tippie.customadvancements.advancement.InvalidAdvancementException;
import me.tippie.customadvancements.advancement.PlayerOpenAdvancementTabEvent;
import me.tippie.customadvancements.advancement.requirement.AdvancementRequirement;
import me.tippie.customadvancements.player.CAPlayer;
import net.minecraft.advancements.*;
import net.minecraft.advancements.critereon.ImpossibleTrigger;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundUpdateAdvancementsPacket;
import net.minecraft.network.protocol.game.ServerboundSeenAdvancementsPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class v1_20_R1 implements InternalsProvider<Advancement, ResourceLocation, AdvancementProgress> {
    private static final HashSet<AdvancementTree> loadedTrees = new HashSet<>();
    private static final HashMap<ResourceLocation, Advancement> advancements = new HashMap<>();
    private static final HashMap<UUID, HashMap<ResourceLocation, AdvancementProgress>> playerProgress = new HashMap<>();

    @Override
    public List<Advancement> getTreeFriendlyListList(Collection<Advancement> advancements) {
        List<Advancement> result = new ArrayList<>(advancements.size());

        // Add all the advancements and its children after the parent is added so
        for (Advancement advancement : advancements) {
            if (result.contains(advancement)) continue;
            if (advancement.getParent() == null) {
                result.add(advancement);
                addChildren(advancement, result);
            }
        }

        // Add all orphan advancements.. Hopefully their parents are already sent to the client....
        for (Advancement advancement : advancements) {
            if (!result.contains(advancement)) {
                result.add(advancement);
                addChildren(advancement, result);
            }
        }

        return result;
    }

    private void addChildren(Advancement adv, List<Advancement> list) {
        for (Advancement child : adv.getChildren()) {
            if (!list.contains(child)) {
                list.add(child);
                addChildren(child, list);
            }
        }
    }

    @Override
    public CompletableFuture<Void> loadAdvancements(List<AdvancementTree> trees) {
        return CompletableFuture.runAsync(() -> {
            loadedTrees.clear();
            advancements.clear();
            playerProgress.clear();

            for (AdvancementTree tree : trees) {
                ResourceLocation rootLocation = ResourceLocation.tryParse("customadvancements:" + tree.getLabel() + "/root");
                Advancement root = Advancement.Builder.advancement()
                        .display(CraftItemStack.asNMSCopy(tree.getOptions().getDisplayItem()),
                                Component.literal(tree.getOptions().getDisplayName() == null ? tree.getLabel() : tree.getOptions().getDisplayName()),
                                Component.literal(tree.getOptions().getDescription() == null ? "No Description Set" : tree.getOptions().getDescription()),
                                ResourceLocation.tryParse(tree.getOptions().getMinecraftGuiBackground()),
                                FrameType.TASK,
                                false,
                                false,
                                false)
                        .addCriterion("0", new ImpossibleTrigger.TriggerInstance())
                        .requirements(RequirementsStrategy.AND.createRequirements(Collections.singletonList("0")))
                        .build(rootLocation);


                advancements.put(rootLocation, root);

                for (CAdvancement cAdvancement : tree.getAdvancements()) {
                    ResourceLocation location = ResourceLocation.tryParse("customadvancements:" + tree.getLabel() + "/" + cAdvancement.getLabel());


                    Advancement.Builder builder = Advancement.Builder.advancement()
                            .display(CraftItemStack.asNMSCopy(cAdvancement.getDisplayItem()),
                                    Component.literal(cAdvancement.getDisplayName() == null ? cAdvancement.getLabel() : cAdvancement.getDisplayName()),
                                    Component.literal(cAdvancement.getDescription() == null ? "No Description Set" : cAdvancement.getDescription(null)),
                                    null,
                                    getFrameType(cAdvancement.getMinecraftGuiFrame().getValue()),
                                    false,
                                    cAdvancement.isMinecraftChatAnnounce(),
                                    cAdvancement.isHidden());


                    List<String> requirements;

                    switch (cAdvancement.getMinecraftProgressType()) {
                        case COUNT -> {
                            requirements = new ArrayList<>(cAdvancement.getMaxProgress());
                            for (int i = 0; i < cAdvancement.getMaxProgress(); i++) {
                                builder.addCriterion(String.valueOf(i), new ImpossibleTrigger.TriggerInstance());
                                requirements.add(String.valueOf(i));
                            }
                        }
                        case PERCENTAGE -> {
                            requirements = new ArrayList<>(100);
                            for (int i = 0; i < 100; i++) {
                                builder.addCriterion(String.valueOf(i), new ImpossibleTrigger.TriggerInstance());
                                requirements.add(String.valueOf(i));
                            }
                        }
                        default -> {
                            requirements = new ArrayList<>(1);
                            for (int i = 0; i < 1; i++) {
                                builder.addCriterion(String.valueOf(i), new ImpossibleTrigger.TriggerInstance());
                                requirements.add(String.valueOf(i));
                            }
                        }
                    }

                    builder.requirements(RequirementsStrategy.AND.createRequirements(requirements));

                    advancements.put(location, builder.build(location));
                }

                for (CAdvancement cAdvancement : tree.getAdvancements()) {
                    ResourceLocation location = ResourceLocation.tryParse("customadvancements:" + tree.getLabel() + "/" + cAdvancement.getLabel());
                    Advancement advancement = advancements.get(location);
                    AdvancementRequirement requirement = cAdvancement.getRequirements().stream()
                            .filter(req -> req.getType() instanceof me.tippie.customadvancements.advancement.requirement.types.Advancement)
                            .findFirst().orElse(null);

                    try {
                        if (requirement != null) {

                            try {
                                CAdvancement advancementRequirement = CustomAdvancements.getAdvancementManager().getAdvancement(requirement.getValue());
                                Advancement parent = advancements.get(ResourceLocation.tryParse("customadvancements:" + advancementRequirement.getTree() + "/" + advancementRequirement.getLabel()));
                                if (parent != null) {
                                    advancements.put(location, setParent(advancement, parent));
                                } else {
                                    advancements.put(location, setParent(advancement, root));
                                }
                            } catch (InvalidAdvancementException e) {
                                advancements.put(location, setParent(advancement, root));

                            }
                        } else {
                            advancements.put(location, setParent(advancement, root));
                        }
                    } catch (RuntimeException e) {
                        CustomAdvancements.getInstance().getLogger().log(Level.SEVERE, "Failed to set put back advancement when setting the parent of " + location + " of tree " + tree.getLabel() + " and advancement " + cAdvancement.getLabel(),e);
                    }
                }
                loadedTrees.add(tree);

                TreeNodePosition.run(root);
            }

        });
    }

    @Override
    public CompletableFuture<Void> sendAdvancements(Player player, boolean clear) {
        return CompletableFuture.runAsync(() -> {
            playerProgress.computeIfAbsent(player.getUniqueId(), uuid -> createProgress());
            HashMap<ResourceLocation, AdvancementProgress> progress = playerProgress.get(player.getUniqueId());
            CAPlayer caPlayer = CustomAdvancements.getCaPlayerManager().getPlayer(player.getUniqueId());

            Collection<Advancement> sending = new HashSet<>(advancements.values());

            for (AdvancementTree tree : loadedTrees) {
                for (CAdvancement advancement : tree.getAdvancements()) {
                    ResourceLocation location = ResourceLocation.tryParse("customadvancements:" + tree.getLabel() + "/" + advancement.getLabel());
                    Advancement adv = advancements.get(location);
                    DisplayInfo displayInfo = adv.getDisplay();
                    sending.remove(adv);
                    Advancement updatedAdv = adv.deconstruct().display(
                            displayInfo.getIcon(),
                            displayInfo.getTitle(),
                            Component.literal(advancement.getDescription(player)),
                            displayInfo.getBackground(),
                            displayInfo.getFrame(),
                            !advancement.isAnnounced(player) && advancement.isMinecraftToast(),
                            displayInfo.shouldAnnounceChat(),
                            displayInfo.isHidden()
                    ).parent(adv.getParent()).build(location);
                    updatedAdv.getDisplay().setLocation(displayInfo.getX(), displayInfo.getY());
                    sending.add(updatedAdv);
                    AdvancementProgress advancementProgress = progress.get(location);
                    String progressText = advancementProgress.getProgressText();
                    int done = progressText == null ? (advancementProgress.isDone() ? 1 : 0) : Integer.parseInt(progressText.split("/")[0]);

                    try {
                        int diff;

                        switch (advancement.getMinecraftProgressType()) {
                            case COUNT -> {
                                diff = caPlayer.getProgress(advancement.getPath()) - done;
                            }
                            case PERCENTAGE -> {
                                diff = caPlayer.getProgress(advancement.getPath()) / advancement.getMaxProgress() - done;
                            }
                            default -> {
                                diff = (caPlayer.getProgress(advancement.getPath()) >= advancement.getMaxProgress() ? 1 : 0) - done;
                            }
                        }
                        if (diff < 0) {
                            for (int i = done; i > caPlayer.getProgress(advancement.getPath()); i--)
                                advancementProgress.revokeProgress(String.valueOf(i - 1));

                        } else if (diff > 0) {

                            for (int i = done; i < caPlayer.getProgress(advancement.getPath()); i++)
                                advancementProgress.grantProgress(String.valueOf(i));
                        }
                    } catch (InvalidAdvancementException ignored) {
                    }
                }
            }

            sendAdvancementPacket(player, clear, sending, new HashSet<>(), progress).join();
        });
    }

    @Override
    public CompletableFuture<Void> updateAdvancement(Player player, CAdvancement... advancements) {
        return CompletableFuture.runAsync(() -> {

            playerProgress.computeIfAbsent(player.getUniqueId(), uuid -> createProgress());
            HashMap<ResourceLocation, AdvancementProgress> progress = playerProgress.get(player.getUniqueId());
            CAPlayer caPlayer = CustomAdvancements.getCaPlayerManager().getPlayer(player.getUniqueId());
            Map<ResourceLocation, AdvancementProgress> updating = new HashMap<>();
            Map<ResourceLocation, Advancement> sending = new HashMap<>();

            for (CAdvancement advancement : advancements) {
                ResourceLocation location = ResourceLocation.tryParse("customadvancements:" + advancement.getTree() + "/" + advancement.getLabel());
                AdvancementProgress advancementProgress = progress.get(location);
                String progressText = advancementProgress.getProgressText();
                int done = progressText == null ? (advancementProgress.isDone() ? 1 : 0) : Integer.parseInt(progressText.split("/")[0]);

                try {
                    int diff;

                    switch (advancement.getMinecraftProgressType()) {
                        case COUNT -> {
                            diff = caPlayer.getProgress(advancement.getPath()) - done;
                        }
                        case PERCENTAGE -> {
                            diff = caPlayer.getProgress(advancement.getPath()) / advancement.getMaxProgress() - done;
                        }
                        default -> {
                            diff = (caPlayer.getProgress(advancement.getPath()) >= advancement.getMaxProgress() ? 1 : 0) - done;
                        }
                    }
                    if (diff < 0) {
                        for (int i = done; i > caPlayer.getProgress(advancement.getPath()); i--)
                            advancementProgress.revokeProgress(String.valueOf(i - 1));

                    } else if (diff > 0) {
                        for (int i = done; i < caPlayer.getProgress(advancement.getPath()); i++)
                            advancementProgress.grantProgress(String.valueOf(i));
                    }

                    if (diff != 0) {
                        updating.put(location, advancementProgress);
                        Advancement adv = v1_20_R1.advancements.get(location);
                        DisplayInfo displayInfo = adv.getDisplay();
                        Advancement updatedAdv = adv.deconstruct().display(
                                displayInfo.getIcon(),
                                displayInfo.getTitle(),
                                Component.literal(advancement.getDescription(player)),
                                displayInfo.getBackground(),
                                displayInfo.getFrame(),
                                !advancement.isAnnounced(player) && advancement.isMinecraftToast(),
                                displayInfo.shouldAnnounceChat(),
                                displayInfo.isHidden()
                        ).parent(adv.getParent()).build(location);
                        updatedAdv.getDisplay().setLocation(displayInfo.getX(), displayInfo.getY());
                        sending.put(location, updatedAdv);

                    }
                } catch (InvalidAdvancementException ignored) {
                }

            }


            sendAdvancementPacket(player, false, sending.values(), sending.keySet(), updating).join();
        });
    }

    @Override
    public void registerAdvancementTabListener(Player player) {

        // Use reflection to get newly private net.minecraft.server.network.ServerGamePacketListenerImpl.connection
        Field nettyConnectionField;
        try {
            // noinspection JavaReflectionMemberAccess
            nettyConnectionField = ServerGamePacketListenerImpl.class.getDeclaredField("h"); // obf 'connection'
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        nettyConnectionField.setAccessible(true);

        ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
        Connection minecraftConnection;
        try {
            minecraftConnection = (Connection) nettyConnectionField.get(connection);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        minecraftConnection.channel.pipeline().addBefore("packet_handler", "advancement_tab_handler", new ChannelDuplexHandler() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

                if (msg instanceof ServerboundSeenAdvancementsPacket packet) {
                    PlayerOpenAdvancementTabEvent.Action action =
                            packet.getAction().equals(ServerboundSeenAdvancementsPacket.Action.OPENED_TAB) ?
                                    PlayerOpenAdvancementTabEvent.Action.OPEN :
                                    PlayerOpenAdvancementTabEvent.Action.CLOSE;

                    NamespacedKey key = packet.getTab() == null ? null : new NamespacedKey(packet.getTab().getNamespace(), packet.getTab().getPath());

                    PlayerOpenAdvancementTabEvent event = new PlayerOpenAdvancementTabEvent(player, action, key);
                    Bukkit.getPluginManager().callEvent(event);
                }

                super.channelRead(ctx, msg);
            }
        });
    }

    @Override
    public CompletableFuture<Void> sendAdvancementPacketImpl(Player player, boolean clear, Collection<Advancement> advancements, Set<ResourceLocation> remove, Map<ResourceLocation, AdvancementProgress> progress) {
        return CompletableFuture.runAsync(() -> {
            ClientboundUpdateAdvancementsPacket packet = new ClientboundUpdateAdvancementsPacket(clear, advancements, remove, progress);
            ((CraftPlayer) player).getHandle().connection.send(packet);
        });
    }

    @Override
    public String getResourceLocationOfAdvancement(Advancement advancement) {
        return advancement.getId().toString();
    }

    @Override
    public String getResourceLocationOfNms(ResourceLocation location) {
        return location.toString();
    }

    @Override
    public ResourceLocation getNmsLocationFromString(String location) {
        return ResourceLocation.tryParse(location);
    }

    private FrameType getFrameType(String frame) {
        return switch (frame.toLowerCase()) {
            case "task" -> FrameType.TASK;
            case "challenge" -> FrameType.CHALLENGE;
            case "goal" -> FrameType.GOAL;
            default -> throw new IllegalArgumentException("Frame type must be one of [task, challenge, goal]!");
        };
    }

    private HashMap<ResourceLocation, AdvancementProgress> createProgress() {
        HashMap<ResourceLocation, AdvancementProgress> result = new HashMap<>();
        for (AdvancementTree tree : loadedTrees) {
            ResourceLocation rootLocation = ResourceLocation.tryParse("customadvancements:" + tree.getLabel() + "/root");


            AdvancementProgress rootProgress = new AdvancementProgress();


            Map<String, Criterion> criterion = new HashMap<>();
            criterion.put("0", new Criterion(new ImpossibleTrigger.TriggerInstance()));

            rootProgress.update(criterion, RequirementsStrategy.AND.createRequirements(Collections.singletonList("0")));
            rootProgress.grantProgress("0");

            result.put(rootLocation, rootProgress);

            for (CAdvancement cAdvancement : tree.getAdvancements()) {
                ResourceLocation location = ResourceLocation.tryParse("customadvancements:" + tree.getLabel() + "/" + cAdvancement.getLabel());

                AdvancementProgress advancementProgress = new AdvancementProgress();
                Map<String, Criterion> aCriterion = new HashMap<>();
                List<String> aRequirements = new ArrayList<>(cAdvancement.getMaxProgress());

                for (int i = 0; i < cAdvancement.getMaxProgress(); i++) {
                    aCriterion.put(String.valueOf(i), new Criterion(new ImpossibleTrigger.TriggerInstance()));
                    aRequirements.add(String.valueOf(i));
                }

                advancementProgress.update(aCriterion, RequirementsStrategy.AND.createRequirements(aRequirements));
                result.put(location, advancementProgress);
            }
        }
        return result;
    }

    private Advancement setParent(Advancement advancement, Advancement parent) {
        List<Advancement> children = new ArrayList<>();
        for (Advancement child : advancement.getChildren())
            children.add(child);

        try {
            Advancement newAdvancement = advancement.deconstruct().parent(parent).build(advancement.getId());

            for (Advancement child : children)
                newAdvancement.addChild(child);

            parent.addChild(newAdvancement);
            return newAdvancement;
        } catch (Exception e) {
            throw new RuntimeException("Failed to set parent of advancement.", e);
        }
    }
}
