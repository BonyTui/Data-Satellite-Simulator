Week 1
I started by skimming over the specs. From my understanding, I need to model Device, which sits on a planet stationarily and can transfer/receive files from a Satellite, which orbits around the planet, by simulated using a BlackoutController. Then I look deeper into each of this class to see what fields/method they need. A satellite can either be of type Standard, Teleport, or Relay. Since TeleportingSatellite and RelaySatellite contains everything a StandardSatellite have, I let TeleportingSatellite and RelaySatellite inherit from StandardSatelltie, however they are unique in their abilities of teleporting and relaying, so they have their own methods for doing so. Whereas for Device, there're HandheldDevice, LaptopDevice, DesktopDevice which are basically the same thing just with different value for the maxRange field, so I didn't bother making subclasses for them, and instead have a Device class to cover all. To help with file transferring from device to satellites or vice versa, I made a FileTransfer class with methods to allow this, which Satelite and Device can just use.

Week 2

Week 3
