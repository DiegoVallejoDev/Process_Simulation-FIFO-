package sample;

/**
 * Created by ckmu32 on 1/21/17.
 */

public class Proceso {
    String nombreProgramador, Operacion;
    int ID;
    int tiempoMaximo;
    int numeroLote;
    int numeroProceso;
    int tiempoDefinido;
    int tiempoRestante;
    int tiempoLlegada;
    int tiempoFin;
    int tiempoServicio;
    int tiempoProceso;
    int tiempoRetorno;
    int tiempoEspera;
    int tiempoRespuesta;
    int tiempoBloqueo;
    float OP1, OP2, Resultado;
    boolean comenzoEjecucion, llegoListos=false, respuestaCalculado=false;

    public Proceso(String Nombre, int ID, float OP1, String Operacion, float OP2, int Tiempo, int numLote, int numProceso, float Resultado, int tiempoDefinido, int tiempoRestante, int tiempoLlegada, int tiempoFin, int tiempoServicio, int tiempoRetorno, int tiempoProceso){//Constructor
        this.nombreProgramador=Nombre;
        this.ID=ID;
        this.OP1=OP1;
        this.Operacion=Operacion;
        this.OP2=OP2;
        this.tiempoMaximo=Tiempo;
        this.numeroLote=numLote;
        this.numeroProceso=numProceso;
        this.Resultado=Resultado;
        this.tiempoDefinido=tiempoDefinido;
        this.tiempoRestante=tiempoRestante;
        this.tiempoLlegada=tiempoLlegada;
        this.tiempoFin=tiempoFin;
        this.tiempoServicio=tiempoServicio;
        this.tiempoProceso=tiempoProceso;
        this.tiempoRetorno=tiempoRetorno;
        tiempoEspera=0;//Cambia al final
        comenzoEjecucion=false;
        tiempoRespuesta=0;//Cambia
        llegoListos=false;
        tiempoBloqueo=0;
        respuestaCalculado=false;
    }

    public String getNombreProgramador() {
        return nombreProgramador;
    }

    public void setNombreProgramador(String nombreProgramador) {
        this.nombreProgramador = nombreProgramador;
    }

    public String getOperacion() {
        return Operacion;
    }

    public void setOperacion(String operacion) {
        Operacion = operacion;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getTiempoMaximo() {
        return tiempoMaximo;
    }

    public void setTiempoMaximo(int tiempoMaximo) {
        this.tiempoMaximo = tiempoMaximo;
    }

    public float getOP1() {
        return OP1;
    }

    public void setOP1(float OP1) {
        this.OP1 = OP1;
    }

    public float getOP2() {
        return OP2;
    }

    public void setOP2(float OP2) {
        this.OP2 = OP2;
    }

    public int getTiempoDefinido() {
        return tiempoDefinido;
    }

    public void setTiempoDefinido(int tiempoDefinido) {
        this.tiempoDefinido = tiempoDefinido;
    }

    public int getTiempoRestante() {
        return tiempoRestante;
    }

    public void setTiempoRestante(int tiempoRestante) {
        this.tiempoRestante = tiempoRestante;
    }
}
