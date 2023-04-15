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
        performValidation(args);
        int processCount = Integer.parseInt(args[0]);
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
            }
        }
    }

    private static void performValidation(String[] args) throws IllegalArgumentException {
        if (args.length != 1) {
            throw new IllegalArgumentException("Runner should called with processCount");
        }
        try {
            Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("Error while parsing the processCount " + e.getMessage());
        }
    }
}
