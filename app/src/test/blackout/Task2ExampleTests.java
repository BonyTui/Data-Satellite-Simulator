package blackout;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.blackout.BlackoutController;
import unsw.blackout.FileTransferException;
import unsw.response.models.FileInfoResponse;
import unsw.response.models.EntityInfoResponse;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import java.util.Arrays;

import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

@TestInstance(value = Lifecycle.PER_CLASS)
public class Task2ExampleTests {
    private BlackoutController controller;

    @BeforeEach
    public void setUp() {
        // Initialize before each test
        controller = new BlackoutController();

    }

    @AfterEach
    public void tearDown() {
        // Tear down logic after each test
        controller = null; // Reset the controller
    }

    @Test
    public void testSomeExceptionsForSend() {
        // just some of them... you'll have to test the rest

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 5000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));

        String msg = "Hey";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        assertThrows(FileTransferException.VirtualFileNotFoundException.class,
                () -> controller.sendFile("NonExistentFile", "DeviceC", "Satellite1"));

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
        controller.simulate(msg.length() * 2);
        assertThrows(FileTransferException.VirtualFileAlreadyExistsException.class,
                () -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
    }

    @Test
    public void testEntitiesInRange() {
        // Task 2
        // Example from the specification

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createSatellite("Satellite2", "StandardSatellite", 1000 + RADIUS_OF_JUPITER, Angle.fromDegrees(315));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));
        controller.createDevice("DeviceD", "HandheldDevice", Angle.fromDegrees(180));
        controller.createSatellite("Satellite3", "StandardSatellite", 2000 + RADIUS_OF_JUPITER, Angle.fromDegrees(175));

        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceC", "Satellite2"),
                controller.communicableEntitiesInRange("Satellite1"));
        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceB", "DeviceC", "Satellite1"),
                controller.communicableEntitiesInRange("Satellite2"));
        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2"), controller.communicableEntitiesInRange("DeviceB"));

        assertListAreEqualIgnoringOrder(Arrays.asList("DeviceD"), controller.communicableEntitiesInRange("Satellite3"));
    }

    @Test
    public void testMovement() {
        // Task 2
        // Example from the specification

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(340));
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(340), 100 + RADIUS_OF_JUPITER,
                "StandardSatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(337.95), 100 + RADIUS_OF_JUPITER,
                "StandardSatellite"), controller.getInfo("Satellite1"));
    }

    @Test
    public void testExample() {
        // Task 2
        // Example from the specification

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER,
                Angle.fromDegrees(320));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));

        String msg = "Hey";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        controller.simulate(msg.length() * 2);
        assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "Satellite1", "DeviceB"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
                controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

        controller.simulate(msg.length());
        assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true),
                controller.getInfo("DeviceB").getFiles().get("FileAlpha"));
    }

    @Test
    public void testRelayMovement() {
        // Task 2
        // Example from the specification

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "RelaySatellite", 100 + RADIUS_OF_JUPITER, Angle.fromDegrees(180));

        // moves in negative direction
        assertEquals(
                new EntityInfoResponse("Satellite1", Angle.fromDegrees(180), 100 + RADIUS_OF_JUPITER, "RelaySatellite"),
                controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(178.77), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(177.54), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(176.31), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));

        controller.simulate(5);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(170.18), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate(24);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(140.72), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));
        // edge case
        controller.simulate();
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(139.49), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));
        // coming back
        controller.simulate(1);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(140.72), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));
        controller.simulate(5);
        assertEquals(new EntityInfoResponse("Satellite1", Angle.fromDegrees(146.85), 100 + RADIUS_OF_JUPITER,
                "RelaySatellite"), controller.getInfo("Satellite1"));
    }

    @Test
    public void testTeleportingMovement() {
        // Test for expected teleportation movement behaviour

        controller.createSatellite("Satellite1", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER,
                Angle.fromDegrees(0));

        controller.simulate();
        Angle clockwiseOnFirstMovement = controller.getInfo("Satellite1").getPosition();
        controller.simulate();
        Angle clockwiseOnSecondMovement = controller.getInfo("Satellite1").getPosition();
        assertTrue(clockwiseOnSecondMovement.compareTo(clockwiseOnFirstMovement) == 1);

        // It should take 250 simulations to reach theta = 180.
        // Simulate until Satellite1 reaches theta=180
        controller.simulate(250);

        // Verify that Satellite1 is now at theta=0
        assertTrue(controller.getInfo("Satellite1").getPosition().toDegrees() % 360 == 0);
    }

    // My tests: Communicable
    @Test
    public void unsupportedTypeCommunication() {
        controller.createSatellite("StandardSatellite", "StandardSatellite", 10000 + RADIUS_OF_JUPITER,
                Angle.fromDegrees(90));

        controller.createDevice("DesktopDevice", "DesktopDevice", Angle.fromDegrees(90));
        controller.createDevice("HandheldDevice", "HandheldDevice", Angle.fromDegrees(100));
        controller.createDevice("LaptopDevice", "LaptopDevice", Angle.fromDegrees(80));

        assertListAreEqualIgnoringOrder(Arrays.asList("HandheldDevice", "LaptopDevice"),
                controller.communicableEntitiesInRange("StandardSatellite"));
        assertListAreEqualIgnoringOrder(Arrays.asList(), controller.communicableEntitiesInRange("DesktopDevice"));
    }

    // My tests: File Transfers
    @Test
    public void testIncompleteTransfer() {
        // Test for expected teleportation movement behaviour

        controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(0));

        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(0));

        String msg = "123456789";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        controller.simulate();

        assertEquals(new FileInfoResponse("FileAlpha", "1", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        controller.simulate(5);

        assertEquals(new FileInfoResponse("FileAlpha", "123456", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        controller.simulate(20);

        assertEquals(new FileInfoResponse("FileAlpha", "123456789", msg.length(), true),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
    }

    @Test
    public void testTeleportTransferDeviceToSatellite() {
        // Test for expected teleportation movement behaviour

        controller.createSatellite("Satellite1", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER,
                Angle.fromDegrees(179));

        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(180));

        String msg = "ttttttttttttttotttttttttttattttttttttttbttttttt";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        controller.simulate();

        assertEquals(new FileInfoResponse("FileAlpha", "tttttttttttttto", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        controller.simulate();

        // Satellite1 Teleports

        assertEquals(new FileInfoResponse("FileAlpha", "oab", 3, true),
                controller.getInfo("DeviceC").getFiles().get("FileAlpha"));

        assertEquals(null, controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
    }

    @Test
    public void testTeleportTransferSatelliteToDevice() {
        // Test for expected teleportation movement behaviour

        // 3 mins from teleporting
        controller.createSatellite("Satellite1", "TeleportingSatellite", 10000 + RADIUS_OF_JUPITER,
                Angle.fromDegrees(178));

        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(180));
        controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(181));

        String msg = "tatottttttttbtt";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);
        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        controller.simulate();

        assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        // Teleporting  Satellite send file to device
        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "Satellite1", "DeviceB"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
                controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

        controller.simulate();

        assertEquals(new FileInfoResponse("FileAlpha", "tatotttttt", msg.length(), false),
                controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

        controller.simulate();

        // Satellite1 Teleports

        assertEquals(new FileInfoResponse("FileAlpha", "tatottttttb", 11, true),
                controller.getInfo("DeviceB").getFiles().get("FileAlpha"));

    }

    @Test
    public void relayTransfer() {
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(90));
        controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER,
                Angle.fromDegrees(160));

        assertListAreEqualIgnoringOrder(Arrays.asList(), controller.communicableEntitiesInRange("DeviceA"));

        controller.createSatellite("Satellite2", "RelaySatellite", 15000 + RADIUS_OF_JUPITER, Angle.fromDegrees(120));

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite1", "Satellite2"),
                controller.communicableEntitiesInRange("DeviceA"));

    }

    @Test
    public void relayBypassSupportedDevices() {

        controller.createDevice("DeviceA", "DesktopDevice", Angle.fromDegrees(90));
        controller.createSatellite("Satellite1", "StandardSatellite", 10000 + RADIUS_OF_JUPITER,
                Angle.fromDegrees(160));

        assertListAreEqualIgnoringOrder(Arrays.asList(), controller.communicableEntitiesInRange("DeviceA"));

        controller.createSatellite("Satellite2", "RelaySatellite", 15000 + RADIUS_OF_JUPITER, Angle.fromDegrees(120));

        assertListAreEqualIgnoringOrder(Arrays.asList("Satellite2"), controller.communicableEntitiesInRange("DeviceA"));

    }

    @Test
    public void multipleRelayCommunication() {
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(90));
        controller.createSatellite("goal", "StandardSatellite", 10000 + RADIUS_OF_JUPITER, Angle.fromDegrees(270));

        assertListAreEqualIgnoringOrder(Arrays.asList(), controller.communicableEntitiesInRange("DeviceA"));

        controller.createSatellite("relay1", "RelaySatellite", 15000 + RADIUS_OF_JUPITER, Angle.fromDegrees(120));

        assertListAreEqualIgnoringOrder(Arrays.asList("relay1"), controller.communicableEntitiesInRange("DeviceA"));

        controller.createSatellite("relay2", "RelaySatellite", 15000 + RADIUS_OF_JUPITER, Angle.fromDegrees(180));

        assertListAreEqualIgnoringOrder(Arrays.asList("relay1", "relay2"),
                controller.communicableEntitiesInRange("DeviceA"));

        controller.createSatellite("relay3", "RelaySatellite", 15000 + RADIUS_OF_JUPITER, Angle.fromDegrees(225));

        assertListAreEqualIgnoringOrder(Arrays.asList("relay1", "relay2", "relay3", "goal"),
                controller.communicableEntitiesInRange("DeviceA"));

    }

    @Test
    public void maxFileReached() {
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(90));
        controller.createSatellite("Satellite1", "StandardSatellite", 20000 + RADIUS_OF_JUPITER, Angle.fromDegrees(90));

        String msg = "E";
        controller.addFileToDevice("DeviceA", "FileAlpha1", msg);
        controller.addFileToDevice("DeviceA", "FileAlpha2", msg);
        controller.addFileToDevice("DeviceA", "FileAlpha3", msg);
        controller.addFileToDevice("DeviceA", "FileAlpha4", msg);

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha1", "DeviceA", "Satellite1"));
        assertEquals(new FileInfoResponse("FileAlpha1", "", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha1"));

        controller.simulate();

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha2", "DeviceA", "Satellite1"));
        assertEquals(new FileInfoResponse("FileAlpha2", "", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha2"));

        controller.simulate();

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha3", "DeviceA", "Satellite1"));
        assertEquals(new FileInfoResponse("FileAlpha3", "", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha3"));

        controller.simulate();

        assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
                () -> controller.sendFile("FileAlpha4", "DeviceA", "Satellite1"));

    }

    @Test
    public void maxByteReached() {
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(90));
        controller.createSatellite("Satellite1", "StandardSatellite", 20000 + RADIUS_OF_JUPITER, Angle.fromDegrees(90));

        String msg = "oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo";
        controller.addFileToDevice("DeviceA", "80bytes", msg);
        controller.addFileToDevice("DeviceA", "1byte", "e");

        assertDoesNotThrow(() -> controller.sendFile("80bytes", "DeviceA", "Satellite1"));
        assertEquals(new FileInfoResponse("80bytes", "", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("80bytes"));

        assertThrows(FileTransferException.VirtualFileNoStorageSpaceException.class,
                () -> controller.sendFile("1byte", "DeviceA", "Satellite1"));

    }

    @Test
    public void bandwidthLimit() {
        controller.createDevice("DeviceA", "HandheldDevice", Angle.fromDegrees(90));
        controller.createDevice("DeviceB", "HandheldDevice", Angle.fromDegrees(91));
        controller.createSatellite("Satellite1", "StandardSatellite", 20000 + RADIUS_OF_JUPITER, Angle.fromDegrees(90));

        String msg = "123";
        controller.addFileToDevice("DeviceA", "file1", msg);
        controller.addFileToDevice("DeviceB", "file2", "e");

        assertDoesNotThrow(() -> controller.sendFile("file1", "DeviceA", "Satellite1"));
        assertThrows(FileTransferException.VirtualFileNoBandwidthException.class,
                () -> controller.sendFile("file2", "DeviceB", "Satellite1"));

    }
}
