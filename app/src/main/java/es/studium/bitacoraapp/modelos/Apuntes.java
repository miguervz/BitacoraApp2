package es.studium.bitacoraapp.modelos;

public class Apuntes
{
    private int idApunte;
    private String fecha;
    private String texto;
    private  int idCuadernoFK;
    private int imagen;

    public Apuntes(int idApunte, String fecha, String texto, int imagen, int idCuadernoFK){
        this.idApunte = idApunte;
        this.fecha = fecha;
        this.texto = texto;
        this.imagen = imagen;
        this.idCuadernoFK = idCuadernoFK;
    }



    public int getIdApunte(){return idApunte;}
    public String getFecha(){return fecha;}
    public String getTexto(){return texto;}
    public int getImagen() {return imagen;}
    public int getIdCuadernoFK(){return idCuadernoFK;}
}
