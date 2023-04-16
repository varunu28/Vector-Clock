package com.varun;

import com.varun.storage.DatabaseServer;
import com.varun.storage.GetResponseConditionUtil;
import com.varun.util.MessageQueue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Runner {
    public static void main(String[] args) throws IllegalArgumentException, IOException {
        int processCount = performValidation();
        MessageQueue messageQueue = new MessageQueue(processCount);
        ExecutorService executorService = Executors.newFixedThreadPool(processCount);
        GetResponseConditionUtil getResponseConditionUtil = new GetResponseConditionUtil();
        for (int i = 1; i <= processCount; i++) {
            executorService.submit(new DatabaseServer(i, processCount, messageQueue, getResponseConditionUtil));
        }
        processUserInput(messageQueue, processCount);
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private static void processUserInput(MessageQueue messageQueue, int processCount) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            String message = bufferedReader.readLine();
            String[] splits = message.split("\\s+");
            try {
                int processId = Integer.parseInt(splits[0]);
                if (processId > processCount || processId < 1) {
                    System.out.printf("Invalid processId. processId should be in range 1 & %d \n", processCount);
                    continue;
                }
                String publishedMessage = message.substring(message.indexOf(' ') + 1);
                messageQueue.publishMessage(processId, publishedMessage);
            } catch (NumberFormatException e) {
                System.out.println("processId(Integer) should be provided with each command");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static int performValidation() throws IllegalArgumentException {
        String processCount = System.getProperty("processCount");
        if (processCount == null) {
            throw new IllegalArgumentException("Runner should called with processCount");
        }
        try {
            return Integer.parseInt(processCount);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Error while parsing the processCount " + e.getMessage());
        }
    }
}
