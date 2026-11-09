package dev.pryvietmir.locofixes.client.ponder;

import com.simibubi.create.AllItems;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import java.util.function.UnaryOperator;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.PonderPalette;
import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.WorldSectionElement;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

/**
 * Scenes for Loconautics blocks without a Ponder entry. The schematics live in
 * assets/loconautics_fixes/ponder. Each text line has an English default here and a
 * translation key in the lang files (loconautics_fixes.ponder.&lt;scene&gt;.text_N).
 */
public final class ExtraScenes {
    private ExtraScenes() {
    }

    // ---------------------------------------------------------------- transmission

    public static void transmission(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = open(builder, "transmission", "Controlling Speed with a Transmission");
        BlockPos gearbox = util.grid().at(2, 1, 2);
        BlockPos speedSide = util.grid().at(2, 1, 1);
        BlockPos directionSide = util.grid().at(2, 1, 3);
        Selection input = util.select().fromTo(0, 1, 2, 2, 1, 2);
        Selection output = util.select().fromTo(3, 1, 2, 4, 1, 2);

        scene.world().showSection(util.select().fromTo(0, 1, 2, 4, 1, 2), Direction.DOWN);
        scene.idle(15);
        scene.world().setKineticSpeed(input, 32);
        scene.idle(10);
        scene.overlay().showText(70)
                .text("The Transmission passes rotation through at a speed set by Redstone")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(gearbox));
        scene.idle(80);
        scene.overlay().showText(50)
                .text("Without a signal the output stays still")
                .placeNearTarget().pointAt(util.vector().topOf(3, 1, 2));
        scene.idle(60);

        scene.world().showSection(util.select().position(speedSide), Direction.DOWN);
        scene.idle(10);
        scene.world().setKineticSpeed(output, 256);
        scene.effects().rotationSpeedIndicator(util.grid().at(3, 1, 2));
        scene.overlay().showText(90)
                .text("A signal on this side sets the output speed: full strength gives 256 RPM, whatever the input speed is")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(speedSide));
        scene.idle(100);

        scene.world().showSection(util.select().position(directionSide), Direction.DOWN);
        scene.idle(10);
        scene.world().toggleRedstonePower(util.select().position(directionSide));
        scene.effects().indicateRedstone(directionSide);
        scene.world().setKineticSpeed(output, -256);
        scene.effects().rotationDirectionIndicator(util.grid().at(3, 1, 2));
        scene.overlay().showText(70)
                .text("A signal on the opposite side reverses the output")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(directionSide));
        scene.idle(80);

        scene.overlay().showControls(util.vector().topOf(gearbox), Pointing.DOWN, 40).rightClick().whileSneaking();
        scene.idle(10);
        scene.overlay().showText(80)
                .text("Sneak Click it to set Redstone Link frequencies for speed and direction, so it can be controlled wirelessly")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(gearbox));
        scene.idle(90);
        scene.overlay().showText(80)
                .text("Sneak Click an Analog Controller with the Transmission item to copy the controller's frequencies onto it")
                .placeNearTarget().pointAt(util.vector().topOf(gearbox));
        scene.idle(90);
    }

    // ---------------------------------------------------------------- analog controller

    public static void analogController(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = open(builder, "analog_controller", "Driving a Train with the Analog Controller");
        BlockPos controller = util.grid().at(2, 2, 2);

        scene.world().showSection(util.select().fromTo(2, 1, 2, 2, 2, 2), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(70)
                .text("The Analog Controller is the driver's control stand of a physics train")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(controller));
        scene.idle(80);
        scene.overlay().showControls(util.vector().topOf(controller), Pointing.DOWN, 40).rightClick();
        scene.idle(10);
        scene.overlay().showText(60)
                .text("Right Click it to take control, and again to let go")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(controller));
        scene.idle(70);
        scene.overlay().showText(80)
                .text("While you control it, throttle and steering are sent over Redstone Link frequencies")
                .placeNearTarget().pointAt(util.vector().topOf(controller));
        scene.idle(90);
        scene.overlay().showControls(util.vector().topOf(controller), Pointing.DOWN, 40).rightClick().whileSneaking();
        scene.idle(10);
        scene.overlay().showText(60)
                .text("Sneak Click it to choose those frequencies")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(controller));
        scene.idle(70);

        scene.world().showSection(util.select().position(0, 1, 2), Direction.DOWN);
        scene.world().showSection(util.select().position(4, 1, 2), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(70)
                .text("Transmissions listen to the throttle...")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(0, 1, 2));
        scene.idle(80);
        scene.overlay().showText(60)
                .text("...and Bearing Axles listen to the steering")
                .placeNearTarget().pointAt(util.vector().topOf(4, 1, 2));
        scene.idle(70);
        scene.overlay().showControls(util.vector().topOf(controller), Pointing.DOWN, 50)
                .rightClick().whileSneaking().withItem(item("transmission"));
        scene.idle(10);
        scene.overlay().showText(80)
                .text("Sneak Click the controller with either of their items to copy the matching frequencies onto it")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(controller));
        scene.idle(90);
        scene.overlay().showControls(util.vector().topOf(controller), Pointing.DOWN, 40)
                .rightClick().withItem(new ItemStack(Items.BLUE_DYE));
        scene.idle(10);
        scene.overlay().showText(50)
                .text("Dye can be used to change its color")
                .placeNearTarget().pointAt(util.vector().topOf(controller));
        scene.idle(60);
    }

    // ---------------------------------------------------------------- bearing axle

    public static void bearingAxle(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = open(builder, "bearing_axle", "Driving a Carriage with a Bearing Axle");
        BlockPos bogey = util.grid().at(2, 2, 2);
        BlockPos axle = util.grid().at(3, 2, 2);
        BlockPos shaft = util.grid().at(4, 2, 2);

        scene.world().showSection(util.select().fromTo(2, 1, 0, 2, 1, 4), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(bogey), Direction.DOWN);
        scene.idle(15);
        scene.world().showSection(util.select().position(axle), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(70)
                .text("The Bearing Axle is the driving wheel of a carriage")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(axle));
        scene.idle(80);
        scene.overlay().showOutline(PonderPalette.GREEN, "bogey", util.select().position(bogey), 70);
        scene.overlay().showText(70)
                .text("Place it beside a bogey or on top of one")
                .placeNearTarget().pointAt(util.vector().topOf(bogey));
        scene.idle(80);

        scene.world().showSection(util.select().position(shaft), Direction.WEST);
        scene.idle(10);
        scene.world().setKineticSpeed(util.select().fromTo(axle, shaft), 32);
        scene.effects().rotationSpeedIndicator(shaft);
        scene.overlay().showText(70)
                .text("Rotation from the back of the axle drives the train along the rails")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(shaft));
        scene.idle(80);
        scene.overlay().showText(60)
                .text("Only one Bearing Axle can drive each carriage")
                .colored(PonderPalette.RED).placeNearTarget().pointAt(util.vector().topOf(axle));
        scene.idle(70);
        scene.overlay().showText(80)
                .text("It steers using Left and Right signals: Redstone on its sides, or Redstone Link frequencies")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(axle));
        scene.idle(90);
        scene.overlay().showControls(util.vector().topOf(axle), Pointing.DOWN, 40).withItem(AllItems.GOGGLES.asStack());
        scene.idle(10);
        scene.overlay().showText(70)
                .text("Engineer's Goggles show whether it is set up correctly, its gear and the load")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(axle));
        scene.idle(80);
    }

    // ---------------------------------------------------------------- handbrake

    public static void handbrake(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = open(builder, "handbrake", "Parking a Carriage with the Handbrake");
        BlockPos brake = util.grid().at(2, 2, 2);
        BlockPos lever = util.grid().at(1, 2, 2);

        scene.world().showSection(util.select().fromTo(2, 1, 2, 2, 2, 2), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(60)
                .text("The Handbrake is a parking brake for a single carriage")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(brake));
        scene.idle(70);
        scene.world().showSection(util.select().fromTo(1, 1, 2, 1, 2, 2), Direction.DOWN);
        scene.idle(15);
        scene.world().toggleRedstonePower(util.select().fromTo(lever, brake));
        scene.effects().indicateRedstone(lever);
        scene.overlay().showText(80)
                .text("While powered by Redstone it locks the carriage to the rail, so it can't be driven or roll down a slope")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(brake));
        scene.idle(90);
        scene.world().toggleRedstonePower(util.select().fromTo(lever, brake));
        scene.overlay().showText(50)
                .text("Without a signal the carriage is free again")
                .placeNearTarget().pointAt(util.vector().topOf(brake));
        scene.idle(60);
    }

    // ---------------------------------------------------------------- knuckle

    public static void knuckle(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = open(builder, "knuckle", "Coupling Carriages with Knuckles");
        BlockPos left = util.grid().at(1, 1, 2);
        BlockPos right = util.grid().at(3, 1, 2);

        scene.world().showSection(util.select().fromTo(0, 1, 2, 1, 1, 2), Direction.DOWN);
        ElementLink<WorldSectionElement> car = scene.world().showIndependentSection(util.select().fromTo(3, 1, 2, 4, 1, 2), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(60)
                .text("Knuckles are automatic couplers for train carriages")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(left));
        scene.idle(70);
        scene.world().moveSection(car, util.vector().of(-1, 0, 0), 20);
        scene.idle(20);
        scene.world().modifyBlock(left, set("connected", "true"), false);
        scene.world().modifyBlock(right, set("connected", "true"), false);
        scene.effects().indicateSuccess(left);
        scene.overlay().showText(80)
                .text("When two Knuckles meet they lock together on a pivot, so the cars hold as one train and still take corners")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(left));
        scene.idle(90);
        scene.overlay().showControls(util.vector().topOf(left), Pointing.DOWN, 40).rightClick().withItem(AllItems.WRENCH.asStack());
        scene.idle(10);
        scene.world().modifyBlock(left, set("connected", "false"), false);
        scene.world().modifyBlock(right, set("connected", "false"), false);
        scene.world().moveSection(car, util.vector().of(1, 0, 0), 20);
        scene.overlay().showText(60)
                .text("Right Click with a Wrench to uncouple them")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(left));
        scene.idle(70);
        scene.overlay().showText(60)
                .text("On a Knuckle that is not coupled, the Wrench rotates it instead")
                .placeNearTarget().pointAt(util.vector().topOf(left));
        scene.idle(70);
        scene.overlay().showControls(util.vector().topOf(left), Pointing.DOWN, 40).rightClick().withItem(new ItemStack(Items.OAK_PLANKS));
        scene.idle(10);
        scene.overlay().showText(70)
                .text("Like Copycat blocks, it takes the look of a block you click it with")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(left));
        scene.idle(80);
    }

    // ---------------------------------------------------------------- screw link

    public static void screwLink(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = open(builder, "screw_link", "Coupling Carriages with Screw Links");
        BlockPos left = util.grid().at(1, 1, 2);
        BlockPos right = util.grid().at(3, 1, 2);

        scene.world().showSection(util.select().fromTo(0, 1, 2, 1, 1, 2), Direction.DOWN);
        scene.world().showSection(util.select().fromTo(3, 1, 2, 4, 1, 2), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(70)
                .text("Screw Link Headstocks couple carriages with a hanging link and hook")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(left));
        scene.idle(80);
        scene.overlay().showLine(PonderPalette.WHITE, util.vector().blockSurface(left, Direction.EAST),
                util.vector().blockSurface(right, Direction.WEST), 70);
        scene.overlay().showText(70)
                .text("Grab the hook at the end of one link and hang it on the facing headstock to couple them")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(right));
        scene.idle(80);
        scene.overlay().showControls(util.vector().topOf(left), Pointing.DOWN, 40).rightClick();
        scene.idle(10);
        scene.overlay().showText(60)
                .text("Right Click a coupled headstock with an empty hand to uncouple")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(left));
        scene.idle(70);
    }

    // ---------------------------------------------------------------- copycat headstock

    public static void copycatHeadstock(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = open(builder, "copycat_headstock", "Using the Copycat Headstock");
        BlockPos headstock = util.grid().at(2, 1, 2);

        scene.world().showSection(util.select().fromTo(1, 1, 2, 2, 1, 2), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(60)
                .text("The Copycat Headstock is an end beam for carriages")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(headstock));
        scene.idle(70);
        scene.overlay().showControls(util.vector().topOf(headstock), Pointing.DOWN, 40).rightClick().withItem(new ItemStack(Items.SPRUCE_PLANKS));
        scene.idle(10);
        scene.overlay().showText(70)
                .text("Like Copycat blocks, it takes the look of a block you click it with")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(headstock));
        scene.idle(80);
        scene.overlay().showText(60)
                .text("Clicking with the same block again switches between its variants")
                .placeNearTarget().pointAt(util.vector().topOf(headstock));
        scene.idle(70);
    }

    // ---------------------------------------------------------------- turntable

    public static void turntable(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = open(builder, "turntable", "Turning Trains with a Turntable Bearing");
        BlockPos bearing = util.grid().at(2, 2, 2);
        BlockPos lever = util.grid().at(0, 2, 2);

        scene.world().showSection(util.select().fromTo(2, 1, 2, 2, 2, 2), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(70)
                .text("The Turntable Bearing turns the platform in front of it by an exact angle, then holds it there")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(bearing));
        scene.idle(80);
        ElementLink<WorldSectionElement> plate = scene.world().showIndependentSection(util.select().fromTo(1, 3, 1, 3, 4, 3), Direction.DOWN);
        scene.world().configureCenterOfRotation(plate, util.vector().centerOf(2, 3, 2));
        scene.idle(15);
        scene.overlay().showControls(util.vector().blockSurface(bearing, Direction.WEST), Pointing.LEFT, 40).rightClick();
        scene.idle(10);
        scene.world().modifyBlock(bearing, set("assembled", "true"), false);
        scene.overlay().showText(70)
                .text("Right Click it with an empty hand to turn the blocks in front of it into a turning platform")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().blockSurface(bearing, Direction.WEST));
        scene.idle(80);
        scene.world().setKineticSpeed(util.select().fromTo(2, 1, 2, 2, 2, 2), 16);
        scene.effects().rotationSpeedIndicator(util.grid().at(2, 1, 2));
        scene.overlay().showText(60)
                .text("It needs Rotation to turn")
                .placeNearTarget().pointAt(util.vector().blockSurface(util.grid().at(2, 1, 2), Direction.WEST));
        scene.idle(70);

        scene.world().showSection(util.select().fromTo(0, 1, 2, 1, 2, 2), Direction.DOWN);
        scene.idle(15);
        scene.world().toggleRedstonePower(util.select().fromTo(0, 2, 2, 1, 2, 2));
        scene.effects().indicateRedstone(lever);
        scene.world().rotateSection(plate, 0, 90, 0, 40);
        scene.overlay().showText(70)
                .text("A Redstone signal starts its next turn")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(lever));
        scene.idle(80);
        scene.overlay().showControls(util.vector().blockSurface(bearing, Direction.WEST), Pointing.LEFT, 40).rightClick().whileSneaking();
        scene.idle(10);
        scene.overlay().showText(90)
                .text("Sneak Click it with an empty hand to edit its instructions: turn by an angle, wait, wait for Redstone, or follow a train schedule")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().blockSurface(bearing, Direction.WEST));
        scene.idle(100);
        scene.overlay().showText(90)
                .text("Sneak Click it with a Train Signal to bind the signal: it then stays red while the platform is turned away or holds a train")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(2, 4, 2));
        scene.idle(100);
        scene.overlay().showText(60)
                .text("A train longer than the platform can't be turned")
                .colored(PonderPalette.RED).placeNearTarget().pointAt(util.vector().topOf(2, 4, 2));
        scene.idle(70);
    }

    // ---------------------------------------------------------------- industrial fluids

    public static void industrialPipes(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = open(builder, "industrial_pipes", "Industrial Pipes and Pumps");
        BlockPos pump = util.grid().at(1, 1, 2);

        scene.world().showSection(util.select().fromTo(0, 1, 2, 3, 1, 2), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(80)
                .text("Industrial Pipes, Glass Pipes, Smart Pipes and Valves work the same way as Create's own")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(util.grid().at(2, 1, 2)));
        scene.idle(90);
        scene.world().showSection(util.select().fromTo(0, 1, 3, 1, 1, 3), Direction.NORTH);
        scene.idle(10);
        scene.world().setKineticSpeed(util.select().fromTo(0, 1, 3, 1, 1, 3), 32);
        scene.world().setKineticSpeed(util.select().position(pump), -32);
        scene.world().propagatePipeChange(pump);
        scene.effects().rotationSpeedIndicator(util.grid().at(1, 1, 3));
        scene.overlay().showText(70)
                .text("The Industrial Pump is powered like a Mechanical Pump...")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(pump));
        scene.idle(80);
        scene.overlay().showText(60)
                .text("...and pushes fluid 1.5 times as far")
                .placeNearTarget().pointAt(util.vector().topOf(pump));
        scene.idle(70);
    }

    public static void industrialTank(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = open(builder, "industrial_tank", "Storing Fluids in Industrial Tanks");
        BlockPos tank = util.grid().at(3, 1, 2);

        scene.world().showSection(util.select().position(tank), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(60)
                .text("Industrial Fluid Tanks hold 12,000mb per block")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(tank));
        scene.idle(70);
        scene.overlay().showText(70)
                .text("A tank lays along the face you click, so it can stand up or lie down")
                .placeNearTarget().pointAt(util.vector().topOf(tank));
        scene.idle(80);
        scene.overlay().showText(80)
                .text("Every touching tank joins into one, in any shape up to 5 blocks wide")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(tank));
        scene.idle(90);
        scene.overlay().showText(80)
                .text("Placing a tank on the end of another places a whole layer at once. Sneak to place a single block")
                .placeNearTarget().pointAt(util.vector().topOf(tank));
        scene.idle(90);
        scene.overlay().showControls(util.vector().topOf(tank), Pointing.DOWN, 40).rightClick().withItem(AllItems.WRENCH.asStack());
        scene.idle(10);
        scene.overlay().showText(60)
                .text("Right Click with a Wrench to switch the windows of the whole tank")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(tank));
        scene.idle(70);
    }

    // ---------------------------------------------------------------- hoses

    public static void hoseConnector(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = open(builder, "hose_connector", "Linking Tanks with Hose Connectors");
        BlockPos connector = util.grid().at(2, 1, 2);

        scene.world().showSection(util.select().position(1, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(connector), Direction.WEST);
        scene.idle(15);
        scene.overlay().showText(60)
                .text("A Hose Connector goes on the side of a tank")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(connector));
        scene.idle(70);
        scene.overlay().showText(80)
                .text("Hoses strung between connectors carry fluid from one carriage's tank to another")
                .placeNearTarget().pointAt(util.vector().topOf(connector));
        scene.idle(90);
        scene.overlay().showControls(util.vector().topOf(connector), Pointing.DOWN, 40).rightClick().withItem(AllItems.WRENCH.asStack());
        scene.idle(10);
        scene.overlay().showText(80)
                .text("Right Click it with a Wrench to remove its newest hose. With no hoses left, the Wrench breaks the connector off")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(connector));
        scene.idle(90);
    }

    public static void nozzleSocket(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = open(builder, "nozzle_socket", "Docking Nozzles in a Nozzle Socket");
        BlockPos socket = util.grid().at(3, 2, 2);

        scene.world().showSection(util.select().position(3, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(socket), Direction.DOWN);
        scene.idle(15);
        scene.overlay().showText(60)
                .text("The Nozzle Socket is a port on a tank where a Hose Nozzle can dock")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(socket));
        scene.idle(70);
        scene.overlay().showControls(util.vector().topOf(socket), Pointing.DOWN, 40).rightClick();
        scene.idle(10);
        scene.world().modifyBlock(socket, set("open", "true"), false);
        scene.overlay().showText(60)
                .text("Right Click it to open or close its lid")
                .attachKeyFrame().placeNearTarget().pointAt(util.vector().topOf(socket));
        scene.idle(70);
        scene.overlay().showText(60)
                .text("The lid stays open while a nozzle is docked")
                .placeNearTarget().pointAt(util.vector().topOf(socket));
        scene.idle(70);
    }

    // ---------------------------------------------------------------- helpers

    private static CreateSceneBuilder open(SceneBuilder builder, String id, String title) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title(id, title);
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(5);
        return scene;
    }

    private static ItemStack item(String path) {
        return new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("loconautics", path)));
    }

    /** Sets a block state property by name, so the scenes don't depend on Loconautics classes. */
    private static UnaryOperator<BlockState> set(String name, String value) {
        return state -> {
            Property<?> property = state.getBlock().getStateDefinition().getProperty(name);
            return property == null ? state : with(state, property, value);
        };
    }

    private static <T extends Comparable<T>> BlockState with(BlockState state, Property<T> property, String value) {
        return property.getValue(value).map(v -> state.setValue(property, v)).orElse(state);
    }
}
