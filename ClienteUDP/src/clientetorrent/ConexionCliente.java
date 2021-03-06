/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientetorrent;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lenovo
 */
public class ConexionCliente {
    private DatagramSocket socket;
    private DatagramPacket sendPacked;
    private ArrayList<DatagramPacket> paquetes;
    private String direccionServidor;
    private int puertoServidor;
    private DataInputStream bufferDeEntrada = null;
    private DataOutputStream bufferDeSalida = null;
    private final int TAMANIO_PACKET = 11;
    private String numeroArchivo;
    
    public ConexionCliente(String direccionServidor, int puertoServidor) {
        try {
            socket = new DatagramSocket();
            this.paquetes = new ArrayList();
            this.puertoServidor = puertoServidor;
            this.sendPacked = new DatagramPacket(
                    new byte[this.TAMANIO_PACKET], 
                    this.TAMANIO_PACKET,
                    InetAddress.getByName("localhost"),
                    puertoServidor);
            
        } catch (IOException ex) {
            Logger.getLogger(ConexionCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void levantarConexion() {
        try {
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
            this.sendPacked = new DatagramPacket(
                    new byte[this.TAMANIO_PACKET], 
                    this.TAMANIO_PACKET,
                    InetAddress.getByName("localhost"),
                    puertoServidor);
            this.sendPacked.setData(datos); 
            this.numeroArchivo = ((char)sendPacked.getData()[0]) + "";
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
    
    public void ejecutarConexion() {
        Thread hilo = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    try {
                        recibirDatos();
                    } catch (IOException ex) {
                        Logger.getLogger(ConexionCliente.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } finally {
                  
                }
            }
        });
        hilo.start();
    }
    
    public void recibirDatos() throws IOException {
        while(true){
            DatagramPacket paqueteRecibido = new DatagramPacket(new byte[TAMANIO_PACKET], TAMANIO_PACKET);
            this.socket.receive(paqueteRecibido);
            this.paquetes.add(paqueteRecibido);
            byte[] datos = paqueteRecibido.getData();
System.out.println("Mensaje" + new String(datos));
            if(datos[datos.length-1] == 00){
System.out.println("Final del archivo");
                this.guardarArchivo();
            }
        }
    }
    
    public void guardarArchivo(){
        byte[] datosArchivo = new byte[this.TAMANIO_PACKET*this.paquetes.size()];
System.out.println("Tamaño datos" + datosArchivo.length);
        int contador = 0;
        for(int f = 0; f < this.paquetes.size(); f++){
            for(int c = 0; c < this.paquetes.get(f).getData().length; c++){
                if(this.paquetes.get(f).getData()[c] != 00){
                    datosArchivo[contador] = this.paquetes.get(f).getData()[c];
                }
                contador++;
            }
        } 
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(new File("descargas\\" + this.determinarNombre(numeroArchivo)));
            out.write(datosArchivo);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        } 
        this.paquetes = new ArrayList();
    }
    
    private String determinarNombre(String numero){
        switch(numero){
            case "0":
                return "Shrek.txt";
            case "1":
                return "Bee Movie.txt";
            case "2":
                return "Sausage Party.txt";
            case "3":
                return "Avenger Endgame.txt";
            case "4":
                return "Wall-E.txt";
            case "5":
                return "ejemplo.txt";
            default:
                return null;
        }
    }
}
