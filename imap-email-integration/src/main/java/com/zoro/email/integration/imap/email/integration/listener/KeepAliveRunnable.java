package com.zoro.email.integration.imap.email.integration.listener;

import com.sun.mail.imap.IMAPFolder;

import javax.mail.MessagingException;

public class KeepAliveRunnable implements Runnable {
    private static final long KEEP_ALIVE_FREQ = 300000; // 5 minutes
    private IMAPFolder folder;
    public KeepAliveRunnable(IMAPFolder folder) {
        this.folder = folder;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                Thread.sleep(KEEP_ALIVE_FREQ);

                // Perform a NOOP to keep the connection alive
                System.out.println("Performing a NOOP to keep the connection alive");
                folder.doCommand(protocol -> {
                    protocol.simpleCommand("NOOP", null);
                    return null;
                });
            } catch (InterruptedException e) {
                // Ignore, just aborting the thread...
            } catch (MessagingException e) {
                // Shouldn't really happen...
                System.out.println("Unexpected exception while keeping alive the IDLE connection");
                e.printStackTrace();
            }
        }
    }
}
