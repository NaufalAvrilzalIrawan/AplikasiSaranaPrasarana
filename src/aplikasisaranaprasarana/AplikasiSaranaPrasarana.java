/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package aplikasisaranaprasarana;

/**
 *
 * @author user
 */
public class AplikasiSaranaPrasarana {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Panggil metode inisialisasi sebelum aplikasi berjalan
        DatabaseInitializer.inisialisasiTabel();
    
        LoginForm l = new LoginForm();
        l.setVisible(true);
    }
    
}
