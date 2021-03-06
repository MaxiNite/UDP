/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servidor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
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
    private final int TAMANIO_PACKET = 11;
    private int CONEXIONES_MAX = 100;
    private ExecutorService service;
    private int coreCount;
    private ArrayList<File> archivos;
    
    
    public ServidorTorrent(int port){
        this.archivos = new ArrayList();
        this.archivos.add(new File("archivos\\Shrek.txt"));
        this.archivos.add(new File("archivos\\Bee Movie.txt"));
        this.archivos.add(new File("archivos\\Sausage Party.txt"));
        this.archivos.add(new File("archivos\\Avengers Endgame.txt"));
        this.archivos.add(new File("archivos\\Wall-E.txt"));
        this.archivos.add(new File("archivos\\ejemplo.txt"));
        System.out.println(this.archivos.get(0).getName());
        try {
            this.socketServer = new DatagramSocket(port);
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
       
        while(contConexiones <= this.CONEXIONES_MAX ){
            
                //this.conexiones.add(conexion);
            service.execute(new AceptarSolicitud());
            contConexiones++;
           
        }
    }
 
    public class AceptarSolicitud implements Runnable{
        private DatagramPacket packet;
        private int contadorPaquetes = 0;
        
        @Override
        public void run() {
            this.packet = new DatagramPacket(new byte[TAMANIO_PACKET], TAMANIO_PACKET);
            try {
                System.out.println("entra");
                socketServer.receive(packet);
                
                System.out.println("El cliente "  + packet.getPort() + " se ha conectado.");
                
                System.out.println("Contenido paquete: " + new String(packet.getData()));
                String solicitud = ((char)packet.getData()[0]) + "";
                this.procesarSolicitud(solicitud);
                
            } catch (IOException ex) {
                Logger.getLogger(ServidorTorrent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        private void enviar(byte[] datos) throws IOException{
            this.packet.setData(datos);
            this.packet.setLength(TAMANIO_PACKET);
            socketServer.send(this.packet);
        }
        
        private void procesarSolicitud(String solicitud) throws IOException{
            File archivoSeleccionado = this.determinarArchivo(solicitud);
            byte[][] paquetes = this.enpaquetarArchivo(archivoSeleccionado);
            for(byte[] d : paquetes){
                this.enviar(d);
            } 
        }
        
        private File determinarArchivo(String nombre){
            return archivos.get(Integer.parseInt(nombre));
        }
        
        private byte[][] enpaquetarArchivo(File archivo) throws IOException{
            System.out.println("Nombre del archivito " + archivo.getName());
            FileInputStream in = new FileInputStream(archivo);
            byte[] datos = new byte[in.available()];
            in.read(datos);
            in.close();
            int cantPaquetes = (int)Math.ceil((double)(datos.length)/TAMANIO_PACKET);
            byte[][] paquetes = new byte[cantPaquetes][TAMANIO_PACKET];
            
            int contadorBytes = 0;
            for(int f = 0; f < cantPaquetes; f++){
                for(int c = 0; c < TAMANIO_PACKET; c++){
                    if(contadorBytes < datos.length){
                        paquetes[f][c] = datos[contadorBytes];
                    }else{
                        paquetes[f][c] = 00;
                    }
                    contadorBytes++;
                }
            }
            return paquetes;
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
