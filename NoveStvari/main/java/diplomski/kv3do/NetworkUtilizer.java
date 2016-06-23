package diplomski.kv3do;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by KenanPC on 21/06/2016.
 */
public class NetworkUtilizer extends AsyncTask{

    private Socket socket = null;
    String porukaZaSljanje;
    JSONObject objekatZaSlanje;

    public NetworkUtilizer(JSONObject objekat)
    {
        objekatZaSlanje = objekat;
    }

    @Override
    protected String doInBackground(Object[] params) {

        String ip = "192.168.1.63";
        int port = 8081;

        try {
            KonektujSoket(ip,port);

            posaljiPoruku(objekatZaSlanje);

            ZatvoriSoket();
        }
        catch (Exception e)
        {
            System.out.print(e.getMessage());
            return "Greška pri slanju poruke";
        }

        return "Poslana prva poruka";
    }

    private void KonektujSoket(String ip, int port) throws UnknownHostException, IOException
    {
        System.out.print("Konektujem se na " + ip);

        socket = new Socket(ip, port);
    }

    private String posaljiPoruku(JSONObject objekat)
    {
        String returnStr = "";
        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(getSocket().getInputStream()));

            out.println(objekat);
            returnStr = in.readLine();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            System.out.print(e.getMessage());
        }

        return returnStr;
    }

    private void ZatvoriSoket() throws IOException
    {
        this.socket.close();
        porukaZaSljanje = "";
    }

    private Socket getSocket() {
        return this.socket;
    }
}
