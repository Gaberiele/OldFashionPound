package oldFashionPound;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OldPoundConverter {

	private static final int PENCE_PER_SCELLINI = 12;
	private static final int SCELLINI_PER_STERLINE = 20;
	private static final int PENCE_PER_STERLINE = SCELLINI_PER_STERLINE * PENCE_PER_SCELLINI;

	public static String formatPoundsString(int pounds) {
		// Calcoliamo il numero di sterline
		int sterline = pounds / PENCE_PER_STERLINE;
		int restantePence = pounds % PENCE_PER_STERLINE;
		// Calcoliamo il numero di scellini
		int scellini = restantePence / PENCE_PER_SCELLINI;
		int pence = restantePence % PENCE_PER_SCELLINI;
		return sterline + "p " + scellini + "s " + pence + "d";
	}

	// Converte il prezzo in pence
	public static int parsePounds(String input) {
		int sterline = 0, scellini = 0, pence = 0;
		boolean hasPounds = false, hasShillings = false, hasPence = false;
		if (input == null || input.isBlank()) {
			throw new IllegalArgumentException("Errore: input vuoto o nullo.");
		}

		Pattern pattern = Pattern.compile("(\\d+)(?i)([psd])");
		Matcher matcher = pattern.matcher(input);

		if (!input.matches("(?i)^\\d+[psd](\\s*\\d+[psd])*\\s*$")) {
		    throw new IllegalArgumentException(
		        "Errore: input non valido. Usa numeri seguiti da 'p', 's' o 'd', separati opzionalmente da spazi.");
		}
		
		while (matcher.find()) {
			int value = Integer.parseInt(matcher.group(1));
			switch (matcher.group(2).toLowerCase()) {
			case "p":
				if (hasPounds)
					throw new IllegalArgumentException("Errore: valore in sterline duplicato.");
				sterline = value;
				hasPounds = true;
				break;
			case "s":
				if (hasShillings)
					throw new IllegalArgumentException("Errore: valore in scellini duplicato.");
				if (value >= 20)
					throw new IllegalArgumentException("Errore: massimo 19 scellini per sterlina.");
				scellini = value;
				hasShillings = true;
				break;
			case "d":
				if (hasPence)
					throw new IllegalArgumentException("Errore: valore in pence duplicato.");
				if (value >= 12)
					throw new IllegalArgumentException("Errore: massimo 11 pence per scellino.");
				pence = value;
				hasPence = true;
				break;
			}
		}

		return (sterline * PENCE_PER_STERLINE + scellini * PENCE_PER_SCELLINI + pence);

	}

	public static int calculate(Integer operando1, Integer operando2, String operatore) {
		return switch (operatore) {
		case "+" -> (operando1 + operando2);
		case "-" -> (operando1 - operando2);
		case "*" -> (operando1 * operando2);
		default -> throw new IllegalArgumentException("Operatore non valido: " + operatore);
		};
	}

	public static String[] extractOperatorAndOperands(String input) {
		String[] operandi = input.split("\\s*([+\\-*/])\\s*");
		String operatore = "";
		if (operandi.length != 2) {
			throw new IllegalArgumentException("Errore: input non valido. Assicurati di usare un solo operatore.");
		}
		Pattern pattern = Pattern.compile("[+\\-*/]");
		Matcher matcher = pattern.matcher(input);

		// Se trovo l'operatore, lo restituisco
		if (matcher.find()) {
			operatore = matcher.group();
		} else {
			throw new IllegalArgumentException("Errore: input non valido. Operatore assente");
		}
		// String operatore = input.replaceAll(".*?([+\\-*/]).*", "$1");
		return new String[] { operandi[0], operandi[1], operatore };
	}

	public static void main(String[] args) {

		Scanner scanner = new Scanner(System.in);

		while (true) {
			System.out.print("Inserisci l'operazione o exit per terminare: ");
			String input = scanner.nextLine().trim();
			// Se l'utente vuole uscire
			if (input.equalsIgnoreCase("exit")) {
				System.out.println("Operazione terminata.");
				break; // Esce dal ciclo
			}

			try {
				// Estraggo gli operandi e l'operatore
				String[] parsedData = extractOperatorAndOperands(input);
				String operando1Str = parsedData[0];
				String operando2Str = parsedData[1];
				String operatore = parsedData[2];

				int operando1 = parsePounds(operando1Str);
				int operando2;
				int pencesRisultanti;
				int restoDiv = 0;

				// Se l'operazione è una moltiplicazione o divisione il secondo operando
				// dev'essere un intero
				if (operatore.matches("[*/]"))
					try {
						operando2 = Integer.parseInt(operando2Str);
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException("Errore: il secondo operando deve essere un numero intero.");
					}
				else
					operando2 = parsePounds(operando2Str);

				if (operatore.equalsIgnoreCase("/")) {
					if (operando2 == 0)
						throw new ArithmeticException("Errore: non è possibile divisione per zero");

					pencesRisultanti = operando1 / operando2;
					restoDiv = operando1 % operando2;
				} else {
					pencesRisultanti = calculate(operando1, operando2, operatore);
				}

				StringBuilder output = new StringBuilder(input);
				output.append(" = ");
				if (pencesRisultanti < 0)
					output.append("-(").append(formatPoundsString(pencesRisultanti)).append(")");
				else {
					output.append(formatPoundsString(pencesRisultanti));
					if (restoDiv > 0) {
						output.append(" (").append(formatPoundsString(restoDiv)).append(")");
					}
				}
				System.out.println(output);

			} catch (IllegalArgumentException | ArithmeticException e) {
				System.err.println(e.getMessage());
			}
		}
		scanner.close();
	}
}
