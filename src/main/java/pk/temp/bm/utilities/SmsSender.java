package pk.temp.bm.utilities;

import jssc.SerialPort;
import jssc.SerialPortList;

public class SmsSender {

    public void SendSMS(String Port, String ServiceCenter, String PhoneNumber, String Msg) {
        ServiceCenter = "+92"+ServiceCenter;
        PhoneNumber = "+92"+PhoneNumber;
        String CSCA = "AT+CSCA= \""+ServiceCenter+"\"\r";
        String CMGS = "AT+CMGS= \""+PhoneNumber+"\"\r";
        String Text = ""+Msg+" \u001A";

        SerialPort serialPort = new SerialPort(Port);
        try {
            serialPort.openPort();
            serialPort.setParams(SerialPort.BAUDRATE_9600,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);
            serialPort.writeBytes("AT+CMGF=1\r".getBytes());
            Thread.sleep(30);
            serialPort.writeBytes(CSCA.getBytes());
            Thread.sleep(30);
            serialPort.writeBytes(CMGS.getBytes());
            Thread.sleep(30);
            serialPort.writeBytes(Text.getBytes());

            serialPort.closePort();
        }
        catch (Exception ex){
            //System.out.println(ex);
            //JOptionPane.showMessageDialog(null, "Port Not Found");
        }
    }
}
