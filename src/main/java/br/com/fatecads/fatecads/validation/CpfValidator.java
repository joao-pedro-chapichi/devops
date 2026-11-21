package br.com.fatecads.fatecads.validation;

public final class CpfValidator {
    private CpfValidator() { }

    public static boolean valido(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}") || cpf.chars().distinct().count() == 1) return false;
        for (int posicao = 9; posicao < 11; posicao++) {
            int soma = 0;
            for (int i = 0; i < posicao; i++) {
                soma += Character.digit(cpf.charAt(i), 10) * (posicao + 1 - i);
            }
            int digito = (soma * 10) % 11;
            if (digito == 10) digito = 0;
            if (digito != Character.digit(cpf.charAt(posicao), 10)) return false;
        }
        return true;
    }
}
