/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientetorrent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lenovo
 */
public class ConexionCliente {
    private DatagramSocket socket;
    private DatagramPacket sendPacked;
    private String direccionServidor;
    private int puertoServidor;
    private DataInputStream bufferDeEntrada = null;
    private DataOutputStream bufferDeSalida = null;
    private final int TAMANIO_PACKET = 10;
    
    public ConexionCliente(String direccionServidor, int puertoServidor) {
        try {
            socket = new DatagramSocket();
            this.sendPacked = new DatagramPacket(
                    new byte[this.TAMANIO_PACKET], 
                    this.TAMANIO_PACKET,
                    InetAddress.getByName("localhost"),
                    puertoServidor);
            
        } catch (IOException ex) {
            Logger.getLogger(ConexionCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void levantarConexion(String nombre) {
        try {
            this.enviar(nombre.getBytes());
            System.out.println("Conectado a :");
        } catch (Exception e) {
            System.out.println("Excepción al levantar conexión: " + e.getMessage());
            System.exit(0);
        }
    }
    
//    public void abrirFlujos() {
//        try {
//            //bufferDeEntrada = new DataInputStream(socket.getInputStream());
//            //bufferDeSalida = new DataOutputStream(socket.getOutputStream());
//            //bufferDeSalida.flush();
//        } catch (IOException e) {
//            System.out.println("Error en la apertura de flujos");
//        }
//    }

    public void enviar(byte[] datos) {
        try {
            this.sendPacked.setData(datos); 
            this.sendPacked.
            this.socket.send(this.sendPacked);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void cerrarConexion() {
        try {
            bufferDeEntrada.close();
            bufferDeSalida.close();
            socket.close();
            System.out.println("Conexión terminada");
        } catch (IOException e) {
            System.out.println("IOException on cerrarConexion()");
        }
    }
//    
//    public void ejecutarConexion() {
//        Thread hilo = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    recibirDatos();
//                } finally {
//                  
//                }
//            }
//        });
//        hilo.start();
//    }
    
//    public void recibirDatos() {
//        String st = "";
//        try {
//            do {
//                //st = (String) bufferDeEntrada.readUTF();
//                System.out.println("\n[Servidor] => " + st);
//            } while (true);
//        } catch (IOException e) {
//        }
//    }
    
}
