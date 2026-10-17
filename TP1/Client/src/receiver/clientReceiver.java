package receiver;

import common.protocol.PacketDecoder;

import java.io.DataInputStream;

public class clientReceiver {

    private PacketDecoder packetDecoder = new PacketDecoder();
    private DataInputStream clientInput;

    public clientReceiver(DataInputStream clientInput) {
        this.clientInput = clientInput;
    }

    public void startListenning() {
        //Thread receiver =  new Thread(() => {

        //});

        //receiver.start();

    }

}
