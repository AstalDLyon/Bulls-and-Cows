package bullscows;

import java.util.Random;
import java.util.Scanner;

public class Main {

    private static final String ALL_SYMBOLS = "0123456789abcdefghijklmnopqrstuvwxyz";

    /**
     * Coordena o fluxo completo do jogo com validação defensiva (sem try-catch):
     * lê e valida o tamanho do código e a quantidade de símbolos possíveis, gera o código
     * secreto e valida cada palpite durante os turnos até a vitória ou detecção de erro.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Input the length of the secret code:");
        if (!scanner.hasNextLine()) {
            return;
        }
        String lengthInput = scanner.nextLine().trim();

        if (!isValidPositiveInteger(lengthInput)) {
            System.out.printf("Error: \"%s\" isn't a valid number.%n", lengthInput);
            return;
        }
        int length = Integer.parseInt(lengthInput);

        if (length <= 0) {
            System.out.printf("Error: \"%s\" isn't a valid number.%n", lengthInput);
            return;
        }

        System.out.println("Input the number of possible symbols in the code:");
        if (!scanner.hasNextLine()) {
            return;
        }
        String symbolsInput = scanner.nextLine().trim();

        if (!isValidPositiveInteger(symbolsInput)) {
            System.out.printf("Error: \"%s\" isn't a valid number.%n", symbolsInput);
            return;
        }
        int possibleSymbols = Integer.parseInt(symbolsInput);

        if (possibleSymbols > ALL_SYMBOLS.length()) {
            System.out.println("Error: maximum number of possible symbols in the code is 36 (0-9, a-z).");
            return;
        }

        if (length > possibleSymbols) {
            System.out.printf("Error: it's not possible to generate a code with a length of %d with %d unique symbols.%n",
                    length, possibleSymbols);
            return;
        }

        String secretCode = generateSecretCode(length, possibleSymbols);
        String allowedSymbols = ALL_SYMBOLS.substring(0, possibleSymbols);
        printPreparedMessage(length, possibleSymbols);

        System.out.println("Okay, let's start a game!");

        int turn = 1;
        while (true) {
            System.out.printf("Turn %d:%n", turn);
            if (!scanner.hasNextLine()) {
                break;
            }

            String guess = scanner.nextLine().trim();

            if (!isValidGuess(guess, length, allowedSymbols)) {
                System.out.println("Error: invalid guess input.");
                return;
            }

            boolean isGuessed = gradeGuess(secretCode, guess);

            if (isGuessed) {
                System.out.println("Congratulations! You guessed the secret code.");
                break;
            }

            turn++;
        }
    }

    /**
     * Verifica sem uso de exceções se uma string representa um número inteiro positivo válido,
     * checando se não está vazia, se contém apenas dígitos e se cabe em um int.
     */
    public static boolean isValidPositiveInteger(String input) {
        if (input == null || !input.matches("\\d+")) {
            return false;
        }
        return input.length() <= 9;
    }

    /**
     * Valida o palpite do jogador garantindo que ele possua exatamente o tamanho esperado
     * e que contenha apenas caracteres pertencentes ao intervalo de símbolos permitidos.
     */
    public static boolean isValidGuess(String guess, int expectedLength, String allowedSymbols) {
        if (guess == null || guess.length() != expectedLength) {
            return false;
        }
        for (int i = 0; i < guess.length(); i++) {
            if (allowedSymbols.indexOf(guess.charAt(i)) == -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gera um código secreto de tamanho específico contendo caracteres únicos sorteados
     * dentro do limite de símbolos possíveis (0-9 e a-z) usando java.util.Random.
     */
    public static String generateSecretCode(int length, int possibleSymbols) {
        Random random = new Random();
        StringBuilder secret = new StringBuilder();

        while (secret.length() < length) {
            int index = random.nextInt(possibleSymbols);
            char symbol = ALL_SYMBOLS.charAt(index);

            if (secret.indexOf(String.valueOf(symbol)) == -1) {
                secret.append(symbol);
            }
        }

        return secret.toString();
    }

    /**
     * Formata e exibe a mensagem inicial informando o tamanho oculto por asteriscos (*)
     * e o intervalo exato de números e letras utilizados na geração do código.
     */
    public static void printPreparedMessage(int length, int possibleSymbols) {
        String stars = "*".repeat(length);
        String rangeDescription;

        if (possibleSymbols <= 10) {
            char maxDigit = ALL_SYMBOLS.charAt(possibleSymbols - 1);
            rangeDescription = String.format("(0-%c)", maxDigit);
        } else if (possibleSymbols == 11) {
            rangeDescription = "(0-9, a)";
        } else {
            char maxLetter = ALL_SYMBOLS.charAt(possibleSymbols - 1);
            rangeDescription = String.format("(0-9, a-%c)", maxLetter);
        }

        System.out.printf("The secret is prepared: %s %s.%n", stars, rangeDescription);
    }

    /**
     * Avalia o palpite do jogador comparando-o com o código secreto, exibe a pontuação
     * formatada no singular ou plural e retorna true se todos os bulls forem acertados.
     */
    public static boolean gradeGuess(String secret, String guess) {
        int bulls = 0;
        int cows = 0;

        for (int i = 0; i < secret.length(); i++) {
            char guessChar = guess.charAt(i);
            if (guessChar == secret.charAt(i)) {
                bulls++;
            } else if (secret.indexOf(guessChar) != -1) {
                cows++;
            }
        }

        String bullWord = (bulls == 1) ? "bull" : "bulls";
        String cowWord = (cows == 1) ? "cow" : "cows";

        if (bulls > 0 && cows > 0) {
            System.out.printf("Grade: %d %s and %d %s%n", bulls, bullWord, cows, cowWord);
        } else if (bulls > 0) {
            System.out.printf("Grade: %d %s%n", bulls, bullWord);
        } else if (cows > 0) {
            System.out.printf("Grade: %d %s%n", cows, cowWord);
        } else {
            System.out.println("Grade: None");
        }

        return bulls == secret.length();
    }
}
