/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package udp;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Lenovo
 */
public class Conexion implements Runnable{
    private String nombre;
    private Socket socket;
    private DataInputStream bufferDeEntrada = null;
    private DataOutputStream bufferDeSalida = null;
    
    public Conexion(Socket socket){
        this.socket = socket;
        this.establecerConexion();
    }
    
    public void establecerConexion(){
        this.flujos();
        this.recibirDatos();
    }
    
    public void flujos() {
        try {
            bufferDeEntrada = new DataInputStream(socket.getInputStream());
            bufferDeSalida = new DataOutputStream(socket.getOutputStream());
            bufferDeSalida.flush();
        } catch (IOException e) {
            System.out.println("Error en la apertura de flujos");
        }
    }
    
    public void recibirDatos() {
        String st = "";
        try {
            if(this.nombre == null){
                this.nombre = (String) bufferDeEntrada.readUTF();
            }else{
                System.out.println("Leyendo...");
                st = (String) bufferDeEntrada.readUTF(); 
                enviar(st);
                System.out.println("\n[Cliente] => " + st);
            }
        } catch (IOException e) {
            cerrarConexion();
        }
    }
    
    public void enviar(String s) {
        try {
            DataOutputStream salidaDestino = new DataOutputStream(this.socket.getOutputStream());
            System.out.println("Mensaje enviado: " + s);
            salidaDestino.writeUTF(s);
            salidaDestino.flush();
        } catch (IOException e) {
            System.out.println("Error en enviar(): " + e.getMessage());
        }
    }
    
    public void cerrarConexion() {
        try {
            bufferDeEntrada.close();
            bufferDeSalida.close();
            socket.close();
        } catch (IOException e) {
            System.out.print("Excepción en cerrarConexion(): " + e.getMessage());
        } finally {
            System.out.print("Conversación finalizada....");
            System.exit(0);

        }
    }
    
    public void ejecutarConexion() {
        try {
            while(true){
                recibirDatos();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        this.establecerConexion();
        this.ejecutarConexion();
    }

    public Socket getSocket(){
        return this.socket;
    }
    
    public String getNombre(){
        return this.nombre;
    }
}
