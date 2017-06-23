import java.io.*;
import java.net.*;
import java.util.List;
import com.google.protobuf.ByteString; 
import java.util.Date;
import java.text.SimpleDateFormat;

class server
{
   public static void main(String args[]) throws Exception
      {
      	System.out.print("Please enter the port number: ");
      	int port = Integer.parseInt(System.console().readLine());
        int sn = 0;
        boolean loss = false;
        String currentTimeStamp = "";
        DatagramSocket serverSocket = new DatagramSocket(port);
            byte[] receiveData = new byte[5120];
            System.out.println("UDP server listening at port "+port);

            while(true)
               {

                  try {
                     DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
                     serverSocket.receive(receivePacket);
                     byte[] sentence = receivePacket.getData();

                     ByteString byteString = ByteString.copyFrom(sentence,0,receivePacket.getLength());

                     // System.out.println("Received: "+receivePacket.getLength());
                     System.out.println("Received: "+byteString);
                     loss = false;
                     
                     EnvironmentMonitoringProtos.EnvironmentUpdate sensorData = EnvironmentMonitoringProtos.EnvironmentUpdate.parseFrom(byteString);
                     
                     // System.out.println("SerializedSize: " + sensorData.getSerializedSize());
                     // System.out.println("Total Sensor record recevied: " + sensorData.getSensorRecordCount());
                     for (EnvironmentMonitoringProtos.EnvironmentUpdate.SensorRecord data: sensorData.getSensorRecordList()) {
                        List<EnvironmentMonitoringProtos.EnvironmentUpdate.SensorData> list = data.getSensorDataList();
                        // print some record;
                        // System.out.println("Timestamp: " + data.getTimestamp());
                        // System.out.println("SN: " + data.getSequenceNumber());
                        if (data.getSequenceNumber() != sn + 1)
                        {
                           loss = true;
                        }
                        sn = data.getSequenceNumber();
                        System.out.println(data.getSequenceNumber());
                     }
                     currentTimeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                        try(FileWriter fw = new FileWriter("UDPlog.txt", true);
                         BufferedWriter bw = new BufferedWriter(fw);
                         PrintWriter out = new PrintWriter(bw))
                        {
                           if (loss == true)
                           {
                              // out.println(currentTimeStamp+","+"0"+","+"0"+","+"");
                           }
                           else
                           {
                              out.println(currentTimeStamp+","+sensorData.getSerializedSize()+","+sensorData.getSensorRecordCount()+","+sn);
                           }

                        } catch (IOException e) {
                         //exception handling left as an exercise for the reader
                        }
                  }
                  catch (IOException e) {
                     e.printStackTrace();
                  } 
                     




                  // InetAddress IPAddress = receivePacket.getAddress();
                  // int port = receivePacket.getPort();
                  // String capitalizedSentence = sentence.toUpperCase();
                  // sendData = capitalizedSentence.getBytes();
                  // DatagramPacket sendPacket =
                  // new DatagramPacket(sendData, sendData.length, IPAddress, port);
                  // serverSocket.send(sendPacket);
               }
      }
}