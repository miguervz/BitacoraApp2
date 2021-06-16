package es.studium.bitacoraapp.modelos;

public class Cuadernos
{
    private int idCuaderno;
    private String cuaderno;
    private int imagen;

    public Cuadernos(int idCuaderno, String cuaderno, int imagen){
        this.idCuaderno = idCuaderno;
        this.cuaderno = cuaderno;
        this.imagen = imagen;
    }

    public int getIdCuaderno(){return idCuaderno;}
    public String getCuaderno(){return cuaderno;}
    public int getImagen(){return imagen;}
}
