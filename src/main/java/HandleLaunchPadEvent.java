import com.illposed.osc.OSCMessage;
import com.illposed.osc.OSCSerializeException;
import com.illposed.osc.transport.OSCPortOut;
import events.LaunchControlEvent;
import interactable.UserControl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

public class HandleLaunchPadEvent implements LaunchControlEvent {

    LaunchControlXLDevice device;

    boolean isSoloModeEnabled = false;

    public HandleLaunchPadEvent(LaunchControlXLDevice device) {
        this.device = device;
    }

    @Override
    public void physicalStateChanged(UserControl keyCode, int i) {

        OSCPortOut oscPortOut = null;
        try {
            oscPortOut = new OSCPortOut(new InetSocketAddress("10.1.1.1", 10024));
        } catch (IOException e) {
            System.out.println("OSC Port error:" + e.getLocalizedMessage() + e.getCause() + e.getStackTrace());
        }

        try {

            assert oscPortOut != null;

            if (keyCode.equals(UserControl.FADER7)) {
                List<Object> args = new ArrayList<>();
                args.add(convertToMidas(i));


                oscPortOut.send(new OSCMessage("/rtn/aux/mix/fader", args));
                //mr18.setFader(fader, level);
                device.sendFactoryCommand(8, FactoryCommand.FULL_BRIGHTNESS);
            }


        } catch (IOException | OSCSerializeException e) {
            throw new RuntimeException(e);
        }

        if (keyCode.equals(UserControl.SOLO) && i == 127) {
            //BLINK ALL BOTTOM LEDS
            System.out.println("SOLO");
            isSoloModeEnabled = !isSoloModeEnabled;

            if (isSoloModeEnabled) {
                device.turnLedOn(8, UserControl.SOLO, LedCommand.BLINK_GREEN);

                blinkAllBottomLeds();
            } else {
                device.turnLedOff(8, UserControl.SOLO);
                turnOffAllBottomLeds();
            }

        }
    }

    private void blinkAllBottomLeds() {
        device.turnLedOn(8, UserControl.BUTTON1, LedCommand.BLINK_YELLOW);
        device.turnLedOn(8, UserControl.BUTTON2, LedCommand.BLINK_YELLOW);
        device.turnLedOn(8, UserControl.BUTTON3, LedCommand.BLINK_YELLOW);
        device.turnLedOn(8, UserControl.BUTTON4, LedCommand.BLINK_YELLOW);
        device.turnLedOn(8, UserControl.BUTTON5, LedCommand.BLINK_YELLOW);
        device.turnLedOn(8, UserControl.BUTTON6, LedCommand.BLINK_YELLOW);
        device.turnLedOn(8, UserControl.BUTTON7, LedCommand.BLINK_YELLOW);
        device.turnLedOn(8, UserControl.BUTTON8, LedCommand.BLINK_YELLOW);
    }

    private void turnOffAllBottomLeds() {
        device.turnLedOff(8, UserControl.BUTTON1);
        device.turnLedOff(8, UserControl.BUTTON2);
        device.turnLedOff(8, UserControl.BUTTON3);
        device.turnLedOff(8, UserControl.BUTTON4);
        device.turnLedOff(8, UserControl.BUTTON5);
        device.turnLedOff(8, UserControl.BUTTON6);
        device.turnLedOff(8, UserControl.BUTTON7);
        device.turnLedOff(8, UserControl.BUTTON8);
    }

    private int convertToMidas(double midiData) {
        double p = midiData/127;
        double midiPercent = p * 100;
        //System.out.println("Midi percent: " + midiPercent);
        //System.out.println((midiPercent/100) * 1024);
        //System.out.println("Int data: " + (int) ((midiPercent/100) * 1024));
        return (int) ((midiPercent/100) * 1024);
    }
}
