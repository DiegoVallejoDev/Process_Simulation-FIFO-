package sample;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Duration;
import jdk.nashorn.internal.objects.Global;

import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;


public class Controller implements Initializable{ ////Hacer clase proceso con los elementos de la ventana. Y que el lote
    //se asigne en base a un contdor de procesos registrados. Esta clase deberá llevar un constructor

    @FXML//Declaramos elementos visuales que usaremos en código
    TextField txtNombre, txtID, txtOP1, txtOP2, txtTiempo_Max, txtPendiente, txtGeneral, txtTT, txtTR, txtprocesosDeseados, txtComando, txtNuevos;
    @FXML
    ChoiceBox cbSimbolo;
    @FXML
    Button btnGuardar_Datos, btnConfirmar_Datos, btncrearProcesos;
    @FXML
    Label lbProceso_Total;
    @FXML
    TextArea txtLP_Nombre, txtLP_Tiempo, txtPE, txtTerminados, txtBloqueados;

    /*
    * Crea procesos y se guardan en la lista de procesos.
    * Comienza la ejecución de los 4 primeros. El ciclo ejecuta 4 procesos. Se puede mantener la información de lote para esto
    * Se presiona "I" se manda a lista de bloqueados. Cada proceso espera 8 segundo antes de volver a la lista
    */

    public static List<Integer> lista_ID = new ArrayList<>();//Guarda ID
    public static List<Object> listaProcesos = new ArrayList<>();//Guarda los procesos creados
    public static List<Object> listaListos = new ArrayList<>();//Guarda los procesos a ejecutar
    public static List<Object> listaBloqueados = new ArrayList<>();//Guarda los procesos bloqueados
    public static List<Object> listaEjecucion = new ArrayList<>();//Guarda el proceso en ejecución
    //Hashtable<Integer,Object> hashProcesos = new Hashtable<>();
    public static List<String> listaTerminados = new ArrayList<>();//Guarda los procesos que fueron terminados normales o por error
    boolean puedeGuardar=false, otroGuardar=false, errorProcesos=false, PAUSAR=false, ERROR=false, INTERRUMPIR=false;
    int datosRevisados = 0, numeroLote = 1, numeroProceso = 1, numeroProcesos_Totales = 0, contadorID=1;
    String nombresProgramador[]={"Kaleb","Charlotte","Jaime","Leonardo","Sam","Yura","Benito","Roy","Lisa","Keyes","Rick"};
    String Operaadores[]={"+","-","*","/","^","√","%"};
    int  global=0;

    Random random = new Random();

    public void initialize(URL location, ResourceBundle resources){//Inicializamos la selección de operación y otros elementos más
        cbSimbolo.setItems(FXCollections.observableArrayList("+","-","*","/","^","√","%"));
        cbSimbolo.setTooltip(new Tooltip("Selecciona el tipo de operación a realizar"));
        cbSimbolo.setValue("*");
        btnGuardar_Datos.setDisable(true);
        //lbLote.setText("Lote: "+numeroLote);//Nostramos número de lote
        //lbProceso.setText("Proceso: "+numeroProceso);//Mostramos número de proceso
        lbProceso_Total.setText("Procesos totales: "+numeroProcesos_Totales);//Mostramos número de procesos guardados
        //lbLote.setTooltip(new Tooltip("Indica el lote de este proceso"));
        //lbProceso.setTooltip(new Tooltip("Indica el número de proceso"));
        lbProceso_Total.setTooltip(new Tooltip("Indica el número de procesos guardados"));
        txtID.setText(Integer.toString(contadorID));
        txtID.setEditable(false);

        int valor=random.nextInt(12);//Random nombre programador
        if(valor>11)
            valor=2;
        if(valor<0)
            valor=0;
        int finalValor = valor;
        txtNombre.setText(nombresProgramador[finalValor]);
        txtNombre.setEditable(false);


        int OP1=random.nextInt(10)+1;//Random de operandos
        txtOP1.setText(Integer.toString(OP1));
        txtOP1.setEditable(false);
        int OP2=random.nextInt(10)+1;
        txtOP2.setText(Integer.toString(OP2));
        txtOP2.setEditable(false);

        int Tiempo=(int)(Math.random()*10+5);//Random de Tiempo
        txtTiempo_Max.setText(Integer.toString(Tiempo));
        txtTiempo_Max.setEditable(false);


        txtLP_Tiempo.scrollTopProperty().bindBidirectional(txtLP_Nombre.scrollTopProperty());
        txtLP_Nombre.scrollTopProperty().bindBidirectional(txtLP_Tiempo.scrollTopProperty());

        btnConfirmar_Datos.setDisable(true);
        ///pruebasLoteProceso();
    }

    public void desactivarGuardar(){//Una vez guardados los datos se desactiva para evitar trolleos
        btnGuardar_Datos.setDisable(true);
        txtNombre.clear();
        txtID.clear();
        txtOP1.clear();
        txtOP2.clear();
        txtTiempo_Max.clear();
        //lbLote.setText("Lote: "+numeroLote);
        //lbProceso.setText("Proceso: "+numeroProceso);
        numeroProcesos_Totales++;
        lbProceso_Total.setText("Procesos totales: "+numeroProcesos_Totales);
        contadorID++;
        txtID.setText(Integer.toString(contadorID));
        txtNombre.setFocusTraversable(true);

        int valor=random.nextInt(11);//Random nombre programador
        txtNombre.setText(nombresProgramador[valor]);

        int OP1=random.nextInt(10)+1;//Random de operandos
        txtOP1.setText(Integer.toString(OP1));
        txtOP1.setEditable(false);
        int OP2=random.nextInt(10)+1;
        txtOP2.setText(Integer.toString(OP2));
        txtOP2.setEditable(false);

        int Tiempo=(int)(Math.random()*10+5);//Random de Tiempo
        txtTiempo_Max.setText(Integer.toString(Tiempo));
        txtTiempo_Max.setEditable(false);

        int Operacion=random.nextInt(7);//Random Operacion
        cbSimbolo.setValue(Operaadores[Operacion]);


        //debugHashTable();//Pruebas de la hash
    }

    public void crearProcesos_Automatico(){//Obtenemos número de procesos, verificamos número y corremos for que crear y verifica cada proceso
        errorProcesos=false;
        String numeroProcesos=txtprocesosDeseados.getText();
        verifcarTotal(numeroProcesos);
        if(errorProcesos==false){
            for(int i = 0;i<Integer.parseInt(numeroProcesos);i++){
                obtenerDatos_Parte1();
                desactivarGuardar();
            }
        }
        else
            System.out.println("Algo mal pasó");

    }

    public void obtenerDatos_Parte1(){//Al presionar el botón de confirmar
        String ver_ID, ver_OP1, ver_OP2, ver_TM, operacionSeleccionada, nombreProgramador;
        float Resultado=0, Operador1=0, Operador2=0;

        datosRevisados=0;
        puedeGuardar=false;
        otroGuardar=false;

        ///Obtenemos el nombre del programador
        nombreProgramador = txtNombre.getText();

        ///VERIFICAR ID
        ver_ID = txtID.getText();//Que no guarde el ID hasta que todo esté correcto
        verifcarID(ver_ID);

        ///VERIFICAR OP1
        ver_OP1 = txtOP1.getText();
        verifcarOP(ver_OP1);
        System.out.println(ver_OP1);

        ///Obtenemos la operación a realizar
        operacionSeleccionada = cbSimbolo.getValue().toString();

        ///VERIFICAR OP2
        ver_OP2 = txtOP2.getText();
        verifcarOP(ver_OP2);
        System.out.println(ver_OP2);

        //VERIFICAR TM
        ver_TM = txtTiempo_Max.getText();
        verifcarTM(ver_TM);


        if(datosRevisados==4) {//Campos verificados y correctos
            if(numeroProceso>4){//4 procesos guardados
                System.out.println("Contador de proceso 4 llegó a 4. Reseteando a 1 y aumentando contador de lote");
                numeroProceso=1;
                numeroLote++;
                //lbProceso.setText("Proceso: "+numeroProceso);
            }



            //Calculamos resultado
            Operador1=Float.parseFloat(ver_OP1);//Casteamos el string a float
            Operador2=Float.parseFloat(ver_OP2);

            if(operacionSeleccionada.contains("+")) {
                Resultado = (Operador1 + Operador2);
                otroGuardar = true;
            }
            else if(operacionSeleccionada.contains("-")) {
                Resultado = (Operador1 - Operador2);
                otroGuardar = true;
            }
            else if(operacionSeleccionada.contains("*")) {
                Resultado = (Operador1 * Operador2);
                otroGuardar = true;
            }
            else if(operacionSeleccionada.contains("/")) {
                if(Operador1==0 && Operador2==0) {

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("División incorrecta");
                    alert.setContentText("Verifique y trate de nuevo");
                    alert.showAndWait();
                }
                else if(Operador1>0 && Operador2==0){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("División incorrecta");
                    alert.setContentText("Verifique y trate de nuevo");
                    alert.showAndWait();
                }
                else {
                    System.out.println("División correcta");
                    puedeGuardar=true;
                    Resultado = (Operador1 / Operador2);
                }
            }
            else if(operacionSeleccionada.contains("%")) {


                if(Operador1==0 && Operador2==0) {
                    Resultado = (Operador1 / Operador2);
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Módulo incorrecto");
                    alert.setContentText("Verifique y trate de nuevo");
                    alert.showAndWait();
                }
                else if(Operador1>0 && Operador2==0){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Módulo incorrecto");
                    alert.setContentText("Verifique y trate de nuevo");
                    alert.showAndWait();
                }
                else {
                    System.out.println("Módulo correcto");
                    puedeGuardar=true;
                    Resultado = (Operador1 % Operador2);
                }
            }
            else if(operacionSeleccionada.contains("^")) {
                Resultado = (float) Math.pow(Operador1, Operador2);
                otroGuardar = true;
            }
            else if(operacionSeleccionada.contains("√")) {
                Resultado = (float)Math.sqrt(Operador2);
                otroGuardar=true;
            }
            else
                System.out.println("MEH");

            //Guardamos
            if(puedeGuardar==true || otroGuardar==true) {
                //hashProcesos.put(Integer.parseInt(ver_ID), new Proceso(nombreProgramador, Integer.parseInt(ver_ID), Float.parseFloat(ver_OP1), operacionSeleccionada, Float.parseFloat(ver_OP2), Integer.parseInt(ver_TM), numeroLote, numeroProceso, Resultado, Integer.parseInt(ver_TM), 0));
                listaProcesos.add(new Proceso(nombreProgramador, Integer.parseInt(ver_ID), Float.parseFloat(ver_OP1), operacionSeleccionada, Float.parseFloat(ver_OP2), Integer.parseInt(ver_TM), numeroLote, numeroProceso, Resultado, Integer.parseInt(ver_TM), 0,0, 0, 0, 0, Integer.parseInt(ver_TM)));

                //System.out.println("Todo correcto"); //Se guarda el ID y se activa le botón de guardar datos
                btnGuardar_Datos.setDisable(false);//Habilito el botón de guardar
                lista_ID.add(Integer.parseInt(ver_ID));//Agrego el ID

                numeroProceso++;//Una vez que se guardan los datos incrementa el numero de procesos
            }
            else
                System.out.println("No se guarda");
        }
        else
            System.out.println("Hubo datos incorrectos y no se puede guardar el proceso");

    }

    public void verifcarTotal(String ver_Procesos){//Verifica el número de lotes a crear sea númerico

        if(ver_Procesos.matches("[0-9]+") || ver_Procesos.matches("[+][0-9]+") || ver_Procesos.matches("[-][0-9]+")) {//Verificar que sea numero
            //System.out.println("Es número");
            //datosRevisados++;

        }
        else {
            //System.out.println("Error, no es número");
            errorProcesos=true;

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("El número de procesos deseados no es númerico");
            alert.setContentText("Verifique y trate de nuevo");
            alert.showAndWait();
        }
    }

    public void verifcarID(String ver_ID){
        int conf_ID;

        if(ver_ID.matches("[0-9]+")) {//Verificar que sea numero
            //System.out.println("Es número");
            conf_ID=Integer.parseInt(ver_ID);
            if(conf_ID>0){
                //System.out.println("ID mayor 0");
                //Verificar que no sea repetido
                if(lista_ID.contains(conf_ID)){//Verifica si no es Id repetido
                    //System.out.println("ID repetido");

                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("El ID es repetido");
                    alert.setContentText("Verifique y trate de nuevo");
                    alert.showAndWait();
                }
                else {
                    //System.out.println("ID Correcto");
                    datosRevisados++;
                }
            }
            else {
                //System.out.println("ID no es mayor a 0");

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("El ID no es mayor a 0");
                alert.setContentText("Verifique y trate de nuevo");
                alert.showAndWait();
            }
        }
        else {
            //System.out.println("Error, no es número");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("El ID no es númerico");
            alert.setContentText("Verifique y trate de nuevo");
            alert.showAndWait();
        }
    }

    public void verifcarOP(String ver_OP){//Verifica ambos operadores.  Agregar que reconozca cuando es cantidad con decimal

        if(ver_OP.matches("[0-9]+") || ver_OP.matches("[+][0-9]+") || ver_OP.matches("[-][0-9]+")) {//Verificar que sea numero
            //System.out.println("Es número");
            datosRevisados++;
        }
        else {
            //System.out.println("Error, no es número");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("El OP no es númerico");
            alert.setContentText("Verifique y trate de nuevo");
            alert.showAndWait();
        }
    }

    public void verifcarTM(String ver_TM){
        int conf_TM;

        if(ver_TM.matches("[0-9]+")) {//Verificar que sea numero
            //System.out.println("Es número");
            conf_TM=Integer.parseInt(ver_TM);
            if(conf_TM>0){
                //System.out.println("TM mayor 0");
                datosRevisados++;
            }
            else {
                //System.out.println("TM no es mayor a 0");

                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("El TM no es mayor a 0");
                alert.setContentText("Verifique y trate de nuevo");
                alert.showAndWait();
            }
        }
        else {
            //System.out.println("Error, no es número");

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("El TM no es númerico");
            alert.setContentText("Verifique y trate de nuevo");
            alert.showAndWait();
        }
    }

    /*
    public void debugHashTable(){
        Set<Integer> keys = hashProcesos.keySet();//Obtenemos las llaves de la hash

        for(Integer key : keys){//For usando las llaves de la hash
            Proceso pr = (Proceso)hashProcesos.get(key);//Obtenemos la instancia en base a la llave
            System.out.println("Key: "+key+ "   Nombre: "+pr.nombreProgramador+ "   ID: "+pr.ID+ "  OP1: "+pr.OP1+ "    Operación: "+pr.Operacion+ "    OP2: "+pr.OP2+ "    Tiempo máximo: "+pr.tiempoMaximo+ "    Lote: "+pr.numeroLote+ "    Proceso: "+pr.numeroProceso+"    Resultado: "+pr.Resultado);
        }
    }
    */


public void Hilo() throws InterruptedException, java.lang.Exception{
    ExecutorService ex = Executors.newSingleThreadExecutor();

    ex.execute(new Runnable() {
        @Override
        public void run() {
            try {
                pruebasLoteProceso();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

ex.shutdown();

    //Thread.currentThread().isInterrupted();

}

    public void waitWhileSuspended() throws InterruptedException {//Pausa el proceso con un sleep. Cuando se activa verifica el txtComando para ver si quita la pausa o no
        while (PAUSAR==true) {//PAUSAR es la bandera para testear la pausa
            String Tecla = txtComando.getText();
            if(Tecla.contains("P") || Tecla.contains("p"))
                PAUSAR=true;
            else if(Tecla.contains("C") || Tecla.contains("c"))
                PAUSAR=false;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    txtComando.clear();
                }
            });
            Thread.sleep(200);
        }
    }

    public void pruebasLoteProceso() throws  InterruptedException, java.lang.Exception{//Pasarle por parametro el lote a mostrar
        /*Set<Integer> keys = hashProcesos.keySet();//Obtenemos las llaves de la hash

        for(Integer key : keys){//For usando las llaves de la hash
            Proceso pr = (Proceso)hashProcesos.get(key);//Obtenemos la instancia en base a la llave
            if(pr.numeroLote==1) {
                txtLP_Nombre.appendText(pr.nombreProgramador+"\r\n");
                txtLP_Tiempo.appendText(pr.tiempoMaximo+"\r\n");
            }
        }*/

        /*
        for(int i = 1;i<=numeroLote;i++){//Recorre lotes
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    txtLP_Nombre.clear();
                    txtLP_Tiempo.clear();
                }
            });

            for (int j = 0;j<listaProcesos.size();j++){//Recorre procesos




                Proceso pr = (Proceso)listaProcesos.get(j);
                if(pr.numeroLote==i){//Carga los procesos del lote(Los muestra). Pasa a otro método y comienza la ejecución
                   Platform.runLater(new Runnable() {
                       @Override
                       public void run() {
                           txtLP_Nombre.appendText(pr.nombreProgramador+"\r\n");
                           txtLP_Tiempo.appendText(pr.tiempoMaximo+"\r\n");
                       }
                   });

                    //System.out.println("Nombre: "+pr.nombreProgramador);
                    //System.out.println("Tiempo: "+pr.tiempoMaximo);

                }
            }
        }
        */

        //ejecucionProceso();
        ejecucion_conBloqueado();
    }

    public void mostrarLoteActual(int Lote){
        int loteSeleccionado=Lote;
        txtLP_Nombre.clear();
        txtLP_Tiempo.clear();

        for(int i = 0;i<listaProcesos.size();i++){

            Proceso pr = (Proceso)listaProcesos.get(i);

            if(pr.numeroLote==loteSeleccionado){//Muestra los proceso del lote seleccionado
                txtLP_Nombre.appendText(pr.nombreProgramador+"\r\n");
                txtLP_Tiempo.appendText(pr.tiempoMaximo+"\r\n");
                System.out.println("Nombre: "+pr.nombreProgramador);
                System.out.println("Tiempo: "+pr.tiempoMaximo);


            }
        }
    }

    public void modificarHilo(){
        String Tecla = txtComando.getText();
        if(Tecla.contains("P") || Tecla.contains("p"))
            PAUSAR=true;
        else if(Tecla.contains("C") || Tecla.contains("c"))
            PAUSAR=false;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txtComando.clear();
            }
        });
    }

    boolean primerLlenado=false;
    int contadorBloqueados=0;
    public void agregarListos(){//Agregar los procesos que se ejecutarán
        if(listaListos.isEmpty() && primerLlenado==false){
            if(listaProcesos.size()>3) {//0,1,2,3 //Agrega los 4 primeros procesos
                for(int i=0;i<4;i++){
                    Proceso aux = (Proceso) listaProcesos.get(i);
                    if(aux.llegoListos==false) {
                        aux.tiempoLlegada = global;
                        aux.llegoListos = true;
                    }
                    //System.out.println(listaProcesos.get(i).toString());
                    listaListos.add(aux);//Añade los 4 primeros procesos
                }
                for (int i=0;i<4;i++)
                    listaProcesos.remove(0);
            }
            primerLlenado=true;
        }
        else{
            System.out.println("Parte de agregado en else");
            System.out.println("Tamaño de listaListos: "+listaListos.size());
            if(!listaProcesos.isEmpty() && (listaListos.size()+listaBloqueados.size())<3){//Listos solo puede tener 4 procesos
                System.out.println("Agregó listos");
                //VALIDAR PARA QUE NO INGRESE PROCESOS DE MAS
                Proceso aux = (Proceso) listaProcesos.get(0);
                listaProcesos.remove(aux);
                if(aux.llegoListos==false) {
                    aux.tiempoLlegada = global;
                    aux.llegoListos = true;
                }
                listaListos.add(aux);
                System.out.println("Agregado else: "+aux.ID);
                //listaListos.add(listaProceso
                /*
                for(int i=0;i<4 && listaListos.size()<=4;i++) {
                    if(listaBloqueados.size()==1)
                        i=0;
                    else if(listaBloqueados.size()==2)
                        i=1;
                    else if(listaBloqueados.size()==3)
                        i=2;
                    else if(listaBloqueados.size()==4)
                        i=3;
                    else
                        i=4;
                    if (listaListos.size() <4 && !listaProcesos.isEmpty() ) {//Verificar con bloqueados para que no agregue
                        Proceso aux = (Proceso) listaProcesos.get(0);
                        System.out.println("Agregado: "+aux.ID);
                        aux.tiempoLlegada=global;
                        listaListos.add(aux);
                        //listaListos.add(listaProcesos.get(0));//Añade otro proceso cuando
                        listaProcesos.remove(0);
                    }
                    else
                        break;
                }
                */
            }
        }
    }

    public void sacarBloqueado(){
        if(!listaBloqueados.isEmpty()){
            Proceso aux = (Proceso) listaBloqueados.get(0);
            listaBloqueados.remove(aux);
            listaListos.add(aux);//Lo manda a listaListo para que se agregue después cuando salga la lista
            System.out.println("Salió proceso");
            contadorBloqueados--;
            System.out.println("Tercer agregado");
        }
        else
            System.out.println("Bloqueados vacío");
    }

    int tiempo_esperaBloqueado=0;

    boolean salir=false;
    void cuentaBloqueo(){
        //salir=false;//Reinicia
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txtBloqueados.clear();
                for(int i=0;i<listaBloqueados.size();i++){//Contar tiempo
                    Proceso prBloqueado = (Proceso) listaBloqueados.get(i);
                    txtBloqueados.appendText("ID: "+prBloqueado.ID+"TB: "+prBloqueado.tiempoBloqueo+"\r\n");
                    prBloqueado.tiempoBloqueo--;
                    if(prBloqueado.tiempoBloqueo<=0) {
                        salir = true;//Primer proceso acabó
                    }
                    //System.out.println("Bloqueado: "+prBloqueado.ID);
                }
            }
        });
        if(salir==true) {
            sacarBloqueado();
            agregarListos();
            salir=false;
        }
    }

    void manejoBloqueados(){//Sirve para contar
        /*
        if(tiempo_esperaBloqueado>=8){
            tiempo_esperaBloqueado=0;
            sacarBloqueado();//Lego a 8 y saca al de bloqueado
            agregarListos();
        }
        else {
            tiempo_esperaBloqueado++;

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    txtBloqueados.clear();
                    txtBloqueados.appendText("Tiempo(1er): "+(tiempo_esperaBloqueado)+"\r\n");
                    for(int i=0;i<listaBloqueados.size();i++){
                        Proceso prBloqueado = (Proceso) listaBloqueados.get(i);
                        txtBloqueados.appendText("ID: "+prBloqueado.ID+"\r\n");
                        //System.out.println("Bloqueado: "+prBloqueado.ID);
                    }
                }
            });
            System.out.println("Tiempo bloqueado: "+(tiempo_esperaBloqueado));
            System.out.println("tamaño creados: "+listaProcesos.size());
        }
        //agregarListos();
        */
        cuentaBloqueo();
    }


    public void ejecucion_conBloqueado() throws java.lang.Exception{
        btnConfirmar_Datos.setDisable(true);
        btncrearProcesos.setDisable(true);
        int tiempoServicio_Anterior = 0;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txtComando.requestFocus();
            }
        });

        int Restante=0, Transcurrido=0, loteRestante=numeroLote;
        int loteProcesar=1;
        boolean ejecutando=true;


        while (ejecutando==true && listaTerminados.size()!=numeroProcesos_Totales){
            agregarListos();

            if(!listaListos.isEmpty()) {


                Proceso prActual = (Proceso) listaListos.get(0);//Obtiene proceso actual

                listaEjecucion.add(prActual);
                listaListos.remove(prActual);
                Proceso pt = null;//Auxiiar
                Proceso pe = null;//Auxiliar
                Restante = prActual.tiempoMaximo;
                Transcurrido = 0;
                int Listos=listaListos.size();
                int tiempoMaximo_Proceso = prActual.tiempoMaximo;


                for (int i = 0; i < tiempoMaximo_Proceso && ERROR==false && INTERRUMPIR==false; i++) {//Ejecución del proceso
                    if(prActual.comenzoEjecucion==false){
                        prActual.tiempoRespuesta=global;
                        prActual.comenzoEjecucion=true;
                    }
                    global++;
                    agregarListos();
                    System.out.println("Tamaño creados: "+listaProcesos.size());
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            txtPE.setText("ID: " + prActual.ID + "\r\n" + "Tiempo estimado: " + prActual.tiempoDefinido + "\r\n" + "Operación: " + prActual.OP1 + " " + prActual.Operacion + " " + prActual.OP2 + "\r\n");

                        }
                    });
                    Transcurrido++;

                    Restante--;
                     prActual.tiempoMaximo--;
                    prActual.tiempoRestante++;///TT

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            txtLP_Nombre.clear();
                            txtLP_Tiempo.clear();
                        }
                    });

                    for (int j = 0; j < listaListos.size(); j++) {//Procesos en listos
                        Proceso prListos = (Proceso) listaListos.get(j);
                        ///***********Muestra los proceso de la lista de listos********
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    txtLP_Nombre.appendText(prListos.ID + "\r\n");
                                    txtLP_Tiempo.appendText(prListos.tiempoMaximo + " - " + prListos.tiempoDefinido + "\r\n");//Tiempo Maximo(Restante)
                                }
                            });

                    }

                    Platform.runLater(new Runnable() {//Datos del proceso actual
                        @Override
                        public void run() {
                            txtPE.appendText("Proceso: " + (prActual.ID) + "\n");
                            txtPE.appendText("TR: " + prActual.tiempoMaximo + "\n");
                            txtPE.appendText("TT: " + prActual.tiempoRestante);//Transcurrido
                            txtNuevos.setText("Nuevos: "+listaProcesos.size());
                        }
                    });

                    ///
                    ///CÁLCULO DE TIEMPOS
                    ///
                    //prActual.tiempoRestante=Restante;
                    //prActual.tiempoFin=global;
                    prActual.tiempoServicio=Transcurrido;
                    /////////prActual.tiempoEspera=prActual.tiempoProceso-prActual.tiempoLlegada;//Temporal para la tabla
                    //prActual.tiempoRetorno=prActual.tiempoFin-prActual.tiempoLlegada;
                    //prActual.tiempoEspera=prActual.tiempoRetorno-prActual.tiempoServicio;
                    //if(prActual.tiempoEspera<0) {
                      //  prActual.tiempoEspera=prActual.tiempoServicio-prActual.tiempoRetorno;
                    //}
                    if(prActual.respuestaCalculado==false) {
                        prActual.tiempoRespuesta = prActual.tiempoRespuesta - prActual.tiempoLlegada;
                        prActual.respuestaCalculado=true;
                    }
                    ///
                    ///CÁLCULO DE TIEMPOS
                    ///

                    String Tecla = txtComando.getText();//Verifca input de txtComando para ver si pausa o no y pasar mano al método ***waitWhileSuspended();***
                    if (Tecla.equals("P") || Tecla.equals("p")) {
                        PAUSAR = true;
                    } else if (Tecla.equals("C") || Tecla.equals("c")) {
                        PAUSAR = false;
                    } else if (Tecla.equals("E") || Tecla.equals("e")) {
                        pe = prActual;
                        ERROR = true;
                        System.out.println("Proceso catalogado como error");
                    } else if (Tecla.equals("I") || Tecla.equals("i")) {
                        pt = prActual;
                        INTERRUMPIR = true;
                        System.out.println("Proceso bloqueado");
                        contadorBloqueados++;
                    } else if(Tecla.equals("N") || Tecla.equals("n")){
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                obtenerDatos_Parte1();
                                desactivarGuardar();
                            }
                        });
                        System.out.println("Proceso creado");
                    } else if(Tecla.equals("T") ||Tecla.equals("t")){
                        System.out.println("Muestra tabla con tiempos calculados");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                tiemposTemporales_Tabla();
                            }
                        });
                        PAUSAR=true;
                    }


                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            txtComando.clear();
                        }
                    });


                    int finalGlobal = global;
                    int finalLoteRestante = loteRestante;
                    int finalRestante = Restante;
                    int finalTranscurrido = Transcurrido;
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            //lbtContadorGeneral.setText("Contador general: "+Integer.toString(global));
                            txtGeneral.setText("General: " + Integer.toString(finalGlobal));
                            //lbLotesPendientes.setText("Lotes pendientes: "+Integer.toString(loteRestante));
                            //txtPendiente.setText("Pendiente: "+Integer.toString(finalLoteRestante));
                            //lbTR.setText("TR: "+Integer.toString(Restante));
                            //lbTT.setText("TT: "+Integer.toString(Transcurrido));
                            txtTR.setText("TR: " + Integer.toString(finalRestante));
                            txtTT.setText("TT: " + Integer.toString(finalTranscurrido));
                        }
                    });

                    waitWhileSuspended();
                    //VER LO DE BLOQUEADOS PARA SU FUNCIONAMIENTO EN CUANTO a como administrarlo junto con los procesos normales
                    if (!listaBloqueados.isEmpty()) {
                        //Hay bloqueados. manejarlos
                        manejoBloqueados();
                    }
                    else{
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                txtBloqueados.clear();
                            }
                        });
                    }

                    Thread.sleep(1000);
                    if (INTERRUMPIR == true)
                        break;
                    else if (ERROR == true)
                        break;
                }


                if (ERROR == true) {//Si error es verdadero modificamos como guardará el proceso
                    listaEjecucion.remove(pe);
                    pe.tiempoServicio=Transcurrido;
                    //tiempoServicio_Anterior=pe.tiempoServicio;
                    pe.tiempoFin=global;
                    pe.tiempoRetorno=pe.tiempoFin-pe.tiempoLlegada;
                    pe.tiempoEspera=pe.tiempoRetorno-pe.tiempoServicio;
                    //pe.tiempoRespuesta=pe.tiempoRespuesta-pe.tiempoLlegada;//Calculador en for
                    /*
                    pe.tiempoRespuesta=tiempoServicio_Anterior-pe.tiempoLlegada;
                    if(pe.tiempoRespuesta<0)
                        pe.tiempoRespuesta=pe.tiempoLlegada-tiempoServicio_Anterior;
                        */
                    //pe.tiempoRespuesta=pe.tiempoRetorno-pe.tiempoServicio;
                    listaTerminados.add("ID: " + pe.ID + "    Operación: " + "ERROR" + "    Resultado: " + "ERROR" + "   TIEMPO: " + "ERROR"+ "    Tiempo llegada: "+pe.tiempoLlegada +"    TiempoServicio: "+pe.tiempoServicio+ "    Tiempofin: "+pe.tiempoFin+ "    Tiempo retorno: "+pe.tiempoRetorno+"    Tiempo espera: "+pe.tiempoEspera+ "    Tiempo respuesta: "+pe.tiempoRespuesta);
                    //tiempoServicio_Anterior=pe.tiempoServicio;
                    //listaProcesos.remove(pr);//Quitamos el proceso erroneo
                    ERROR = false;//Devolvemos la bandera a como estaba
                    //j=-1;//La ponemos en -1 para que cuando comience esté en 0 y comience nuevamente
                } else if (INTERRUMPIR == true) {//Agregar lo de bloqueados - *****BLOQUEADOOOOOOOOOOOSSSSSSSSSSSSS!!!!*****
                    INTERRUMPIR = false;
                    listaEjecucion.remove(pt);//Remueve de la que ejecuta
                    pt.tiempoBloqueo=8;
                    listaBloqueados.add(pt);//Añade a bloqueados
                    System.out.println("Tamaño bloqueado: "+listaBloqueados.size());
                    //listaListos.add(pt);

                    //j=-1;//La ponemos en -1 para que cuando comience esté en 0 y comience nuevamente
                } else {
                    prActual.tiempoFin=global;
                    prActual.tiempoServicio=prActual.tiempoProceso;
                    prActual.tiempoRetorno=prActual.tiempoFin-prActual.tiempoLlegada;
                    prActual.tiempoEspera=prActual.tiempoRetorno-prActual.tiempoServicio;
                    //prActual.tiempoRespuesta=prActual.tiempoRespuesta-prActual.tiempoLlegada; Calcualdo en for
                    /*
                    prActual.tiempoRespuesta=tiempoServicio_Anterior-prActual.tiempoLlegada;
                    if(prActual.tiempoRespuesta<0)
                        prActual.tiempoRespuesta=prActual.tiempoLlegada-tiempoServicio_Anterior;
                        */
                    //prActual.tiempoRespuesta=prActual.tiempoRetorno-prActual.tiempoServicio;
                    listaTerminados.add("ID: " + prActual.ID + "    Operación: " + prActual.OP1 + prActual.Operacion + prActual.OP2 + "    Resultado: " + prActual.Resultado + "   TIEMPO: " + prActual.tiempoMaximo+ "     Tiempo llegada: "+prActual.tiempoLlegada +"    Tiempo Servicio: "+prActual.tiempoServicio +"    TiempoFin: "+prActual.tiempoFin+ "    Tiempo retorno: "+prActual.tiempoRetorno+ "    Tiempo espera: "+prActual.tiempoEspera+"    Tiempo respuesta: "+prActual.tiempoRespuesta);
                    listaEjecucion.remove(prActual);
                    //tiempoServicio_Anterior=prActual.tiempoServicio;
                    //if(!listaListos.isEmpty())
                    //j=-1;//La ponemos en -1 para que cuando comience esté en 0 y comience nuevamente
                }
            }
            else{//No hay procesos en listos. Todos se mandaron a bloqueados
                //VER LO DE BLOQUEADOS PARA SU FUNCIONAMIENTO EN CUANTO a como administrarlo junto con los procesos normales
                if (!listaBloqueados.isEmpty()) {

                    String Tecla = txtComando.getText();//Verifca input de txtComando para ver si pausa o no y pasar mano al método ***waitWhileSuspended();***
                    if (Tecla.equals("P") || Tecla.equals("p")) {
                        PAUSAR = true;
                    } else if (Tecla.equals("C") || Tecla.equals("c")) {
                        PAUSAR = false;
                    } else if(Tecla.equals("N") || Tecla.equals("n")){
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                obtenerDatos_Parte1();
                                desactivarGuardar();
                            }
                        });
                        System.out.println("Proceso creado");
                    } else if(Tecla.equals("T") ||Tecla.equals("t")){
                        System.out.println("Muestra tabla con tiempos calculados");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                tiemposTemporales_Tabla();
                            }
                        });
                        PAUSAR=true;
                    }


                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            txtComando.clear();
                        }
                    });
                    waitWhileSuspended();


                    //Hay bloqueados. manejarlos
                    manejoBloqueados();
                    global++;//Aumenta global si es que no hay procesos listos
                    Thread.sleep(1000);
                }
                else{
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            txtBloqueados.clear();
                        }
                    });
                }
            }


//HARA QUE AL FINAL MUESTRE TERMINADOS DE NUEVO
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    txtTerminados.clear();
                }
            });

            for(int r=0;r<listaTerminados.size();r++){//Muestra los terminados
                int finalR = r;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        txtTerminados.appendText(listaTerminados.get(finalR)+"\r\n");
                    }
                });
            }

            int finalGlobal1 = global;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    txtPE.clear();
                    txtLP_Nombre.clear();
                    txtLP_Tiempo.clear();
                    //lbtContadorGeneral.setText("Contador general: "+Integer.toString(global));
                    txtGeneral.setText("General: "+Integer.toString(finalGlobal1));
                }
            });



        }

        //HARA QUE AL FINAL MUESTRE TERMINADOS DE NUEVO
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txtTerminados.clear();
            }
        });

        for(int r=0;r<listaTerminados.size();r++){//Muestra los terminados
            int finalR = r;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    txtTerminados.appendText(listaTerminados.get(finalR)+"\r\n");
                }
            });
        }

        int finalGlobal1 = global;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txtPE.clear();
                txtLP_Nombre.clear();
                txtLP_Tiempo.clear();
                //lbtContadorGeneral.setText("Contador general: "+Integer.toString(global));
                txtGeneral.setText("General: "+Integer.toString(finalGlobal1));
            }
        });


    }

    public void tiemposTemporales_Tabla(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Tabla de tiempos calculados");
        alert.setHeaderText("Tiempos");
        alert.setContentText("Presione show details para desplegar datos");

        // Create expandable Exception.

        Label label = new Label("Presione Ok para cerrar ventana y después presione C para continuar proceso");

        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(false);

        if(!listaTerminados.isEmpty()){
            textArea.appendText("\n"+"Terminados"+"\n");
            for(int i=0;i<listaTerminados.size();i++)
                textArea.appendText(listaTerminados.get(i)+"\n");
        }

        if(!listaEjecucion.isEmpty()){
            textArea.appendText("\n"+"Ejecución"+"\n");
            Proceso pr = (Proceso)listaEjecucion.get(0);
            textArea.appendText("ID: "+pr.ID+" Llegada: "+pr.tiempoLlegada+" Máx: "+pr.tiempoProceso+" Servicio: "+pr.tiempoServicio+" Restante: "+pr.tiempoMaximo+" Respuesta: "+pr.tiempoRespuesta+"\n");
        }

        if(!listaBloqueados.isEmpty()){
            textArea.appendText("\n"+"Bloqueados"+"\n");
            for(int i=0;i<listaBloqueados.size();i++){
                Proceso pr = (Proceso)listaBloqueados.get(i);
                textArea.appendText("ID: "+pr.ID+" Llegada: "+pr.tiempoLlegada+" Bloqueo: "+pr.tiempoBloqueo+" Máx: "+pr.tiempoProceso+" Servicio: "+pr.tiempoServicio+" Restante: "+pr.tiempoMaximo+" Respuesta: "+pr.tiempoRespuesta+"\n");
            }
        }

        if(!listaProcesos.isEmpty()){
            textArea.appendText("\n"+"Nuevos"+"\n");
            for (int i = 0; i <listaProcesos.size(); i++) {
                Proceso pr = (Proceso)listaProcesos.get(i);
                textArea.appendText("ID: "+pr.ID+"\n");//Tiempos listos
            }
        }

        if(!listaListos.isEmpty()){
            textArea.appendText("\n"+"Listos"+"\n");
            for (int i = 0; i <listaListos.size(); i++) {
                Proceso pr = (Proceso)listaListos.get(i);
                textArea.appendText("ID: "+pr.ID+" Llegada: "+pr.tiempoLlegada+" Máx: "+pr.tiempoProceso+" Servicio: "+pr.tiempoServicio+" Restante: "+pr.tiempoMaximo+" Respuesta: "+pr.tiempoRespuesta+"\n");
            }
        }

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);
        alert.showAndWait();
    }

    public void ejecucionProceso() throws InterruptedException{
        btnConfirmar_Datos.setDisable(true);
        btncrearProcesos.setDisable(true);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txtComando.requestFocus();
            }
        });

        int Restante=0, Transcurrido=0, global=0, loteRestante=numeroLote;
        int loteProcesar=1;

        //AGREGA LOS PRIMEROS 4 PROCESOS
        agregarListos();

        for (int i=1;i<=numeroLote;i++){//Símbólico para el control. Solo que ya no se mostrará
            loteRestante=(loteRestante-1);
            for(int j=0;j<listaListos.size();j++) {
                agregarListos();//Verifica que haya procesos

                Proceso pr = (Proceso) listaListos.get(j);
                Proceso pt = null;
                Proceso pe = null;
                Proceso pu = null;
                if(pr.numeroLote==i) {
                    Restante = pr.tiempoMaximo;
                    Transcurrido = 0;
                    int tiempoEjecucion_Lote=pr.tiempoMaximo;
                    for (int aux = 0; aux < tiempoEjecucion_Lote && ERROR==false && INTERRUMPIR==false; aux++) {

                        pu=pr;//Auxiliar para el proceso normal
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                txtPE.setText("ID: " + pr.ID + "\r\n" + "Tiempo estimado: " + pr.tiempoDefinido + "\r\n" + "Operación: " + pr.OP1 + " " + pr.Operacion + " " + pr.OP2+"\r\n");

                            }
                        });
                        Transcurrido++;
                        global++;
                        Restante--;
                        pr.tiempoMaximo--;
                        pr.tiempoRestante++;///TT


                        System.out.println("\n");
                        System.out.println("\n");
                        System.out.println("\n");
                        System.out.println("\n");
                        System.out.println("\n");
                        System.out.println("\n");
                        System.out.println("\n");
                        System.out.println("\n");
                        System.out.println("\n");
                        System.out.println("------------------------");
                        System.out.println("Lote actual-------------");
                        System.out.println("------------------------");
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                txtLP_Nombre.clear();
                                txtLP_Tiempo.clear();
                            }
                        });

                        for(int k = 0;k<listaListos.size();k++){
                            Proceso prr = (Proceso)listaListos.get(k);
                            if(prr.numeroLote==i){///***********Muestra los proceso del lote seleccionado********
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        txtLP_Nombre.appendText(prr.ID+"\r\n");
                                        txtLP_Tiempo.appendText(prr.tiempoMaximo+" - "+prr.tiempoDefinido+"\r\n");//Tiempo Maximo(Restante)
                                    }
                                });

                                System.out.println("Nombre: "+prr.nombreProgramador);
                                System.out.println("Tiempo: "+prr.tiempoMaximo);
                            }
                        }


                        System.out.println("------------------------");
                        System.out.println("Proceso en ejecución----");
                        System.out.println("------------------------");
                        int finalI = i;
                        int finalJ = j;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                txtPE.appendText("Lote: "+ finalI +"\n");
                                txtPE.appendText("Proceso: "+(finalJ +1)+"\n");
                                txtPE.appendText("TR: "+pr.tiempoMaximo+"\n");
                                txtPE.appendText("TT: "+pr.tiempoRestante);//Transcurrido
                            }
                        });

                        System.out.println("Lotes restantes: "+loteRestante);
                        System.out.println("Lote: " + i);
                        System.out.println("Proceso: " + (j+1)+"\n");
                        System.out.println("Máximo: "+pr.tiempoMaximo);
                        System.out.println("Trans: " + Transcurrido);
                        System.out.println("Global: " + global);
                        System.out.println("Restante: " + Restante);


                        String Tecla = txtComando.getText();//Verifca input de txtComando para ver si pausa o no y pasar mano al método ***waitWhileSuspended();***
                        if(Tecla.equals("P") || Tecla.equals("p")) {
                            PAUSAR = true;
                        }
                        else if(Tecla.equals("C") || Tecla.equals("c")) {
                            PAUSAR = false;
                        }
                        else if(Tecla.equals("E") || Tecla.equals("e")) {
                            pe=pr;
                            ERROR=true;
                            System.out.println("Proceso catalogado como error");
                        }
                        else if(Tecla.equals("I") || Tecla.equals("i")) {
                            pt=pr;
                            INTERRUMPIR=true;
                            System.out.println("Proceso intrrumpido");
                        }


                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                txtComando.clear();
                            }
                        });
                        //txtComando.clear();
                        /*
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                String Tecla = txtComando.getText();
                                if(Tecla.contains("P") || Tecla.contains("p"))
                                    PAUSAR=true;
                                else if(Tecla.contains("C") || Tecla.contains("c"))
                                    PAUSAR=false;
                                txtComando.clear();
                            }
                        });
                        */




                        //PAUSAR
                        if(PAUSAR==true)
                            System.out.println("PAUSAR es true - P");
                        else
                            System.out.println("PAUSAR es false - C");


                        int finalGlobal = global;
                        int finalLoteRestante = loteRestante;
                        int finalRestante = Restante;
                        int finalTranscurrido = Transcurrido;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                //lbtContadorGeneral.setText("Contador general: "+Integer.toString(global));
                                txtGeneral.setText("General: "+Integer.toString(finalGlobal));
                                //lbLotesPendientes.setText("Lotes pendientes: "+Integer.toString(loteRestante));
                                txtPendiente.setText("Pendiente: "+Integer.toString(finalLoteRestante));
                                //lbTR.setText("TR: "+Integer.toString(Restante));
                                //lbTT.setText("TT: "+Integer.toString(Transcurrido));
                                txtTR.setText("TR: "+Integer.toString(finalRestante));
                                txtTT.setText("TT: "+Integer.toString(finalTranscurrido));
                            }
                        });



                        waitWhileSuspended();


                        Thread.sleep(1000);
                        if(INTERRUMPIR==true)
                            break;
                        else if(ERROR==true)
                            break;
                    }
                    //Eliminar proceso erroneo
                    if(ERROR==true) {//Si error es verdadero modificamos como guardará el proceso
                        listaListos.remove(pe);
                        pe.tiempoFin=global;
                        pe.tiempoRetorno=pe.tiempoFin-pe.tiempoLlegada;
                        pe.tiempoEspera=pe.tiempoRetorno-pe.tiempoServicio;
                        listaTerminados.add("ID: " + pe.ID + "    Operación: " + "ERROR" + "    Número de lote: " + pe.numeroLote + "    Resultado: " + "ERROR" + "   TIEMPO: " + "ERROR"+ "Tiempofin: "+pe.tiempoFin+ "Tiempo retorno: "+pe.tiempoRetorno+" Tiempo espera: "+pe.tiempoEspera);
                        //listaProcesos.remove(pr);//Quitamos el proceso erroneo
                        ERROR=false;//Devolvemos la bandera a como estaba
                        j=-1;//La ponemos en -1 para que cuando comience esté en 0 y comience nuevamente
                    }
                    else if(INTERRUMPIR==true){
                        INTERRUMPIR=false;
                        listaListos.remove(pt);//Remueve de la que ejecuta
                        listaBloqueados.add(pt);//Añade a bloqueados
                        //listaListos.add(pt);

                        j=-1;//La ponemos en -1 para que cuando comience esté en 0 y comience nuevamente
                    }
                    else {
                        pr.tiempoFin=global;
                        pr.tiempoRetorno=pr.tiempoFin-pr.tiempoLlegada;
                        pr.tiempoEspera=pr.tiempoRetorno-pr.tiempoServicio;
                        listaTerminados.add("ID: " + pr.ID + "    Operación: " + pr.OP1 + pr.Operacion + pr.OP2 + "    Número de lote: " + pr.numeroLote + "    Resultado: " + pr.Resultado + "   TIEMPO: " + pr.tiempoMaximo+ "TiempoFin: "+pr.tiempoFin+ "Tiempo retorno: "+pr.tiempoRetorno+ "Tiempo espera: "+pr.tiempoEspera);
                        listaListos.remove(pu);
                        if(!listaListos.isEmpty())
                            j=-1;//La ponemos en -1 para que cuando comience esté en 0 y comience nuevamente
                    }
                    System.out.println("------------------------");
                    System.out.println("Procesos Terminados-----");
                    System.out.println("------------------------");

                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            txtTerminados.clear();
                        }
                    });

                    for(int r=0;r<listaTerminados.size();r++){//Muestra los terminados
                        System.out.println(listaTerminados.get(r));
                        System.out.println("\n");
                        System.out.println("Valor R: "+r);
                        int finalR = r;
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                txtTerminados.appendText(listaTerminados.get(finalR)+"\r\n");
                            }
                        });

                    }
                }
            }
        }
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("\n");
        System.out.println("------------------------");
        System.out.println("Lote actual-------------");
        System.out.println("------------------------");
        System.out.println("\n");

        System.out.println("------------------------");
        System.out.println("Proceso en ejecución----");
        System.out.println("------------------------");
        System.out.println("\n");

        System.out.println("------------------------");
        System.out.println("Procesos Terminados-----");
        System.out.println("------------------------");
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txtTerminados.clear();
            }
        });

        for(int r=0;r<listaTerminados.size();r++){//Muestra los terminados
            int finalR = r;
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    txtTerminados.appendText(listaTerminados.get(finalR)+"\r\n");
                }
            });

            System.out.println(listaTerminados.get(r));
            System.out.println("\n");
        }

        System.out.println("Lotes restantes: "+loteRestante);
        System.out.println("Contador global: "+global);
        int finalGlobal1 = global;
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                txtPE.clear();
                txtLP_Nombre.clear();
                txtLP_Tiempo.clear();
                //lbtContadorGeneral.setText("Contador general: "+Integer.toString(global));
                txtGeneral.setText("General: "+Integer.toString(finalGlobal1));
            }
        });

        System.out.println("Tamaño lista procesos: "+listaProcesos.size());

    }
}
