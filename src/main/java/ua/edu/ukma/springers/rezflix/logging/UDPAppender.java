package ua.edu.ukma.springers.rezflix.logging;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import lombok.Setter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

@Setter
public class UDPAppender extends AppenderBase<ILoggingEvent> {

    private final DatagramSocket socket;

    private Layout<ILoggingEvent> layout;
    private int port;
    private InetAddress host;

    public UDPAppender() throws SocketException {
        socket = new DatagramSocket();
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (layout == null) {
            addError("No layout set for UDPAppender");
            return;
        }
        byte[] buf = layout.doLayout(eventObject).getBytes();
        DatagramPacket packet = new DatagramPacket(buf, buf.length, host, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            addError("Failed to send log event via UDP", e);
        }
    }

    public void setHost(String host) throws IOException {
        this.host = InetAddress.getByName(host);
    }
}
