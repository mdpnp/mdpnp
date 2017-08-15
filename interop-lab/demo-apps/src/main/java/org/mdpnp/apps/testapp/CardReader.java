package org.mdpnp.apps.testapp;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.smartcardio.ATR;
import javax.smartcardio.Card;
import javax.smartcardio.CardChannel;
import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.CommandAPDU;
import javax.smartcardio.ResponseAPDU;
import javax.smartcardio.TerminalFactory;

public class CardReader {

	private static CardTerminal terminal = null;
	private static ResponseAPDU response = null;
	private static CardChannel channel;

	public static boolean findTerminals() {
		boolean active = false;
		TerminalFactory factory = null;
		List<CardTerminal> terminalList;

		try {
			factory = TerminalFactory.getInstance("PC/SC", null);
		} catch (NoSuchAlgorithmException e) {
			return active = false;
		}

		try {
			terminalList = factory.terminals().list();
		} catch (CardException e) {
			return active = false;
		}

		if (!terminalList.isEmpty())
			active = true;

		return active;
	}

	public static ResponseAPDU Reader() {
		try {
			// get the list of available terminals
			TerminalFactory factory = TerminalFactory.getInstance("PC/SC", null);
			List<CardTerminal> terminalList = factory.terminals().list();

			// System.out.println(terminalList);

			// take the first terminal in the list
			terminal = (CardTerminal) terminalList.get(0);

			terminal.waitForCardPresent(0);

			// establish a connection with the card
			Card card = terminal.connect("*");
			// System.out.println("Card: " + card);
			channel = card.getBasicChannel();

			// reset the card
			@SuppressWarnings("unused")
			ATR atr = card.getATR();
			// System.out.println("ATR: " + bytesToHex(atr.getBytes()));

			// APDU Command to get UID
			// byte[] commandUID = new byte[] { (byte) 0xFF, (byte) 0xCA, 0x00,
			// 0x00, 0x00 };
			byte[] commandUID = { (byte) 0xFF, (byte) 0xCA, 0x00, 0x00, 0x00 };

			CommandAPDU command = new CommandAPDU(commandUID);

			response = channel.transmit(command);

			// disconnect
			card.disconnect(true);

		} catch (Throwable t) {
			/*
			 * create a program restart method and call it here
			 */
		}
		return response;
	}

	public static CardTerminal getTerminal() {
		return terminal;
	}

	public static ResponseAPDU getResponse() {
		return response;
	}

	public static CardChannel getChannel() {
		return channel;
	}
}

