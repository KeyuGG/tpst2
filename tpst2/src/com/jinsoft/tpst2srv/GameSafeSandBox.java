package com.jinsoft.tpst2srv;

import com.jinsoft.tpst2srv.common.Global;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GameSafeSandBox extends Thread implements Runnable {

    private static final Logger LOG = Logger.getLogger(GameSafeSandBox.class.getName());
    
    private ServerSocket server;
    private BufferedReader reader;
    private BufferedWriter writer;
    private String xml;

    public GameSafeSandBox() {
        xml = "<cross-domain-policy> "
                + "<allow-access-from domain=\"*\" to-ports=\"21680-21690\"/>"
                + "</cross-domain-policy> ";
        //System.out.println("policyfile文件路径: " + path);
        //System.out.println(xml);
        
        //启动843端口
        createServerSocket();
    }
    

    //启动服务器
    private void createServerSocket() {
        try {
            server = new ServerSocket(Global.SAFESANDBOX_PORT);
            //System.out.println( "服务监听端口：" + Global.SAFESANDBOX_PORT );
        } catch (IOException e) {
            System.exit(1);
        }
    }

//启动服务器线程
    @Override
    public void run() {
        while (true) {
            Socket client = null;
            try {
                //接收客户端的连接
                client = server.accept();

                InputStreamReader input = new InputStreamReader(client.getInputStream(), "UTF-8");
                reader = new BufferedReader(input);
                OutputStreamWriter output = new OutputStreamWriter(client.getOutputStream(), "UTF-8");
                writer = new BufferedWriter(output);

                //读取客户端发送的数据
                StringBuilder data = new StringBuilder();
                int c;
                while ((c = reader.read()) != -1) {
                    if (c != '\0') {
                        data.append((char) c);
                    } else {
                        break;
                    }
                }
                
                String info = data.toString();
                LOG.log(Level.INFO, "输入的请求: {0}", info);

                //接收到客户端的请求之后，将策略文件发送出去
                if (info.indexOf("<policy-file-request/>") >= 0) {
                    writer.write(xml + "\0");
                    writer.flush();
                    LOG.log(Level.INFO, "将安全策略文件发送至: {0}", client.getInetAddress());
                } else {
                    writer.write("请求无法识别\0");
                    writer.flush();
                    LOG.log(Level.INFO, "请求无法识别: {0}", client.getInetAddress());
                }
                client.close();
            } catch (Exception e) {
                LOG.warning(e.getMessage());
                try {
                    //发现异常关闭连接
                    if (client != null) {
                        client.close();
                        client = null;
                    }
                } catch (IOException ex) {
                    LOG.warning(ex.getMessage());
                } finally {
                    //调用垃圾收集方法
                    System.gc();
                }
            }
        }
    }
}