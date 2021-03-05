/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package udp;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lenovo
 */
public class ServidorTorrent {
    
    private DatagramSocket socketServer;
    private final int TAMANIO_PACKET = 10;
    private ArrayList<Conexion> conexiones;
    private int CONEXIONES_MAX = 10;
    private ExecutorService service;
    private int coreCount;
    
    
    public ServidorTorrent(int port){
        try {
            this.socketServer = new DatagramSocket(port);
            this.conexiones = new ArrayList();
        } catch (IOException ex) {
            Logger.getLogger(ServidorTorrent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void start(){
        System.out.println("Iniciando servidor...");
        //numero de threads posibles
        this.coreCount = Runtime.getRuntime().availableProcessors();
        
        //El programa solo utiliza la mitad de threads posibles
        this.service = Executors.newFixedThreadPool(coreCount/2);
        int contConexiones = 1;
        DatagramPacket packet = new DatagramPacket(new byte[this.TAMANIO_PACKET], this.TAMANIO_PACKET);
        while(true){
            try {
                System.out.println("entra");
                this.socketServer.receive(packet);
                System.out.println("El cliente "  + packet.getPort() + " se ha conectado.");
                //this.conexiones.add(conexion);
                //service.execute(conexion);
                System.out.println("Contenido paquete: " + new String(packet.getData()));
                
                this.socketServer.send(packet);
                packet.setLength(this.TAMANIO_PACKET);
            } catch (IOException ex) {
                Logger.getLogger(ServidorTorrent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
 

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ServidorTorrent servidor = new ServidorTorrent(4444);
        servidor.start();
    }
    
}
