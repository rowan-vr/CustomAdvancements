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
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_20_R2.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.A;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public class v1_20_R2 implements InternalsProvider<AdvancementNode, ResourceLocation, AdvancementProgress> {
    private static final HashSet<AdvancementTree> loadedTrees = new HashSet<>();
    private static final HashMap<ResourceLocation, AdvancementNode> advancements = new HashMap<>();
    private static final HashMap<UUID, HashMap<ResourceLocation, AdvancementProgress>> playerProgress = new HashMap<>();

    @Override
    public List<AdvancementNode> getTreeFriendlyListList(Collection<AdvancementNode> advancements) {
        List<AdvancementNode> result = new ArrayList<>(advancements.size());
        Map<ResourceLocation, AdvancementNode> updatedAdvancements = new HashMap<>(advancements.size());
        for (AdvancementNode advancement : advancements) {
            updatedAdvancements.put(advancement.holder().id(), advancement);
        }

        // Add all the advancements and its children after the parent is added so
        for (AdvancementNode advancement : advancements) {
            if (result.contains(advancement)) continue;
            if (advancement.parent() == null) {
                result.add(advancement);
                addChildren(advancement, result,updatedAdvancements);
            }
        }

        // Add all orphan advancements.. Hopefully their parents are already sent to the client....
        for (AdvancementNode advancement : advancements) {
            if (!result.contains(advancement)) {
                result.add(advancement);
                addChildren(advancement, result,updatedAdvancements);
            }
        }

        return result;
    }

    private void addChildren(AdvancementNode adv, List<AdvancementNode> list, Map<ResourceLocation, AdvancementNode> advancements) {
        for (AdvancementNode childTemplate : adv.children()) {
            AdvancementNode child = advancements.get(childTemplate.holder().id());

            if (!list.contains(child)) {
                list.add(child);
                addChildren(child, list,advancements);
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
                AdvancementHolder rootHolder = Advancement.Builder.advancement()
                        .display(CraftItemStack.asNMSCopy(tree.getOptions().getDisplayItem()),
                                Component.literal(tree.getOptions().getDisplayName() == null ? tree.getLabel() : tree.getOptions().getDisplayName()),
                                Component.literal(tree.getOptions().getDescription() == null ? "No Description Set" : tree.getOptions().getDescription()),
                                ResourceLocation.tryParse(tree.getOptions().getMinecraftGuiBackground()),
                                FrameType.TASK,
                                false,
                                false,
                                false)
                        .addCriterion("0", new Criterion<>(new ImpossibleTrigger(), new ImpossibleTrigger.TriggerInstance()))
                        .requirements(AdvancementRequirements.Strategy.AND.create(Collections.singletonList("0")))
                        .build(rootLocation);
                AdvancementNode root = new AdvancementNode(rootHolder, null);


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
                                builder.addCriterion(String.valueOf(i),  new Criterion<>(new ImpossibleTrigger(), new ImpossibleTrigger.TriggerInstance()));
                                requirements.add(String.valueOf(i));
                            }
                        }
                        case PERCENTAGE -> {
                            requirements = new ArrayList<>(100);
                            for (int i = 0; i < 100; i++) {
                                builder.addCriterion(String.valueOf(i),  new Criterion<>(new ImpossibleTrigger(), new ImpossibleTrigger.TriggerInstance()));
                                requirements.add(String.valueOf(i));
                            }
                        }
                        default -> {
                            requirements = new ArrayList<>(1);
                            for (int i = 0; i < 1; i++) {
                                builder.addCriterion(String.valueOf(i),  new Criterion<>(new ImpossibleTrigger(), new ImpossibleTrigger.TriggerInstance()));
                                requirements.add(String.valueOf(i));
                            }
                        }
                    }

                    builder.requirements(AdvancementRequirements.Strategy.AND.create(requirements));

                    advancements.put(location, new AdvancementNode(builder.build(location),null));
                }

                for (CAdvancement cAdvancement : tree.getAdvancements()) {
                    ResourceLocation location = ResourceLocation.tryParse("customadvancements:" + tree.getLabel() + "/" + cAdvancement.getLabel());
                    AdvancementNode advancement = advancements.get(location);
                    AdvancementRequirement requirement = cAdvancement.getRequirements().stream()
                            .filter(req -> req.getType() instanceof me.tippie.customadvancements.advancement.requirement.types.Advancement)
                            .findFirst().orElse(null);

                    try {
                        if (requirement != null) {

                            try {
                                CAdvancement advancementRequirement = CustomAdvancements.getAdvancementManager().getAdvancement(requirement.getValue());
                                AdvancementNode parent = advancements.get(ResourceLocation.tryParse("customadvancements:" + advancementRequirement.getTree() + "/" + advancementRequirement.getLabel()));
                                if (parent != null) {
                                    advancements.put(location, setParent(advancement, parent));
                                } else {
                                    advancements.put(location, setParent(advancement,root));
                                }
                            } catch (InvalidAdvancementException e) {
                                advancements.put(location, setParent(advancement,root));

                            }
                        } else {
                            advancements.put(location, setParent(advancement,root));
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

            Collection<AdvancementNode> sending = new HashSet<>(advancements.values());

            for (AdvancementTree tree : loadedTrees) {
                for (CAdvancement advancement : tree.getAdvancements()) {
                    ResourceLocation location = ResourceLocation.tryParse("customadvancements:" + tree.getLabel() + "/" + advancement.getLabel());
                    AdvancementNode adv = advancements.get(location);
                    DisplayInfo displayInfo = adv.advancement().display().orElseThrow();

                    sending.remove(adv);
                    AdvancementHolder updatedAdvHolder =deconstructAdvancement(adv.advancement()).display(
                            displayInfo.getIcon(),
                            displayInfo.getTitle(),
                            Component.literal(advancement.getDescription(player)),
                            displayInfo.getBackground(),
                            displayInfo.getFrame(),
                            !advancement.isAnnounced(player) && advancement.isMinecraftToast(),
                            displayInfo.shouldAnnounceChat(),
                            displayInfo.isHidden()
                    ).parent(adv.parent().holder()).build(location);
                    AdvancementNode updatedAdv = new AdvancementNode(updatedAdvHolder, adv.parent());
                    updatedAdv.advancement().display().orElseThrow().setLocation(displayInfo.getX(), displayInfo.getY());
                    sending.add(updatedAdv);
                    AdvancementProgress advancementProgress = progress.get(location);
                    Optional<String> progressText = advancementProgress.getProgressText() == null ? Optional.empty() : Optional.of(advancementProgress.getProgressText().getString());
                    int done = progressText.map(s -> Integer.parseInt(s.split("/")[0])).orElseGet(() -> (advancementProgress.isDone() ? 1 : 0));

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
            Map<ResourceLocation, AdvancementNode> sending = new HashMap<>();

            for (CAdvancement advancement : advancements) {
                ResourceLocation location = ResourceLocation.tryParse("customadvancements:" + advancement.getTree() + "/" + advancement.getLabel());
                AdvancementProgress advancementProgress = progress.get(location);
                Optional<String> progressText = advancementProgress.getProgressText() == null ? Optional.empty() : Optional.of(advancementProgress.getProgressText().getString());
                int done = progressText.map(s -> Integer.parseInt(s.split("/")[0])).orElseGet(() -> (advancementProgress.isDone() ? 1 : 0));

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

                    if (true) {
                        updating.put(location, advancementProgress);
                        AdvancementNode adv = v1_20_R2.advancements.get(location);
                        DisplayInfo displayInfo = adv.advancement().display().orElseThrow();
                        AdvancementHolder updatedAdvHolder = deconstructAdvancement(adv.advancement()).display(
                                displayInfo.getIcon(),
                                displayInfo.getTitle(),
                                Component.literal(advancement.getDescription(player)),
                                displayInfo.getBackground(),
                                displayInfo.getFrame(),
                                !advancement.isAnnounced(player) && advancement.isMinecraftToast(),
                                displayInfo.shouldAnnounceChat(),
                                displayInfo.isHidden()
                        ).parent(adv.parent().holder()).build(location);
                        AdvancementNode updatedAdv = new AdvancementNode(updatedAdvHolder, adv.parent());
                        updatedAdv.advancement().display().orElseThrow().setLocation(displayInfo.getX(), displayInfo.getY());
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
            nettyConnectionField = ServerCommonPacketListenerImpl.class.getDeclaredField("c"); // obf 'connection'
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
    public CompletableFuture<Void> sendAdvancementPacketImpl(Player player, boolean clear, Collection<AdvancementNode> advancements, Set<ResourceLocation> remove, Map<ResourceLocation, AdvancementProgress> progress) {
        return CompletableFuture.runAsync(() -> {
            ClientboundUpdateAdvancementsPacket packet = new ClientboundUpdateAdvancementsPacket(clear, advancements.stream().map(AdvancementNode::holder).toList(), remove, progress);
            System.out.println("Sending packet to " + player.getName() + " with " + advancements.size() + " advancements, " + remove.size() + " removed advancements and " + progress.size() + " progress updates and reset " + clear);
            System.out.println("---Advancements---");
            advancements.forEach(advancement -> {
                System.out.println("id: " + advancement.holder().id());
                System.out.println("description: " + advancement.advancement().display().orElseThrow().getDescription());
                System.out.println("---");
            });
            ((CraftPlayer) player).getHandle().connection.send(packet);
        });
    }

    @Override
    public String getResourceLocationOfAdvancement(AdvancementNode advancement) {
        return advancement.holder().id().toString();
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


//            Map<String, Criterion<?>> criterion = new HashMap<>();
//            criterion.put("0", new Criterion<>(new ImpossibleTrigger(), new ImpossibleTrigger.TriggerInstance()));

            rootProgress.update(AdvancementRequirements.Strategy.AND.create(Collections.singletonList("0")));
            rootProgress.grantProgress("0");

            result.put(rootLocation, rootProgress);

            for (CAdvancement cAdvancement : tree.getAdvancements()) {
                ResourceLocation location = ResourceLocation.tryParse("customadvancements:" + tree.getLabel() + "/" + cAdvancement.getLabel());

                AdvancementProgress advancementProgress = new AdvancementProgress();
//                Map<String, Criterion> aCriterion = new HashMap<>();
                List<String> aRequirements = new ArrayList<>(cAdvancement.getMaxProgress());

                for (int i = 0; i < cAdvancement.getMaxProgress(); i++) {
//                    aCriterion.put(String.valueOf(i), new Criterion(new ImpossibleTrigger.TriggerInstance()));
                    aRequirements.add(String.valueOf(i));
                }

                advancementProgress.update(AdvancementRequirements.Strategy.AND.create(aRequirements));
                result.put(location, advancementProgress);
            }
        }
        return result;
    }

    private AdvancementNode setParent(AdvancementNode advancement, AdvancementNode parent) {
        List<AdvancementNode> children = new ArrayList<>();
        for (AdvancementNode child : advancement.children())
            children.add(child);

        try {
            AdvancementHolder newAdvancementHolder = deconstructAdvancement(advancement.advancement()).parent(parent.holder()).build(advancement.holder().id());
            AdvancementNode newAdvancement = new AdvancementNode(newAdvancementHolder, parent);

            for (AdvancementNode child : children)
                newAdvancement.addChild(child);

            parent.addChild(newAdvancement);
            return newAdvancement;
        } catch (Exception e) {
            throw new RuntimeException("Failed to set parent of advancement.", e);
        }
    }

    private Advancement.Builder deconstructAdvancement(Advancement advancement) {
        Advancement.Builder builder = Advancement.Builder.advancement();
        if (advancement.display().isPresent())
            builder.display(advancement.display().get());
        for (Map.Entry<String, Criterion<?>> entry : advancement.criteria().entrySet())
            builder.addCriterion(entry.getKey(), entry.getValue());
        builder.requirements(advancement.requirements());
        return builder;
    }
}
