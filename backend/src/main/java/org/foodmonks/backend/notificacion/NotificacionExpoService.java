package org.foodmonks.backend.notificacion;

import io.github.jav.exposerversdk.ExpoPushMessage;
import io.github.jav.exposerversdk.ExpoPushMessageTicketPair;
import io.github.jav.exposerversdk.ExpoPushReceipt;
import io.github.jav.exposerversdk.ExpoPushTicket;
import io.github.jav.exposerversdk.PushClient;
import io.github.jav.exposerversdk.PushClientException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class NotificacionExpoService {

    public void crearNotifacion(String tokenExpo, String asunto, String message) throws PushClientException, InterruptedException {

        if (!PushClient.isExponentPushToken(tokenExpo))
            throw new Error("Token:" + tokenExpo + " is not a valid token.");

        ExpoPushMessage expoPushMessage = new ExpoPushMessage();
        expoPushMessage.getTo().add(tokenExpo);
        expoPushMessage.setTitle(asunto);
        expoPushMessage.setBody(message);

        List<ExpoPushMessage> expoPushMessages = new ArrayList<>();
        expoPushMessages.add(expoPushMessage);

        PushClient client = new PushClient();
        List<List<ExpoPushMessage>> chunks = client.chunkPushNotifications(expoPushMessages);

        List<CompletableFuture<List<ExpoPushTicket>>> messageRepliesFutures = new ArrayList<>();

        for (List<ExpoPushMessage> chunk : chunks) {
            messageRepliesFutures.add(client.sendPushNotificationsAsync(chunk));
        }

        // Wait for each completable future to finish
        List<ExpoPushTicket> allTickets = new ArrayList<>();
        for (CompletableFuture<List<ExpoPushTicket>> messageReplyFuture : messageRepliesFutures) {
            try {
                for (ExpoPushTicket ticket : messageReplyFuture.get()) {
                    allTickets.add(ticket);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

        List<ExpoPushMessageTicketPair<ExpoPushMessage>> zippedMessagesTickets = client.zipMessagesTickets(expoPushMessages, allTickets);

        List<ExpoPushMessageTicketPair<ExpoPushMessage>> okTicketMessages = client.filterAllSuccessfulMessages(zippedMessagesTickets);
        String okTicketMessagesString = okTicketMessages.stream().map(
                p -> "Asunto: " + p.message.getTitle() + ", Id:" + p.ticket.getId()
        ).collect(Collectors.joining(","));
        System.out.println(
                "Recieved OK ticket for " +
                        okTicketMessages.size() +
                        " messages: " + okTicketMessagesString
        );

        List<ExpoPushMessageTicketPair<ExpoPushMessage>> errorTicketMessages = client.filterAllMessagesWithError(zippedMessagesTickets);
        String errorTicketMessagesString = errorTicketMessages.stream().map(
                p -> "Asunto: " + p.message.getTitle() + ", Error: " + p.ticket.getDetails().getError()
        ).collect(Collectors.joining(","));
        System.out.println(
                "Recieved ERROR ticket for " +
                        errorTicketMessages.size() +
                        " messages: " +
                        errorTicketMessagesString
        );

        System.out.println("Fetching reciepts...");

        List<String> ticketIds = (client.getTicketIdsFromPairs(okTicketMessages));
        CompletableFuture<List<ExpoPushReceipt>> receiptFutures = client.getPushNotificationReceiptsAsync(ticketIds);

        List<ExpoPushReceipt> receipts = new ArrayList<>();
        try {
            receipts = receiptFutures.get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(
                "Recieved " + receipts.size() + " receipts:");

        for (ExpoPushReceipt reciept : receipts) {
            System.out.println(
                    "Receipt for id: " +
                            reciept.getId() +
                            " had status: " +
                            reciept.getStatus());
        }

    }
}