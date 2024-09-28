package blackout;

import unsw.blackout.BlackoutController;
import unsw.blackout.FileTransferException;
import unsw.utils.Angle;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import unsw.response.models.EntityInfoResponse;
import unsw.response.models.FileInfoResponse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static blackout.TestHelpers.assertListAreEqualIgnoringOrder;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static unsw.utils.MathsHelper.RADIUS_OF_JUPITER;

public class MyTests {
    public static void main(String[] args) {
        BlackoutController controller = new BlackoutController();

        // Creates 1 satellite and 2 devices
        // Gets a device to send a file to a satellites and gets another device to download it.
        // StandardSatellites are slow and transfer 1 byte per minute.
        controller.createSatellite("Satellite1", "StandardSatellite", 5000 + RADIUS_OF_JUPITER, Angle.fromDegrees(320));
        controller.createDevice("DeviceB", "LaptopDevice", Angle.fromDegrees(310));
        controller.createDevice("DeviceC", "HandheldDevice", Angle.fromDegrees(320));

        String msg = "Hey";
        controller.addFileToDevice("DeviceC", "FileAlpha", msg);

        assertDoesNotThrow(() -> controller.sendFile("FileAlpha", "DeviceC", "Satellite1"));

        System.out.println(controller.getInfo("Satellite1"));
        System.out.println();

        controller.simulate(3);

        System.out.println(controller.getInfo("Satellite1"));
    }
}
