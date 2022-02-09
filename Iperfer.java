import java.io.*;
import java.net.*;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class Iperfer {

    //chunk size that we are going to send back and forth
    

    public static void main(String[] args) throws IOException, NumberFormatException{
        //if it's a client side
        System.out.println("hi");
        if ((args.length == 7 && args[0].equals("-c"))){
            clientSide(args[2], Integer.parseInt(args[4]), Integer.parseInt(args[6]));
        }
        //if it's a server side
        else if (args.length == 3 && args[0].equals("-s")){
            serverSide(Integer.parseInt(args[2]));
        }
        //wrong number of arguments
        else{
            System.out.println("Error: missing or additional arguments");
            System.exit(0);
        }
    }

    public static void clientSide(String hostname, int portNum, int howLong) throws IOException{

        if (portNum < 1024 || portNum > 65535){
            System.out.println("Error: port number must be in the range 1024 to 65535");
            System.exit(0);
        }
            byte[] dataChunk = new byte[1000];
            Arrays.fill(dataChunk, (byte)0);
            Socket sock = new Socket(hostname, portNum);
            OutputStream out = sock.getOutputStream();
            Instant start = Instant.now();
            Instant end = Instant.now();
            Duration time = Duration.between(start, end);
            long howManyKBSent = 0;
            while (time.toSeconds() < howLong){
                end = Instant.now();
                time = Duration.between(start, end);
                out.write(dataChunk);
                howManyKBSent++;
            }
            out.write(-1);
            double howManyMBSent = howManyKBSent/1000;
            System.out.println("sent=" + howManyKBSent + " KB rate=" + howManyMBSent/howLong + " Mbps");
            out.close();
            sock.close();
    }
    
    public static void serverSide(int portNum) throws IOException{
        if (portNum < 1024 || portNum > 65535){
            System.out.println("Error: port number must be in the range 1024 to 65535");
            System.exit(0);
        }
        long numBytes = 0;
        ServerSocket serverSock = new ServerSocket(portNum);
        Socket sock = serverSock.accept();
        InputStream in = sock.getInputStream();
        Instant start = Instant.now(); 
        int num;
        while((num = in.read()) != -1){
            numBytes += num + 1;
        }
        Instant end = Instant.now();
        Duration timeLimit = Duration.between(start, end);
        long time = timeLimit.toSeconds();
        long numKB = numBytes/1000;
        double numMB = numKB/1000.0;
        System.out.println("sent=" + numKB + " KB rate=" + numMB/time + " Mbps");
        serverSock.close();
    }


}
