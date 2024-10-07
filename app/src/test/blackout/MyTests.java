package blackout;

import unsw.blackout.BlackoutController;
import unsw.response.models.FileInfoResponse;
import unsw.utils.Angle;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

import org.junit.jupiter.api.Test;

public class MyTests {
    @Test
    public void testElephantSatellite() {
        BlackoutController controller = new BlackoutController();
        // Task 2
        // Example from the specification

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "ElephantSatellite", 20000 + RADIUS_OF_JUPITER, Angle.fromDegrees(90));
        controller.createDevice("DeviceA", "LaptopDevice", Angle.fromDegrees(62));

        String msg = "oooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo";

        controller.addFileToDevice("DeviceA", "FileAlpha", msg);
        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceA", "Satellite1"));
        assertEquals(new FileInfoResponse("FileAlpha", "", msg.length(), false),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));

        // Goes outside of device range, shouldn't delete file
        controller.simulate(120);

        assertEquals(new FileInfoResponse("FileAlpha", msg, msg.length(), true),
                controller.getInfo("Satellite1").getFiles().get("FileAlpha"));
    }
}
