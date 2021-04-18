package server;

import common.exceptions.MakeSocketException;
import common.exceptions.ConnectionErrorException;
import common.exceptions.CloseSocketException;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.logging.Level;


public class ServerHelper {
    public static final int CONNECTION_TIMEOUT = 60000;

    private int port;
    private ServerSocket serverSocket;

    public ServerHelper(int port) {
        this.port = port;
    }

    public void start() {
        try {
            makeSocket();
            boolean processingStatus = true;
            while (processingStatus) {
                try (Socket clientSocket = connectToClient()) {
                    processingStatus = processClientRequest(clientSocket);
                } catch (ConnectionErrorException | SocketTimeoutException exception) {
                    break;
                } catch (IOException exception) {
                    ServerMain.logger.log(Level.SEVERE,"Произошла ошибка при попытке завершить соединение с клиентом!", exception);
                }
            }
            stop();
        } catch (MakeSocketException exception) {
            ServerMain.logger.log(Level.SEVERE,"Сервер не может быть запущен!", exception);
        }
    }
    private void stop() {
        try {
            ServerMain.logger.info("Завершение работы сервера...");
            if (serverSocket == null) throw new CloseSocketException();
            serverSocket.close();
            ServerMain.logger.info("Работа сервера успешно завершена.");
        } catch (CloseSocketException exception) {
            ServerMain.logger.error("Невозможно завершить работу еще не запущенного сервера!");
        } catch (IOException exception) {
            ServerMain.logger.error("Произошла ошибка при завершении работы сервера!");
        }
    }
    private void makeSocket() throws MakeSocketException {
        try {
            ServerMain.logger.info("Запуск сервера...");
            serverSocket = new ServerSocket(port);
            serverSocket.setSoTimeout(soTimeout);
            ServerMain.logger.info("Сервер успешно запущен.");
        } catch (IllegalArgumentException exception) {
            ServerMain.logger.fatal("Порт '" + port + "' находится за пределами возможных значений!");
            throw new MakeSocketException();
        } catch (IOException exception) {
            ServerMain.logger.fatal("Произошла ошибка при попытке использовать порт '" + port + "'!");
            throw new MakeSocketException();
        }
    }

}
