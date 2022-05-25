import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HappyEyeballs {

    static final Comparator<InetAddress> BY_ADDRESS =
        Comparator.comparing(InetAddress::getHostAddress);

    static final Comparator<InetAddress> BY_VERSION =
        Comparator
            .comparing(
                address -> (address instanceof Inet4Address) ? "v4" : "v6",
                Comparator.reverseOrder()
            );
    
    static final Comparator<InetAddress> ADDRESS_COMPARATOR = 
        BY_VERSION.thenComparing(BY_ADDRESS);

    public static void main(String[] args) throws Exception {
        connect(args[0], Integer.valueOf(args[1]));
    }

    private static Socket connect(String hostname, int port) throws UnknownHostException, InterruptedException {
        var addresses = getAddresses(hostname);
        return null;
    }

    static Collection<InetAddress> getAddresses(String hostname ) throws UnknownHostException {
        var addresses = InetAddress.getAllByName(hostname);
        var addressesString = Stream.of(addresses)
            .sorted(ADDRESS_COMPARATOR)
            .map(address -> {
                var version = (address instanceof Inet4Address) ? "v4" : "v6";
                return String.format("[%s] %s", version, address);
            })
            .collect(Collectors.joining("\n\t", "\t", ""));
        System.out.println(hostname + " resolved to:\n" + addressesString);
		return Stream.of(addresses)
            .sorted(ADDRESS_COMPARATOR)
            .collect(Collectors.toList());
    }

    /**
     * Subtask representing initiating socket connection with one of the addressess the provided
     * domain name resolves to.
     */
    static class SocketTask implements Callable<Socket> {
        private final InetAddress address;
        private final int port;
        private final AtomicReference<Socket> mutex;

        public SocketTask(InetAddress address, int port, AtomicReference<Socket> mutex) {
            this.address = address;
            this.port = port;
            this.mutex = mutex;
        }

        @Override
        public Socket call() throws Exception {
            System.out.println("Connecting to " + address + ":" + port);
            Thread.sleep(3000);
            // Thread.sleep(address instanceof Inet4Address ? 300 : 100);
            if (address.toString().contains(".66.")) {
                throw new RuntimeException("Unable to connect to " + address + ":" + port);
            }
            var socket = new Socket(address, port);
            if (mutex.compareAndSet(null, socket)) {
                System.out.println("Successfully connected to: " + address + ":" + port);
                return socket;
            } else {
                System.out.println("Other task beat us to it!");
                socket.close();
                throw new RuntimeException("Other task beat us to it!");
            }
        }
    }

}