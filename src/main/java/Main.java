import com.illposed.osc.OSCMessage;
import com.illposed.osc.transport.OSCPortOut;
import events.LaunchControlEv;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Main {

    public static void main(String[] args) {
        LaunchControlXLDevice device = new LaunchControlXLDevice("Launch Control XL");

        device.connect();
        device.sendFactoryCommand(8, FactoryCommand.RESET);

        LaunchControlEv ev = new LaunchControlEv();
        HandleLaunchPadEvent handleLaunchPadEvent = new HandleLaunchPadEvent(device);
        ev.addEventListener(handleLaunchPadEvent);
    }
}
