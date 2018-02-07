package com.shashank.singh.splitbill.Networking;

import android.os.AsyncTask;

import com.shashank.singh.splitbill.fragment.ExpenseFragment;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Created by shashank on 5/1/2017.
 */

public class TestAsyncTask extends AsyncTask<Void, Void, Void> {

    private final ExpenseFragment.FragmentCallback mFragmentCallback;

    boolean exists = false;

    @Override
    protected Void doInBackground(Void... voids) {
        String ip ="52.15.120.64";
        int port=80;

        try {
            SocketAddress sockaddr = new InetSocketAddress(ip, port);
            // Create an unbound socket
            Socket sock = new Socket();

            // This method will block no more than timeoutMs.
            // If the timeout occurs, SocketTimeoutException is thrown.
            int timeoutMs =10000;   // 2 seconds
            sock.connect(sockaddr, timeoutMs);
            exists = true;
        } catch(IOException e) {
            // Handle exception
        }
        return null;
    }

    public TestAsyncTask(ExpenseFragment.FragmentCallback fragmentCallback) {
        mFragmentCallback = fragmentCallback;
    }

    @Override
    protected void onPostExecute(Void result) {
        mFragmentCallback.onTaskDone(exists);
    }


}
